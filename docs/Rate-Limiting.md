# Rate Limiting (US-SEC-02)

Public and sensitive endpoints are rate limited to prevent large-scale data
extraction and abuse. A client that exceeds a limit receives
`429 Too Many Requests` with a `Retry-After` header (seconds to wait) and a
JSON body with the same shape as the login errors (`LoginFailureEntryPoint`):

```json
{"status":429,"code":"TOO_MANY_REQUESTS","message":"Too many requests, please try again later"}
```

Limits reached inside Spring Data REST (reports) get the same body through
`RateLimitExceptionAdvice`.

## Limits

> **Proposed values, pending team approval** (see the "proposed implementation
> conventions" rule in [Context-FBI.md](Context-FBI.md)). Changing them only
> requires configuration.

| Rule           | Applies to                         | Counted per        | Limit | Window |
|----------------|------------------------------------|--------------------|-------|--------|
| `public`       | Every `GET` / `HEAD`               | IP                 | 120   | 60 s   |
| `registration` | `POST /users`                      | IP                 | 5     | 3600 s |
| `recovery`     | `POST /password-reset-*`           | IP                 | 10    | 900 s  |
| `reports`      | `POST /reports`                    | Authenticated user | 10    | 3600 s |

Failed logins are not limited here: `LoginThrottlingService` (US-AUT-02)
already locks an account for 15 minutes after 5 consecutive failures. The
existing per-email limit on password recovery (5 per 15 min) still applies on
top of the per-IP one.

## Changing the limits without redeploying

Each value can be overridden with an environment variable; no code change or
rebuild is needed:

| Rule           | Limit variable            | Window variable                  |
|----------------|---------------------------|----------------------------------|
| `public`       | `RATE_LIMIT_PUBLIC`       | `RATE_LIMIT_PUBLIC_WINDOW`       |
| `registration` | `RATE_LIMIT_REGISTRATION` | `RATE_LIMIT_REGISTRATION_WINDOW` |
| `recovery`     | `RATE_LIMIT_RECOVERY`     | `RATE_LIMIT_RECOVERY_WINDOW`     |
| `reports`      | `RATE_LIMIT_REPORTS`      | `RATE_LIMIT_REPORTS_WINDOW`      |

On Fly.io, `fly secrets set RATE_LIMIT_PUBLIC=200` restarts the app with the
new value. Defaults live in `src/main/resources/application.yml`.

## How it works

- `RateLimitFilter` runs inside the Spring Security chain, after CORS (so a
  429 still carries CORS headers) and before Basic authentication (so blocked
  requests never reach the password check).
- Reports are limited in `ReportEventHandler`, where the reporter is
  authenticated.
- `RateLimitService` keeps one `RateLimit` row per key in fixed windows,
  locking the row so concurrent requests are counted exactly. `RateLimit` is an
  internal entity: its repository is not exported through Spring Data REST.
- The client IP comes from `request.getRemoteAddr()`; on Fly.io this is the
  real client thanks to `SERVER_FORWARD_HEADERS_STRATEGY=NATIVE` in `fly.toml`.

## Known limitations

- Every read writes to the database. Acceptable at the current scale; if it
  becomes a bottleneck, the `public` rule can move to in-memory counters.
- Counter rows accumulate (one per IP / user) and are not purged yet.
- Many students behind the same university IP share the `public` counter;
  raise `RATE_LIMIT_PUBLIC` if that becomes a problem.

## Tests

`src/test/resources/features/RateLimiting.feature` (tag `@ratelimit`). Test
limits are set out of reach in `src/test/resources/application-test.yml`,
because every MockMvc request comes from `127.0.0.1`; each rate limiting
scenario lowers the limit it needs and restores it afterwards.
