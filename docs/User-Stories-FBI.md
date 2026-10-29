# User Stories — Test Platform (UdL)

# 1\. Common Features (Visitor, Student and Administrator)

## \[User Story\] US-COM-01 \- Browse subjects

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-01 - Browse subjects"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to browse the list of available subjects and open a subject page,  
so that I can see which subjects the platform covers and access their tests and leaderboard.

#### Context

- Problem: The role model lists "Browse the available subjects" as a visitor feature, but no user story covered it (US-COM-01 was missing).  
- Current behavior: Subjects are only reachable indirectly through search filters or leaderboards.  
- Expected behavior: A subjects list shows every active subject; each subject page shows its description, its published tests and a link to its leaderboard.

#### Acceptance Criteria

- [ ] Given I am on any page, when I open the Subjects section, then I see all active subjects with name, short description and number of published tests.  
- [ ] Given I am on the subjects list, when I select a subject, then I see its page with its public published tests (official and custom, clearly distinguished) and a link to its leaderboard.  
- [ ] Given I am a visitor, when I open a subject page, then only public information is shown and no draft, withdrawn or private tests appear.  
- [ ] Edge case: A subject with no published tests is still listed, and its page shows an empty-state message.  
- [ ] Edge case: Archived subjects (US-ADM-06) are not listed.

#### Scope

- In scope: Subjects list, subject detail page, links to tests and leaderboard.  
- Out of scope: Subject creation and editing (US-ADM-04, US-ADM-05); search filters (US-COM-04).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-COM-02 \- View the home page

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-02 - View the home page"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to view a home page with featured tests and a summary of the leaderboards,  
so that I can quickly identify the platform's main content and activity.

#### Context

- Problem: Users need an entry point that shows what the platform offers.  
- Current behavior: Not implemented.  
- Expected behavior: The home page shows featured tests, a top-5 summary of the global leaderboard and access to subjects and the catalog.

#### Acceptance Criteria

- [ ] Given I open the platform root URL, when the page loads, then I see featured tests with title, subject and type (official/custom).  
- [ ] Given the leaderboards have data, when the home page loads, then I see the top 5 users of the global leaderboard and a link to the full leaderboard.  
- [ ] Given I am a visitor, when I view the home page, then I see clear calls to action to register or log in.  
- [ ] Edge case: If no test is featured by an administrator (US-ADM-36), the most taken published tests of the last 30 days are shown instead.  
- [ ] Edge case: If the leaderboards are empty, an empty-state message is shown; administrator accounts never appear in the summary.

#### Scope

- In scope: Featured tests block, leaderboard summary, navigation to catalog and subjects.  
- Out of scope: Choosing featured tests (US-ADM-36); full leaderboards (US-COM-06, US-COM-07).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 5



## \[User Story\] US-COM-03 \- Explore the public test catalog

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-03 - Explore the public test catalog"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to explore the catalog of published, publicly visible tests,  
so that I can discover the available content.

#### Context

- Problem: Users need a single place to see all available tests.  
- Current behavior: Not implemented.  
- Expected behavior: A paginated catalog lists every published official test and every publicly published custom test (US-EST-38), with the type clearly indicated.

#### Acceptance Criteria

- [ ] Given there are published tests, when I open the catalog, then I see a paginated list with title, subject, type (official/custom), number of questions and year.  
- [ ] Given a test is in Draft or Withdrawn status, or is a privately published custom test, when anyone opens the catalog, then that test is not listed.  
- [ ] Given I select a test in the catalog, when I click it, then I am taken to its public detail page (US-COM-05).  
- [ ] Edge case: When the catalog is empty, an empty-state message is shown instead of an empty list.

#### Scope

- In scope: Catalog list, pagination, link to the test detail.  
- Out of scope: Search and filters (US-COM-04).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-COM-04 \- Search and filter tests

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-04 - Search and filter tests"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to search tests by keyword and filter them by subject, type, difficulty and tags, and sort them by popularity,  
so that I can quickly find the tests that interest me.

#### Context

- Problem: The role model only mentions filtering by subject, while this story also listed difficulty, tags and popularity, which no other story defined.  
- Current behavior: The catalog has no search, and difficulty and tags were not captured when a test is created.  
- Expected behavior: Keyword search on the title plus combinable filters. Difficulty and tags come from the test metadata (US-EST-07, US-ADM-08); popularity is the number of completed attempts.

#### Acceptance Criteria

- [ ] Given I am on the catalog, when I type a keyword, then only tests whose title contains it (case- and accent-insensitive) are shown.  
- [ ] Given I am on the catalog, when I combine filters (subject, type, difficulty, tags), then only tests matching all the selected filters are shown.  
- [ ] Given I am on the catalog, when I sort by popularity, then tests are ordered by number of completed attempts, descending.  
- [ ] Given I applied filters, when I reload or share the URL, then the same filters remain applied.  
- [ ] Edge case: When no test matches, a "no results" message is shown with an option to clear the filters.

#### Scope

- In scope: Keyword search, subject/type/difficulty/tag filters, popularity sort, filters kept in the URL.  
- Out of scope: Full-text search inside question content; personalized recommendations.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-COM-05 \- View test details

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-05 - View test details"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to view the public information of a test, such as title, subject, year, type, difficulty and number of questions,  
so that I can understand its characteristics before deciding whether to take it.

#### Context

- Problem: The story listed "title, number of questions and year" while the role model listed only "title and year"; the two documents disagreed.  
- Current behavior: The set of public fields is ambiguous and nothing explicitly prevents leaking answers.  
- Expected behavior: A detail page with a fixed list of public fields and a "Take test" action; questions and answers are never exposed before an attempt.

#### Acceptance Criteria

- [ ] Given a published test, when I open its detail page, then I see title, subject, year, type (official/custom), difficulty, tags, number of questions and, for custom tests, the author's public name.  
- [ ] Given I am a visitor, when I click "Take test", then I am told that an account is required (US-AUT-04).  
- [ ] Given any user, when the detail page or its API response is loaded, then no question text, answer options or correct answers are included.  
- [ ] Edge case: Opening the URL of a Draft or Withdrawn test returns a "test not available" page instead of its content.  
- [ ] Edge case: A privately published custom test is shown only to logged-in users who open its direct link (US-EST-38).

#### Scope

- In scope: Public detail page and its API.  
- Out of scope: Taking the test (US-EST-01, US-EST-12); test statistics (US-ADM-25).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-COM-06 \- View a subject leaderboard

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-06 - View a subject leaderboard"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to view the leaderboard of a subject,  
so that I can see participants' positions within that subject.

#### Context

- Problem: Students need to compare their performance per subject.  
- Current behavior: Not implemented.  
- Expected behavior: Each subject has a ranking built only from students' official test results.

#### Acceptance Criteria

- [ ] Given a subject with official results, when I open its leaderboard, then I see position, public name and subject score, ordered descending.  
- [ ] Given I am a student who appears in the leaderboard, when I open it, then my own row is highlighted even if it is outside the visible page.  
- [ ] Given the leaderboard is loaded, then only contributions from currently published official tests are included (US-COM-11); custom tests and administrator attempts never count.  
- [ ] Edge case: Ties share the same position and are ordered by who reached the score first (explained to users in US-COM-10).  
- [ ] Edge case: A subject without results shows an empty-state message.

#### Scope

- In scope: Subject leaderboard page, pagination, own-position highlight.  
- Out of scope: Score aggregation rules (US-EST-05); corrections (US-ADM-17).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-COM-07 \- View the global leaderboard

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-07 - View the global leaderboard"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to view the global leaderboard and filter it by period,  
so that I can see the highest-ranked users on the platform.

#### Context

- Problem: The story said "apply the available filters" without defining them.  
- Current behavior: Filters are undefined.  
- Expected behavior: A global ranking based on the global score (US-EST-06), filterable by leaderboard period (current period and closed periods).

#### Acceptance Criteria

- [ ] Given the global leaderboard has data, when I open it, then I see position, public name and global score for the current period.  
- [ ] Given closed periods exist, when I select a past period, then I see that leaderboard exactly as it was when the period was closed.  
- [ ] Given the leaderboard is loaded, then only contributions from currently published official tests are included (US-COM-11); administrator accounts and custom test results never count.  
- [ ] Edge case: Students who chose to appear anonymously (US-EST-46) are shown as "Anonymous student" and keep their position.

#### Scope

- In scope: Global leaderboard page, period filter, pagination.  
- Out of scope: Global score formula (US-EST-06); closing periods (US-ADM-18).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-COM-08 \- View a public profile

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-08 - View a public profile"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to view another student's public profile,  
so that I can see their public information, badges and the content they have published.

#### Context

- Problem: The story did not reflect that administrator accounts are not publicly discoverable, nor the user's own privacy choices.  
- Current behavior: Not implemented.  
- Expected behavior: A student's public profile shows public name, avatar, badges, leaderboard positions and published custom tests, respecting their privacy settings.

#### Acceptance Criteria

- [ ] Given a student with a public profile, when I open it, then I see public name, avatar, the earned badges they chose to show (US-EST-41), leaderboard positions and their public custom tests (US-EST-38).  
- [ ] Given a student with a private profile (US-EST-46), when I open it, then I only see the public name and a "This profile is private" message.  
- [ ] Given an administrator account, when anyone tries to open its public profile, then a "profile not found" page is returned.  
- [ ] Edge case: Full name and email are hidden unless the student opts in (US-EST-42); detailed results and test history are never shown, not even to other logged-in users.

#### Scope

- In scope: Public profile page and its API.  
- Out of scope: Editing my own profile (US-EST-30).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-COM-09 \- View platform information

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-09 - View platform information"
labels: ["user-story", "enhancement", "documentation", "good first issue"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to access the How It Works, Terms of Use, Privacy Policy and Report a Problem pages,  
so that I can understand how the platform works and how my data is handled.

#### Context

- Problem: The story listed "Report Problem" among public pages, but the role model says visitors cannot report.  
- Current behavior: Not implemented.  
- Expected behavior: Information pages are public and linked from the footer; the Report a Problem page explains the process and requires login to submit.

#### Acceptance Criteria

- [ ] Given any page, when I look at the footer, then I see links to How It Works, Terms of Use, Privacy Policy and Report a Problem.  
- [ ] Given I open How It Works, then it explains official vs custom tests, scoring, leaderboards and badges, and links to the ranking criteria (US-COM-10).  
- [ ] Given I am a visitor, when I open Report a Problem, then I see the explanation and a prompt to log in before submitting (US-AUT-04).  
- [ ] Edge case: All pages are readable on mobile screens.

#### Scope

- In scope: Static information pages and footer links.  
- Out of scope: The reporting forms themselves (US-EST-32, US-EST-33).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 2



## \[User Story\] US-COM-10 \- Understand ranking criteria

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-10 - Understand ranking criteria"
labels: ["user-story", "enhancement", "documentation", "question"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to see how subject and global scores are calculated and how ties are broken,  
so that I can interpret leaderboard positions consistently.

#### Context

- Problem: Leaderboards are hard to trust if users do not know how positions are computed.  
- Current behavior: The rules are spread across US-EST-05, US-EST-06 and US-COM-06 and are not shown to users.  
- Expected behavior: A "Ranking criteria" page, linked from every leaderboard and from How It Works, explaining eligibility, which attempt counts, the subject and global formulas and the tie-break rule.

#### Acceptance Criteria

- [ ] Given any leaderboard page, when I click "How rankings work", then I see the ranking criteria page.  
- [ ] Given the page, then it explains that only currently published official tests count (US-COM-11), which attempt counts (US-EST-05), how subject and global totals are calculated (US-EST-05, US-EST-06) and that custom tests and administrator attempts never count.  
- [ ] Given the page, then it states the tie-break rule: equal scores share the position and are ordered by who reached the score first.  
- [ ] Edge case: When the scoring rules change, the page is updated in the same release and shows the date of the last change.

#### Scope

- In scope: Ranking criteria page and links from leaderboards and How It Works.  
- Out of scope: The calculation itself (US-EST-05, US-EST-06).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



## \[User Story\] US-COM-11 \- View only current eligible rankings

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-COM-11 - View only current eligible rankings"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a platform user (visitor, student or administrator),  
I want to see rankings calculated only from currently published official tests,  
so that withdrawn tests no longer influence competitive positions.

#### Context

- Problem: A test withdrawn because of errors would otherwise keep affecting positions.  
- Current behavior: Earlier versions of US-ADM-13, US-EST-05 and US-EST-01 kept withdrawn tests' scores in the leaderboards; this story replaces that rule.  
- Expected behavior: Active subject and global leaderboards only include contributions from official tests whose status is Published. Attempt records are kept, and their contributions return if the test is republished (US-ADM-30).

#### Acceptance Criteria

- [ ] Given an official test is withdrawn, when I view the subject or global leaderboard, then no score from that test is included.  
- [ ] Given the test is republished, when I view the leaderboards, then the contributions of its eligible attempts are included again.  
- [ ] Given custom tests (public or private) or administrator attempts, then they are never included.  
- [ ] Edge case: Snapshots of closed periods (US-ADM-18) are not changed by later withdrawals.

#### Scope

- In scope: Eligibility rule applied to the active leaderboards.  
- Out of scope: The recalculation mechanism (US-ADM-30).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



# 2\. Authentication and Access

## \[User Story\] US-AUT-01 \- Register

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-AUT-01 - Register"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a visitor,  
I want to create an account from the public area of the platform,  
so that I can access the features available to registered students.

#### Context

- Problem: Visitors need a way to become students.  
- Current behavior: Not implemented.  
- Expected behavior: A registration form with name, public name, email and password that creates a Student account pending email verification.

#### Acceptance Criteria

- [ ] Given I am a visitor, when I submit the form with valid data and accept the Terms of Use and Privacy Policy, then a Student account is created and a verification email is sent (US-AUT-07).  
- [ ] Given the email is already registered, when I submit the form, then I am told it cannot be used and I am offered to log in or recover my password.  
- [ ] Given the password does not meet the policy (e.g. fewer than 8 characters), when I submit, then the form shows which rule is not met.  
- [ ] Given any registration request, then the new account always gets the Student role; the role cannot be chosen or sent from the client.  
- [ ] Edge case: Public names must be unique; if the chosen one is taken, the form asks for another.

#### Scope

- In scope: Registration form, validation, role assignment, acceptance of terms.  
- Out of scope: Email verification (US-AUT-07); social login.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Critical  
- Story Points: 5



## \[User Story\] US-AUT-02 \- Log in

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-AUT-02 - Log in"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a visitor with an existing account,  
I want to log in,  
so that I can access my features and personal data.

#### Context

- Problem: Registered users need to authenticate.  
- Current behavior: Not implemented.  
- Expected behavior: Login with email and password, protected against brute-force attempts.

#### Acceptance Criteria

- [ ] Given valid credentials of a verified account, when I log in, then a session is created and I see the features of my role.  
- [ ] Given invalid credentials, when I log in, then I see a generic "incorrect email or password" message.  
- [ ] Given 5 consecutive failed attempts, when I try again, then new attempts are temporarily throttled.  
- [ ] Edge case: An unverified account is asked to verify its email and can resend the link (US-AUT-07).  
- [ ] Edge case: A suspended account (US-ADM-33) cannot log in and sees a message saying the account is suspended.

#### Scope

- In scope: Login form, session creation, throttling.  
- Out of scope: Two-factor authentication.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Critical  
- Story Points: 3



## \[User Story\] US-AUT-03 \- Recover a password

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-AUT-03 - Recover a password"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a registered user (student or administrator) who does not remember their password,  
I want to start the password recovery process,  
so that I can regain access to my account without losing my data.

#### Context

- Problem: Users who forget their password lose access to their account.  
- Current behavior: Not implemented.  
- Expected behavior: The user requests a reset link by email; the link is single-use and expires.

#### Acceptance Criteria

- [ ] Given I enter an email on the recovery page, when I submit, then I always see the same confirmation message, whether or not the account exists.  
- [ ] Given a valid reset link, when I set a new password that meets the policy, then the password is changed and my other active sessions are closed.  
- [ ] Given a reset link older than 1 hour or already used, when I open it, then I am told it has expired and can request a new one.  
- [ ] Edge case: Repeated reset requests in a short time are rate-limited.

#### Scope

- In scope: Recovery request, email link, reset form.  
- Out of scope: Changing the password while logged in (US-AUT-08).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-AUT-04 \- Receive information about a restricted action

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-AUT-04 - Receive information about a restricted action"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a visitor,  
I want to receive a clear explanation when I try to perform an action that requires an account,  
so that I understand what I need to do to continue.

#### Context

- Problem: Visitors trying to take, create or report tests must not hit a dead end.  
- Current behavior: Not implemented.  
- Expected behavior: A message explaining that an account is required, with direct buttons to log in or register.

#### Acceptance Criteria

- [ ] Given I am a visitor, when I click "Take test", "Create test" or "Report", then I see a message explaining that an account is required, with "Log in" and "Register" buttons.  
- [ ] Given I am a visitor, when I open a restricted URL directly, then I am redirected to the login page with the same explanation.  
- [ ] Given a restricted API endpoint, when it is called without authentication, then it returns 401 and no data.  
- [ ] Edge case: An authenticated student who opens an administrator page gets a "not authorized" page (403), not a login prompt.

#### Scope

- In scope: Restricted-action messages in the UI and API responses.  
- Out of scope: Returning to the original action (US-AUT-05).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 2



## \[User Story\] US-AUT-05 \- Return to the previous point after authentication

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-AUT-05 - Return to the previous point after authentication"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a visitor who has just logged in or registered,  
I want to return to the page or action from which I started authentication,  
so that I can continue what I was doing without searching for the content again.

#### Context

- Problem: Losing context after login is frustrating and discourages registration.  
- Current behavior: Not implemented.  
- Expected behavior: The original destination is kept during login or registration and the user is sent back there.

#### Acceptance Criteria

- [ ] Given I clicked "Take test" as a visitor, when I log in, then I return to that test's detail page ready to start.  
- [ ] Given I registered from a restricted action, when I verify my email and log in, then I am taken back to the original page.  
- [ ] Given the return URL points to an external domain, when I log in, then it is ignored and I am sent to the home page (no open redirect).  
- [ ] Edge case: If the original content is no longer available, I land on the home page with a notice.

#### Scope

- In scope: Return-URL handling for login and registration.  
- Out of scope: Restoring unsaved form data.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-AUT-06 \- Log out

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-AUT-06 - Log out"
labels: ["user-story", "enhancement", "good first issue"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want to log out of my account,  
so that I can protect my information when I stop using the platform.

#### Context

- Problem: Users on shared devices need to end their session.  
- Current behavior: Not implemented.  
- Expected behavior: A visible log-out action that invalidates the session on the server.

#### Acceptance Criteria

- [ ] Given I am logged in, when I click "Log out", then my session is invalidated on the server and I am taken to the home page as a visitor.  
- [ ] Given I have logged out, when I press the browser's back button, then private pages are not shown from the cache.  
- [ ] Edge case: If I log out while taking a test, the attempt stays in progress and can be resumed later (US-EST-45).

#### Scope

- In scope: Log-out action and server-side session invalidation.  
- Out of scope: "Log out of all devices".

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 1



## \[User Story\] US-AUT-07 \- Verify my email address

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-AUT-07 - Verify my email address"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As a newly registered student,  
I want to confirm my email address through a link,  
so that the platform knows the account belongs to me and I can recover it if needed.

#### Context

- Problem: Without verification, accounts can be created with someone else's email and password recovery is unreliable.  
- Current behavior: Not defined.  
- Expected behavior: A verification email with a time-limited link; unverified accounts cannot log in.

#### Acceptance Criteria

- [ ] Given I have just registered, when I open the verification link, then my account becomes verified and I can log in.  
- [ ] Given my account is unverified, when I try to log in, then I am asked to verify my email and can resend the link.  
- [ ] Given the link has expired (24 hours), when I open it, then I can request a new one.  
- [ ] Edge case: Unverified accounts older than 7 days are deleted automatically.

#### Scope

- In scope: Verification email, verification endpoint, resend option.  
- Out of scope: Restricting registration to university email domains. Open question: only @udl.cat addresses?

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-AUT-08 \- Change my password

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-AUT-08 - Change my password"
labels: ["user-story", "enhancement", "good first issue"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want to change my password from my account settings,  
so that I can keep my account secure.

#### Context

- Problem: Users could only change their password through the recovery flow.  
- Current behavior: Not available.  
- Expected behavior: A form that asks for the current password and the new one.

#### Acceptance Criteria

- [ ] Given I am logged in, when I enter my current password and a valid new password, then the password is updated and I receive an email notification.  
- [ ] Given I enter a wrong current password, when I submit, then the change is rejected.  
- [ ] Given my password was changed, then all my other sessions are closed.  
- [ ] Edge case: The new password cannot be the same as the current one.

#### Scope

- In scope: Change-password form and email notification.  
- Out of scope: Password recovery (US-AUT-03).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



# 3.3. Taking Tests and Results

## \[User Story\] US-EST-01 \- Take an official test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-01 - Take an official test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to take official tests for different subjects,  
so that I can assess my knowledge and obtain a competitive score.

#### Context

- Problem: This is the core feature of the platform.  
- Current behavior: Not implemented.  
- Expected behavior: A student starts a published official test, answers its questions and submits it; the attempt is graded on the server.

#### Acceptance Criteria

- [ ] Given a published official test, when I click "Start", then an attempt is created and I see the questions with their answer options, without the correct answers.  
- [ ] Given an attempt in progress, when I select or change an answer, then it is saved on the server automatically.  
- [ ] Given I have answered the questions, when I submit and confirm (US-EST-02), then the attempt is graded and I see my results (US-EST-03).  
- [ ] Given the attempt data sent to the browser, then it never contains correct answers or per-option scores.  
- [ ] Edge case: If the test is withdrawn while I am taking it, I can still submit; the attempt is stored and contributes to the leaderboards only if the test is republished (US-COM-11, US-ADM-30).

#### Scope

- In scope: Starting an attempt, answering, autosave, submission.  
- Out of scope: Resuming an unfinished attempt (US-EST-45); time limits.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Critical  
- Story Points: 8



## \[User Story\] US-EST-02 \- Confirm test submission

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-02 - Confirm test submission"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a user taking a test (student or administrator),  
I want to confirm the submission before my answers are graded definitively,  
so that I avoid accidental or premature submissions.

#### Context

- Problem: An accidental click could end an attempt that, for official tests, may count toward the leaderboard.  
- Current behavior: Not implemented.  
- Expected behavior: A confirmation dialog shows a summary before final grading.

#### Acceptance Criteria

- [ ] Given I click "Submit", then a confirmation dialog shows how many questions are answered and unanswered.  
- [ ] Given the dialog is open, when I cancel, then I return to the test with my answers intact.  
- [ ] Given the dialog is open, when I confirm, then the attempt is graded and can no longer be modified.  
- [ ] Edge case: Double-clicking "Confirm" or resending the request produces only one graded attempt.

#### Scope

- In scope: Confirmation dialog and idempotent submission.  
- Out of scope: Results page (US-EST-03).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 2



## \[User Story\] US-EST-03 \- View test results

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-03 - View test results"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a user who has completed a test (student or administrator),  
I want to view my score, correct answers and mistakes,  
so that I can understand my performance and identify the areas I need to improve.

#### Context

- Problem: Students need feedback to learn from each attempt.  
- Current behavior: Not implemented.  
- Expected behavior: A results summary with the overall score and counts, linking to the question-by-question review (US-EST-44).

#### Acceptance Criteria

- [ ] Given I confirmed my submission, when the results load, then I see my score, the maximum score and the number of correct, incorrect and unanswered questions.  
- [ ] Given the results page, when I click "Review answers", then I open the question-by-question review (US-EST-44).  
- [ ] Given a completed attempt, when I open it later from my history (US-EST-24), then I see the same results.  
- [ ] Edge case: Another user cannot see my results by changing the attempt ID in the URL (403).

#### Scope

- In scope: Results summary.  
- Out of scope: Explanations per question.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-EST-04 \- Calculate and store scores automatically

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-04 - Calculate and store scores automatically"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want the system to calculate and store my test scores automatically,  
so that my results are reliable and cannot be manipulated.

#### Context

- Problem: Scores must be trustworthy, and the role model states that a recorded score must not change when the scoring criteria change later.  
- Current behavior: The story was written from the system's point of view, its roles line contradicted the persona, and score immutability was missing.  
- Expected behavior: Grading happens only on the server, using the version of the test that was taken; the score is stored with the attempt and later edits never recalculate it.

#### Acceptance Criteria

- [ ] Given a submitted attempt, when it is graded, then the score is computed on the server from the stored answers and the question scores of the version taken.  
- [ ] Given a stored score, when the test is edited later (scores, correct answers), then the stored score does not change.  
- [ ] Given a client request that includes a score or correct-answer data, then the server ignores that data.  
- [ ] Edge case: Only an administrator correction (US-ADM-17) can change a recorded official score, and it is recorded in the audit log (US-ADM-35).

#### Scope

- In scope: Server-side grading, storage and immutability of scores, for official and custom tests.  
- Out of scope: Leaderboard aggregation (US-EST-05, US-EST-06).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Critical  
- Story Points: 5



## \[User Story\] US-EST-05 \- Participate in the subject leaderboard

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-05 - Participate in the subject leaderboard"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want my official test results to count toward the corresponding subject leaderboard,  
so that I can compare my performance with other students.

#### Context

- Problem: The rule for turning attempts into a subject score was not defined.  
- Current behavior: Undefined.  
- Expected behavior: Proposed rule: only the first completed attempt of each official test counts, and the subject score is the sum of those scores for the subject's currently published official tests (US-COM-11).

#### Acceptance Criteria

- [ ] Given I complete an official test for the first time, when it is graded, then my subject score and position are updated.  
- [ ] Given I retake an official test, when it is graded, then the subject leaderboard does not change (US-EST-49).  
- [ ] Given a custom test result, then it never affects any leaderboard.  
- [ ] Edge case: If the test is withdrawn, its contribution is removed from the active leaderboard and restored if it is republished (US-COM-11, US-ADM-30); the attempt itself is kept (US-EST-37).

#### Scope

- In scope: Subject score aggregation and update after grading.  
- Out of scope: Global score (US-EST-06).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-EST-06 \- Participate in the global leaderboard

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-06 - Participate in the global leaderboard"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want my official results to contribute to my global score,  
so that I can compare my overall progress with other students.

#### Context

- Problem: The global score formula was not defined.  
- Current behavior: Undefined.  
- Expected behavior: Proposed formula: global score \= sum of the student's subject scores in the current period, counting only currently published official tests (US-COM-11).

#### Acceptance Criteria

- [ ] Given my subject score changes, when the change is stored, then my global score and global position are updated.  
- [ ] Given a period is closed (US-ADM-18), then a new period starts with every student at zero and the closed period remains viewable (US-COM-07).  
- [ ] Given a custom test or administrator result, then it never affects the global score.  
- [ ] Edge case: Students with no official attempts in the period do not appear in the global leaderboard.

#### Scope

- In scope: Global score calculation and update.  
- Out of scope: Subject score rules (US-EST-05).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-EST-44 \- Review unanswered questions after submission

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-44 - Review unanswered questions after submission"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a user who submitted a test (student or administrator),  
I want to see which questions I answered correctly, incorrectly or left unanswered,  
so that I understand my final result, including questions worth zero points.

#### Context

- Problem: The total score alone does not show which questions were missed or skipped.  
- Current behavior: The question-by-question review was part of US-EST-03; it now lives here.  
- Expected behavior: A question-by-question review where each question is marked Correct, Incorrect or Unanswered, with the points obtained.

#### Acceptance Criteria

- [ ] Given my submitted attempt, when I open the review, then each question shows my answer (or "Unanswered"), the correct answer and the points obtained.  
- [ ] Given a question worth zero points, then it is marked "0 points – not scored" and still shows whether my answer was correct.  
- [ ] Given the review, when I filter by Correct, Incorrect or Unanswered, then only those questions are shown.  
- [ ] Edge case: The review shows the version of the test I took, even if the test was edited later (US-ADM-31).

#### Scope

- In scope: Question-by-question review after submission.  
- Out of scope: Results summary (US-EST-03).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



# 3.4. Custom Tests and Student-Created Content

## \[User Story\] US-EST-07 \- Create a custom test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-07 - Create a custom test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want to create a custom test associated with a subject,  
so that I can practice with content adapted to my needs.

#### Context

- Problem: The search filters (difficulty, tags) need metadata that the creation story did not capture.  
- Current behavior: Only the link to a subject was defined.  
- Expected behavior: A creation form with title, subject, description, difficulty and tags; the test is created as a draft.

#### Acceptance Criteria

- [ ] Given I am logged in, when I submit the form with a title and a subject, then a custom test is created in Draft status with me as its author.  
- [ ] Given the creation form, then I can set a difficulty (Easy, Medium or Hard) and up to 5 tags.  
- [ ] Given a new custom test, then nobody else can see it until it is published (US-EST-13).  
- [ ] Edge case: Only active subjects can be selected.

#### Scope

- In scope: Creation form and test metadata.  
- Out of scope: Adding questions (US-EST-14); publishing (US-EST-13).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-EST-08 \- Edit my own custom test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-08 - Edit my own custom test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a custom test,  
I want to edit the content of my test,  
so that I can correct or improve it.

#### Context

- Problem: Edits to a published test must not alter results other users already obtained.  
- Current behavior: Not implemented.  
- Expected behavior: The author can edit metadata and questions; past attempts keep the version they were taken on.

#### Acceptance Criteria

- [ ] Given I am the author, when I edit the title, description, difficulty, tags or questions, then the changes are saved.  
- [ ] Given a published test with attempts, when I save changes, then past attempts keep their original questions and scores (US-ADM-31).  
- [ ] Given I am not the author, when I try to edit the test through the UI or the API, then I get a 403 response.  
- [ ] Edge case: If my changes to a published test would break the publication requirements (US-EST-23), the save is rejected and the failing requirements are listed.

#### Scope

- In scope: Editing metadata and questions of my own tests.  
- Out of scope: Moderation by administrators (US-ADM-21).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-09 \- Delete my own custom test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-09 - Delete my own custom test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a custom test,  
I want to delete a test I created,  
so that I can remove content I no longer need.

#### Context

- Problem: Deleting a test must not destroy stored results.  
- Current behavior: Not implemented.  
- Expected behavior: Deletion with confirmation, only for tests nobody has attempted; tests with attempts can be made private instead (US-EST-40).

#### Acceptance Criteria

- [ ] Given I am the author, when I click "Delete", then a confirmation dialog warns that the action cannot be undone.  
- [ ] Given I confirm, then the test disappears from the catalog, search and my list of tests.  
- [ ] Given the test has at least one attempt, when I try to delete it, then deletion is blocked and I am offered to make it private (US-EST-40).  
- [ ] Edge case: A user who is not the author gets a 403 response.

#### Scope

- In scope: Deletion with confirmation.  
- Out of scope: Changing visibility (US-EST-38).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-10 \- Save a test as a draft

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-10 - Save a test as a draft"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test,  
I want to save a test as a draft,  
so that I can keep working on it before making it available to other users.

#### Context

- Problem: Authors need to build tests over several sessions.  
- Current behavior: Not implemented.  
- Expected behavior: Drafts can be saved incomplete and are visible only to their author.

#### Acceptance Criteria

- [ ] Given I am editing a test, when I click "Save draft", then my changes are saved without checking the publication requirements.  
- [ ] Given a draft, then only I can see it.  
- [ ] Given a draft, when I come back later, then I can continue where I left off.  
- [ ] Edge case: Drafts may be incomplete, for example questions without a correct answer.

#### Scope

- In scope: Saving and reopening drafts.  
- Out of scope: Publishing (US-EST-13, US-ADM-12).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



## \[User Story\] US-EST-11 \- View the status of my tests

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-11 - View the status of my tests"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a custom test (student or administrator),  
I want to see whether each of my tests is Draft, Published (public or private) or Withdrawn by moderation,  
so that I can manage its availability.

#### Context

- Problem: The roles line was missing, and neither the visibility of published tests (US-EST-38) nor the moderation status (US-ADM-21) was listed.  
- Current behavior: Not implemented.  
- Expected behavior: A "My tests" list with a status badge on each test.

#### Acceptance Criteria

- [ ] Given I open "My tests", then each test shows its status.  
- [ ] Given I filter by status, then only tests in that status are shown.  
- [ ] Given a test withdrawn by an administrator for moderation, then it shows "Withdrawn (moderation)" together with the reason.  
- [ ] Edge case: Allowed transitions: Draft → Published; Public ↔ Private while published (US-EST-38); Withdrawn (moderation) is set only by an administrator and is final for the author.

#### Scope

- In scope: Status display, status filter, transitions for custom tests.  
- Out of scope: Status of official tests (US-ADM-14).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



## \[User Story\] US-EST-12 \- Practice with custom tests

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-12 - Practice with custom tests"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want to take a custom test created by me or by another user,  
so that I can practice and assess my knowledge without affecting competitive leaderboards.

#### Context

- Problem: Students want to practice with community content without affecting rankings.  
- Current behavior: Not implemented.  
- Expected behavior: Custom tests are taken like official ones but their results are stored as practice.

#### Acceptance Criteria

- [ ] Given a public custom test, or a private one whose link I have (US-EST-38), when I take and submit it, then I see my results as with an official test.  
- [ ] Given a practice result, then it is stored in my history marked as "Practice" and never counts toward any leaderboard.  
- [ ] Given one of my own unpublished tests, then I can only try it through the preview (US-EST-21), and that attempt is not stored in my history.  
- [ ] Edge case: Custom tests can be retaken an unlimited number of times; each attempt is stored in my history.

#### Scope

- In scope: Taking custom tests and storing practice results.  
- Out of scope: Competitive scoring (US-EST-05).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-EST-13 \- Publish my own custom test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-13 - Publish my own custom test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a custom test,  
I want to publish my test when it meets the platform's publication requirements,  
so that I can share practice content with the community.

#### Context

- Problem: The story referred to publication requirements that were never defined.  
- Current behavior: Requirements undefined.  
- Expected behavior: Publishing runs the checks of US-EST-23; if they pass, the test becomes Published and appears in the catalog.

#### Acceptance Criteria

- [ ] Given my draft passes every requirement, when I click "Publish", then its status becomes Published and, if I chose Public visibility (US-EST-38), it appears in the catalog and search.  
- [ ] Given my draft fails a requirement, when I click "Publish", then I see the failing requirements (US-EST-39) and the test stays in Draft.  
- [ ] Given a published custom test, then the author's public name is shown on it.  
- [ ] Edge case: A test withdrawn by moderation cannot be republished by its author.

#### Scope

- In scope: Publishing custom tests.  
- Out of scope: Definition of the requirements (US-EST-23).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-EST-38 \- Set the visibility of my custom test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-38 - Set the visibility of my custom test"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As the author of a custom test (student or administrator),  
I want to choose whether my published test is public or private,  
so that I control who can discover and take my practice content.

#### Context

- Problem: Authors want to share some tests only with specific people, or hide a test without deleting it.  
- Current behavior: My earlier "Withdraw my published custom test" story covered hiding; this visibility setting replaces it.  
- Expected behavior: A published custom test is Public (listed in catalog, search, subject page and author's profile) or Private (not listed; can be taken only by logged-in users who have its direct link).

#### Acceptance Criteria

- [ ] Given I publish a custom test, then I choose Public or Private (default: Public).  
- [ ] Given a public test, when I switch it to Private, then it disappears from catalog, search, subject pages and my public profile, but users with the link can still take it.  
- [ ] Given a private test, when I switch it to Public, then it appears in those places again.  
- [ ] Edge case: A test withdrawn by moderation (US-ADM-21) cannot be changed by its author.  
- [ ] Edge case: Changing visibility never affects stored attempts.

#### Scope

- In scope: Visibility setting for published custom tests.  
- Out of scope: Official tests, which are always public once published. Open question: should "private" mean link-only (proposed) or visible only to the author?

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-40 \- Remove my unattempted custom test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-40 - Remove my unattempted custom test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a custom test (student or administrator),  
I want to delete a test nobody has attempted, or make an attempted test private instead,  
so that I can manage my content without destroying stored results.

#### Context

- Problem: Deleting a test that has attempts would destroy results stored in other users' histories.  
- Current behavior: An earlier version of US-EST-09 allowed deleting tests with attempts; this rule replaces it.  
- Expected behavior: Deletion is allowed only when the test has no attempts; otherwise the author is offered to make it private (US-EST-38).

#### Acceptance Criteria

- [ ] Given my test has no attempts, when I delete it (US-EST-09), then it is permanently deleted.  
- [ ] Given my test has at least one attempt, when I try to delete it, then deletion is blocked, I am told how many attempts exist and I am offered to make it private.  
- [ ] Given a delete request, then the server enforces this check, not only the interface.  
- [ ] Edge case: Previews (US-EST-21) do not count as attempts; attempts in progress do.

#### Scope

- In scope: Attempt check before deletion and the private alternative.  
- Out of scope: Account deletion (US-EST-48).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



# 3.5. Question and Answer Management

## \[User Story\] US-EST-14 \- Add a question

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-14 - Add a question"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test (a student for custom tests, an administrator for official tests),  
I want to add questions to a test,  
so that I can build its content.

#### Context

- Problem: Tests need questions.  
- Current behavior: Not implemented.  
- Expected behavior: Authors add single-choice questions to their tests.

#### Acceptance Criteria

- [ ] Given a test I am editing, when I add a question with its statement, then it is added at the end of the test.  
- [ ] Given a new question, then it starts with a default score of 1 point and no answer options.  
- [ ] Given an empty statement, when I save, then I get a validation error.  
- [ ] Edge case: A test can have at most 100 questions (configurable limit).

#### Scope

- In scope: Single-choice questions with text.  
- Out of scope: Images, multiple correct answers and open-answer questions.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-EST-15 \- Edit a question

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-15 - Edit a question"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test,  
I want to modify a question in a test,  
so that I can correct or update its content.

#### Context

- Problem: Questions may contain errors.  
- Current behavior: Not implemented.  
- Expected behavior: Authors edit questions without affecting attempts already made.

#### Acceptance Criteria

- [ ] Given a question, when I change its statement, options, correct answer or score, then the change is saved.  
- [ ] Given the test has recorded attempts, then past attempts keep the original version of the question (US-ADM-31).  
- [ ] Given I am not the author (or, for official tests, not an administrator), when I try to edit, then I get a 403 response.  
- [ ] Edge case: If two administrators edit the same official test at once, the second save warns about the conflict instead of silently overwriting.

#### Scope

- In scope: Editing questions.  
- Out of scope: Regrading existing attempts (US-ADM-17).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 2



## \[User Story\] US-EST-16 \- Delete a question

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-16 - Delete a question"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test,  
I want to delete a question from a test,  
so that I can remove incorrect or inappropriate questions.

#### Context

- Problem: Wrong questions must be removable.  
- Current behavior: Not implemented.  
- Expected behavior: Deletion with confirmation; question numbering updates.

#### Acceptance Criteria

- [ ] Given a question, when I delete it and confirm, then it is removed and the remaining questions are renumbered.  
- [ ] Given the test has recorded attempts, then past attempts are not affected.  
- [ ] Given I am not authorized, when I try to delete, then I get a 403 response.  
- [ ] Edge case: Deleting a question from a published test is blocked if the test would no longer meet the publication requirements (US-EST-23).

#### Scope

- In scope: Deleting questions.  
- Out of scope: Annulling a question in recorded results (US-ADM-17).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



## \[User Story\] US-EST-17 \- Define possible answers

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-17 - Define possible answers"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test,  
I want to define the possible answers to a question,  
so that users can select an answer while taking the test.

#### Context

- Problem: Questions need answer options.  
- Current behavior: Not implemented.  
- Expected behavior: Each question has between 2 and 6 answer options.

#### Acceptance Criteria

- [ ] Given a question, when I add, edit or remove answer options, then the changes are saved.  
- [ ] Given a question, then it must have between 2 and 6 options to be publishable.  
- [ ] Given a test being taken, then the options are shown in the order the author defined.  
- [ ] Edge case: Two identical options in the same question are flagged as a validation error.

#### Scope

- In scope: Managing answer options.  
- Out of scope: Shuffling options.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-EST-18 \- Define the correct answer

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-18 - Define the correct answer"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test,  
I want to specify the correct answer to a question,  
so that the system can grade the test automatically.

#### Context

- Problem: Automatic grading needs an answer key.  
- Current behavior: Not implemented.  
- Expected behavior: Exactly one option per question is marked as correct.

#### Acceptance Criteria

- [ ] Given a question, when I mark an option as correct, then any previously marked option is unmarked.  
- [ ] Given the correct answer, then it is stored on the server and never sent to a user taking the test before submission.  
- [ ] Given a published test, then every question has exactly one correct answer.  
- [ ] Edge case: A draft may have questions without a correct answer, but it cannot be published (US-EST-23).

#### Scope

- In scope: Marking the correct option.  
- Out of scope: Multiple correct answers.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 2



## \[User Story\] US-EST-19 \- Define question scoring

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-19 - Define question scoring"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As the author of a test,  
I want to assign a score to each question,  
so that the system can calculate the final result correctly.

#### Context

- Problem: Questions may have different weights.  
- Current behavior: Not implemented.  
- Expected behavior: Each question has a score of 0 or more (0 \= not scored); the maximum score of the test is the sum.

#### Acceptance Criteria

- [ ] Given a question, when I set its score, then only values of 0 or more are accepted (default 1).  
- [ ] Given the editor, then the test's maximum score is shown and updated as I change scores.  
- [ ] Given a negative value, when I save, then I get a validation error; a zero-point question is shown as "not scored".  
- [ ] Edge case: Penalties for wrong answers are not supported in this story.

#### Scope

- In scope: Score per question and maximum score.  
- Out of scope: Negative marking. Open question: many university exams subtract points for wrong answers; is that needed?

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



## \[User Story\] US-EST-20 \- Reorder questions

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-20 - Reorder questions"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test,  
I want to change the order of the questions in a test,  
so that I can organize them in the most appropriate way.

#### Context

- Problem: Authors want to control the sequence of questions.  
- Current behavior: Not implemented.  
- Expected behavior: Questions can be reordered and the new order is saved.

#### Acceptance Criteria

- [ ] Given the editor, when I drag a question or use the up/down controls, then its position changes.  
- [ ] Given I save, then the new order is stored and users taking the test see it.  
- [ ] Given the test has recorded attempts, then past attempts keep the order they were taken in.  
- [ ] Edge case: Reordering is possible with the keyboard only (accessibility).

#### Scope

- In scope: Reordering questions.  
- Out of scope: Random question order.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 3



# 3.6. Test Validation

## \[User Story\] US-EST-21 \- Preview a test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-21 - Preview a test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test (student or administrator),  
I want to preview my test before publishing it,  
so that I can check how other users will see it.

#### Context

- Problem: Authors need to see the test as a user would before publishing.  
- Current behavior: Not implemented.  
- Expected behavior: A preview mode that looks exactly like taking the test but stores nothing.

#### Acceptance Criteria

- [ ] Given my test, when I open the preview, then I see it exactly as a user taking it would.  
- [ ] Given the preview, when I answer and submit, then nothing is stored in my history or in any leaderboard.  
- [ ] Given the preview, then it is clearly labelled "Preview".  
- [ ] Edge case: Drafts with incomplete questions can be previewed, and incomplete questions are highlighted.

#### Scope

- In scope: Preview mode.  
- Out of scope: Grading check (US-EST-22).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-22 \- Validate test grading

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-22 - Validate test grading"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test (student or administrator),  
I want to check the correct answers and the grading result of my test,  
so that I can make sure the system grades answers correctly.

#### Context

- Problem: Wrong answer keys produce wrong scores.  
- Current behavior: Not implemented.  
- Expected behavior: An answer-key view and a graded preview that uses the same grading logic as real attempts.

#### Acceptance Criteria

- [ ] Given I submit a preview, when it is graded, then the score is calculated with the same logic as real attempts.  
- [ ] Given the answer-key view, then I see each question with its correct answer and its score.  
- [ ] Given the answer-key view, then the maximum score shown equals the sum of the question scores.  
- [ ] Edge case: Questions without a correct answer are highlighted as errors.

#### Scope

- In scope: Answer-key view and graded preview.  
- Out of scope: Validation of official tests by other administrators (US-ADM-16).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-23 \- Check publication requirements

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-23 - Check publication requirements"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test (student or administrator),  
I want the platform to define and enforce clear publication requirements,  
so that I only publish complete tests that can be graded correctly.

#### Context

- Problem: US-EST-13 and US-ADM-12 depend on publication requirements that were never defined, and US-EST-23 was missing from the numbering.  
- Current behavior: Not defined.  
- Expected behavior: Requirements: title and subject set; a minimum number of questions (configurable, proposed 5); every question has a statement, 2 to 6 options, exactly one correct answer and a score of 0 or more; the test's maximum score is greater than 0\.

#### Acceptance Criteria

- [ ] Given a test, when its readiness is evaluated, then the rules above are applied by one shared validation used by both the editor and the publish endpoint.  
- [ ] Given a publish request that fails any rule, then the server rejects it and returns every failing rule with the question or field it refers to (displayed by US-EST-39).  
- [ ] Given every rule passes, then the test can be published.  
- [ ] Edge case: The same requirements apply to official tests (US-ADM-12).

#### Scope

- In scope: Requirement definitions and server-side validation.  
- Out of scope: Displaying the errors (US-EST-39); content quality review by administrators (US-ADM-16, US-ADM-19).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-EST-39 \- Receive clear publication validation errors

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-39 - Receive clear publication validation errors"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As the author of a test (student or administrator),  
I want to know which questions or fields prevent my test from being published,  
so that I can fix them before sharing it.

#### Context

- Problem: A generic "cannot publish" message forces authors to search for the problem.  
- Current behavior: US-EST-23 mixed the rules and how they are displayed; it now defines the rules and this story covers the messages.  
- Expected behavior: A publication checklist listing every failing rule of US-EST-23, each linked to the exact question or field.

#### Acceptance Criteria

- [ ] Given I open the publication check or click "Publish", then I see each requirement as passed or failed.  
- [ ] Given a failed requirement, then the message names the question number or field and what is wrong (for example "Question 4 has no correct answer").  
- [ ] Given I click a failed requirement, then I am taken to that question or field.  
- [ ] Edge case: Errors returned by the server (for example when the editor was out of date) are shown in the same format.

#### Scope

- In scope: Display of publication validation errors.  
- Out of scope: The rules themselves (US-EST-23).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



# 3.7. History and Progress Tracking

## \[User Story\] US-EST-24 \- View my test history

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-24 - View my test history"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to view my history of completed tests, including date, subject, test, type, score, correct answers and mistakes,  
so that I can track my academic progress.

#### Context

- Problem: Students need a record of their attempts.  
- Current behavior: Not implemented.  
- Expected behavior: A personal list of completed attempts, newest first.

#### Acceptance Criteria

- [ ] Given I open my history, then I see my completed attempts with date, subject, test, type (official/practice), score, correct answers and mistakes, newest first.  
- [ ] Given an attempt in the list, when I open it, then I see its detailed results (US-EST-03).  
- [ ] Given any user, then only my own history is visible to me; other users get a 403 response.  
- [ ] Edge case: Attempts on tests that were later withdrawn remain, labelled as not counting (US-EST-37).

#### Scope

- In scope: History list and access to attempt details.  
- Out of scope: Filters (US-EST-25).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-EST-25 \- Filter my test history

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-25 - Filter my test history"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to filter my history by subject, date range and type,  
so that I can easily find the results I want to review.

#### Context

- Problem: A long history is hard to navigate.  
- Current behavior: Only subject and date were mentioned.  
- Expected behavior: Combinable filters by subject, date range and type (official/practice).

#### Acceptance Criteria

- [ ] Given my history, when I select a subject, then only attempts of that subject are shown.  
- [ ] Given my history, when I set a date range, then only attempts within it are shown.  
- [ ] Given my history, when I combine filters, then only attempts matching all of them are shown, and I can clear them in one click.  
- [ ] Edge case: When no attempt matches, an empty-state message is shown.

#### Scope

- In scope: History filters.  
- Out of scope: Exporting the history.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



## \[User Story\] US-EST-35 \- View my personal best and attempt count

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-35 - View my personal best and attempt count"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want to see my highest submitted score and the number of attempts for each test,  
so that I can understand my progress without confusing individual tries with leaderboard scores.

#### Context

- Problem: With retakes (US-EST-49), students need to see their best result separately from the score that counts in the rankings.  
- Current behavior: Not available.  
- Expected behavior: For every test I attempted, I see the number of submitted attempts and my personal best and, for official tests, the score that counts toward the leaderboard, each clearly labelled.

#### Acceptance Criteria

- [ ] Given I have submitted attempts, when I open my progress view or a test's detail page, then I see the number of submitted attempts and my highest score for that test.  
- [ ] Given an official test, then my personal best and the score counting toward the leaderboard are shown separately and labelled.  
- [ ] Given attempts in progress or previews, then they are not counted.  
- [ ] Edge case: Administrator results are shown to the administrator but labelled as not competitive.

#### Scope

- In scope: Personal best and attempt count per test.  
- Out of scope: Score breakdown by subject (US-EST-36).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-36 \- View my subject and global score breakdown

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-36 - View my subject and global score breakdown"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to see which eligible official tests contribute to each subject total and to my global total,  
so that I can verify my ranking and identify where to improve.

#### Context

- Problem: Students cannot check how their totals are built.  
- Current behavior: Not available.  
- Expected behavior: A private breakdown showing, per subject, each contributing official test with its counted score, and how subject totals make up the global total.

#### Acceptance Criteria

- [ ] Given my scores page, when I open a subject, then I see each eligible official test that contributes, its counted score and the subject total.  
- [ ] Given the breakdown, then the subject total matches my score in the subject leaderboard and the global total matches the global leaderboard (US-EST-06).  
- [ ] Given a withdrawn official test I attempted, then it is listed as "not counting" with no contribution (US-COM-11).  
- [ ] Edge case: Only I can see my breakdown; other users get a 403 response.

#### Scope

- In scope: Private score breakdown for the current period.  
- Out of scope: Breakdown for closed periods.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-37 \- Review results after a test is withdrawn

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-37 - Review results after a test is withdrawn"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want my submitted attempts to stay visible in my private history after a test is withdrawn,  
so that I do not lose evidence of my work even though its competitive contribution is removed.

#### Context

- Problem: Withdrawing a test removes its leaderboard contribution (US-COM-11), but the attempts must not disappear.  
- Current behavior: US-EST-24 only mentioned this as an edge case.  
- Expected behavior: Attempts on withdrawn tests (official tests, or custom tests withdrawn by moderation) stay in my history with their results and a "test withdrawn – not counting" label.

#### Acceptance Criteria

- [ ] Given a test I attempted is withdrawn, when I open my history, then the attempt is still listed with date, score, correct answers and mistakes.  
- [ ] Given that attempt, when I open it, then I see the full results of the version I took (US-ADM-31) and a label saying the test is withdrawn and does not count.  
- [ ] Given the test is republished, then the label disappears and the contribution returns.  
- [ ] Edge case: The withdrawn test itself cannot be opened or retaken from the history.

#### Scope

- In scope: History and results of attempts on withdrawn tests.  
- Out of scope: Leaderboard recalculation (US-ADM-30).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 2



# 3.8. Rewards and Profile

## \[User Story\] US-EST-26 \- Earn badges

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-26 - Earn badges"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to earn badges automatically when I meet specific objectives,  
so that my achievements are recognized and I stay motivated.

#### Context

- Problem: The role model says badges cannot be assigned manually and administrators cannot earn them; the story did not say so.  
- Current behavior: Not implemented.  
- Expected behavior: Badges use the requirement types defined in the badge catalog (US-ADM-26), all based on official tests, and are evaluated automatically after each graded official attempt and each leaderboard update.

#### Acceptance Criteria

- [ ] Given I meet a badge's criteria, when the triggering event happens, then the badge is awarded automatically and I receive a notification (US-EST-47).  
- [ ] Given a badge I already have, then it is never awarded twice.  
- [ ] Given any client request trying to assign a badge, then it is rejected; administrator accounts never earn badges.  
- [ ] Edge case: When a badge is added or reactivated (US-ADM-26, US-ADM-27), students who already meet its criteria receive it at the next evaluation; deactivated badges are no longer awarded.

#### Scope

- In scope: Automatic badge evaluation and awarding.  
- Out of scope: Managing the badge catalog (US-ADM-26).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 5



## \[User Story\] US-EST-27 \- View my badges

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-27 - View my badges"
labels: ["user-story", "enhancement", "good first issue"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to view the badges I have earned,  
so that I can track my achievements.

#### Context

- Problem: Students want to see their achievements.  
- Current behavior: Not implemented.  
- Expected behavior: A badges section in my profile with earned badges and the date earned.

#### Acceptance Criteria

- [ ] Given I have earned badges, when I open my badges section, then I see each badge with name, icon and date earned.  
- [ ] Given I have no badges, then an empty state invites me to view the requirements (US-EST-28).  
- [ ] Given my profile is public, then the badges I chose to show (US-EST-41) also appear on my public profile (US-COM-08).  
- [ ] Edge case: Deactivated badges (US-ADM-27) remain visible to students who earned them.

#### Scope

- In scope: Earned badges list.  
- Out of scope: Requirements and progress (US-EST-28).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 2



## \[User Story\] US-EST-28 \- View badge requirements and progress

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-28 - View badge requirements and progress"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to view the requirements to earn each badge and my progress toward them,  
so that I know which objectives I still need to complete.

#### Context

- Problem: Knowing the requirement alone is less motivating than seeing progress.  
- Current behavior: Only requirements were mentioned.  
- Expected behavior: Each badge shows its requirement and, if not earned, the student's progress.

#### Acceptance Criteria

- [ ] Given the badges page, then each badge shows its name, icon and requirement.  
- [ ] Given a badge I have not earned, then I see my progress toward it (for example 3/5 official tests completed).  
- [ ] Given a badge I have earned, then it is marked as earned.  
- [ ] Edge case: Hidden badges, if any, show only "Secret badge" until earned.

#### Scope

- In scope: Requirement and progress display.  
- Out of scope: Badge awarding (US-EST-26).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 2



## \[User Story\] US-EST-29 \- View my profile

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-29 - View my profile"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want to view my profile,  
so that I can see my account details and the information related to my activity.

#### Context

- Problem: Users need a personal page with their data and activity.  
- Current behavior: Not implemented.  
- Expected behavior: A private profile page with account details and an activity summary.

#### Acceptance Criteria

- [ ] Given I open my profile, then I see my name, public name, email and registration date.  
- [ ] Given I am a student, then I also see a summary of my activity: completed tests, leaderboard positions and badges.  
- [ ] Given my profile page, then I see links to edit my data (US-EST-30), my privacy settings (US-EST-46) and my password (US-AUT-08).  
- [ ] Edge case: For administrators, the activity summary does not show leaderboard positions or badges.

#### Scope

- In scope: Private profile page.  
- Out of scope: Public profile (US-COM-08).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



## \[User Story\] US-EST-30 \- Update editable profile data

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-30 - Update editable profile data"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want to modify the profile data the platform allows me to edit,  
so that I can keep my information up to date.

#### Context

- Problem: The editable fields were not defined.  
- Current behavior: Undefined.  
- Expected behavior: Editable: name, public name, avatar and short bio. Not editable: role, scores, history and badges.

#### Acceptance Criteria

- [ ] Given my profile, when I edit my name, public name, avatar or bio and save, then the changes are stored and shown.  
- [ ] Given a public name already in use, when I save, then I get an error asking for another one.  
- [ ] Given a request that tries to change role, scores, history or badges, then the server rejects it.  
- [ ] Edge case: Avatars larger than 2 MB or in a format other than JPG/PNG/WebP are rejected.

#### Scope

- In scope: Editing allowed profile fields.  
- Out of scope: Changing the email address.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-31 \- Protect users' private data

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-31 - Protect users' private data"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a registered user (student or administrator),  
I want only public data, or data I have authorized, to be shown to others,  
so that my personal data and academic results stay private.

#### Context

- Problem: Privacy must be enforced on the server, not only hidden in the interface.  
- Current behavior: Not defined how it is enforced.  
- Expected behavior: Every API response is filtered by role and ownership; private fields never leave the server for unauthorized users.

#### Acceptance Criteria

- [ ] Given any public endpoint, then it never returns detailed results, history or private profile data, and returns full name, email or hidden badges only when the owner has opted in (US-EST-41, US-EST-42).  
- [ ] Given a user requesting another user's attempts or history, then the server returns 403\.  
- [ ] Given my privacy settings (US-EST-46), then every page and endpoint respects them.  
- [ ] Edge case: Automated tests cover each endpoint with visitor, student, other-student and administrator roles.

#### Scope

- In scope: Server-side filtering of personal data.  
- Out of scope: Account deletion (US-EST-48).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Critical  
- Story Points: 5



## \[User Story\] US-EST-41 \- Manage the visibility of my earned badges

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-41 - Manage the visibility of my earned badges"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to show or hide individual earned badges on my public profile,  
so that I control which achievements others can see.

#### Context

- Problem: Students may not want every achievement on their public profile.  
- Current behavior: Not available. Depends on persistent privacy settings, which are not yet in the class diagram.  
- Expected behavior: Each earned badge has a show/hide toggle; hidden badges remain visible to me.

#### Acceptance Criteria

- [ ] Given my badges section, when I hide a badge, then it no longer appears on my public profile.  
- [ ] Given a hidden badge, then I still see it in my own badges section, marked as hidden.  
- [ ] Given the public profile API, then hidden badges are not included in the response.  
- [ ] Edge case: Newly earned badges are shown by default; if my whole profile is private (US-EST-46), no badges are shown at all.

#### Scope

- In scope: Per-badge visibility on the public profile.  
- Out of scope: Badge awarding (US-EST-26).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 2



## \[User Story\] US-EST-42 \- Manage optional public profile details

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-42 - Manage optional public profile details"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to choose whether my full name and email address appear on my public profile,  
so that I control the exposure of my personal information.

#### Context

- Problem: Full name and email are personal data; showing them must be the user's explicit choice.  
- Current behavior: Implementation dependency: the persistent privacy settings are not yet in the class diagram, so these details must stay private until they are.  
- Expected behavior: Two opt-in toggles (full name, email), both off by default; otherwise only the public name is shown.

#### Acceptance Criteria

- [ ] Given my privacy settings, when I enable "Show full name", then my full name appears on my public profile.  
- [ ] Given my privacy settings, when I enable "Show email", then my email appears on my public profile.  
- [ ] Given both toggles are off (default), then neither the page nor the API exposes my full name or email.  
- [ ] Edge case: Until the privacy settings exist in the data model, both details remain private for everyone.

#### Scope

- In scope: Opt-in visibility of full name and email.  
- Out of scope: Profile visibility and leaderboard anonymity (US-EST-46).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 3



# 3.9. Reports and Issues

## \[User Story\] US-EST-32 \- Report content

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-32 - Report content"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want to report a test or content that I consider incorrect, duplicated or inappropriate,  
so that the administration team can review it.

#### Context

- Problem: Content quality depends on users flagging problems.  
- Current behavior: Not implemented.  
- Expected behavior: A report action on tests with a reason and a description.

#### Acceptance Criteria

- [ ] Given a published test, when I click "Report", then I can choose a reason (incorrect, duplicated, inappropriate, other) and add a description.  
- [ ] Given I submit a report, then it appears in the administrators' queue (US-ADM-20) and in my reports (US-EST-43).  
- [ ] Given I already have an open report for the same test, when I report it again, then I am told it is already reported.  
- [ ] Edge case: I cannot report my own test.

#### Scope

- In scope: Reporting tests and their content.  
- Out of scope: Reporting platform malfunctions (US-EST-33).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-33 \- Report a platform issue

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-33 - Report a platform issue"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to report a problem I found in the operation of the platform,  
so that it can be corrected.

#### Context

- Problem: Users need a channel to report bugs.  
- Current behavior: The roles line said Student but the persona said "authenticated user".  
- Expected behavior: An issue form with category, description, optional page URL and optional screenshot.

#### Acceptance Criteria

- [ ] Given the Report a Problem page, when I submit a category (bug, account, other) and a description, then the issue is created and I see a reference number.  
- [ ] Given I submit an issue, then it appears in the platform problems queue (US-ADM-29) and in my reports (US-EST-43).  
- [ ] Given I attach a screenshot, then only images up to 5 MB are accepted.  
- [ ] Edge case: A user can submit at most 5 issues per day.

#### Scope

- In scope: Platform issue form.  
- Out of scope: Reporting test content (US-EST-32).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-34 \- Be notified when my custom test is withdrawn by moderation

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-34 - Be notified when my custom test is withdrawn by moderation"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to receive a notification when one of my custom tests is withdrawn by an administrator for moderation,  
so that I know it has been taken down and why.

#### Context

- Problem: The story did not say what the notification contains or where it is delivered.  
- Current behavior: Not implemented. Implementation dependency: notification persistence and delivery are not yet in the class diagram (see US-ADM-32).  
- Expected behavior: An in-app notification and an email with the test title, reason and date.

#### Acceptance Criteria

- [ ] Given an administrator withdraws my test for moderation (US-ADM-21, US-ADM-32), then I receive an in-app notification and an email with the title, reason and date.  
- [ ] Given my list of tests, then the test shows "Withdrawn (moderation)" with the reason (US-EST-11).  
- [ ] Given a test withdrawn by moderation, then nobody can see or take it, while existing attempts stay in users' histories (US-EST-37).  
- [ ] Edge case: If my account has been deleted, no notification is sent.

#### Scope

- In scope: Moderation withdrawal notification.  
- Out of scope: Appealing a moderation withdrawal.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-43 \- Track the status of my reports

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-43 - Track the status of my reports"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a user who submitted a report (student or administrator),  
I want to see whether my report is pending, in review, resolved or dismissed,  
so that I understand whether the problem has been addressed.

#### Context

- Problem: Users who report get no feedback, which discourages reporting.  
- Current behavior: Not available.  
- Expected behavior: A "My reports" list with the status of each content report and platform problem, and a notification when it is closed.

#### Acceptance Criteria

- [ ] Given I open "My reports", then I see each report with date, target (test or platform), reason and status (Pending, In review, Resolved, Dismissed).  
- [ ] Given an administrator resolves or dismisses my report (US-ADM-28), then I receive a notification with the outcome (US-EST-47).  
- [ ] Given any user, then only I can see my own reports.  
- [ ] Edge case: The reporter's identity is never shown to the author of the reported test.

#### Scope

- In scope: Report tracking and outcome notification.  
- Out of scope: Discussion threads on reports.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 3



# 3.10. Additional Student Stories

## \[User Story\] US-EST-45 \- Resume an unfinished attempt

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-45 - Resume an unfinished attempt"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to resume a test I started but did not submit,  
so that I do not lose my answers if I close the page or lose my connection.

#### Context

- Problem: The stories did not define what happens to an attempt that is interrupted.  
- Current behavior: Undefined.  
- Expected behavior: In-progress attempts are kept with their saved answers and can be resumed.

#### Acceptance Criteria

- [ ] Given an attempt in progress, when I open the test again, then I see a "Continue" button and my saved answers are restored.  
- [ ] Given a test, then I can have only one attempt in progress for it at a time.  
- [ ] Given an unfinished attempt, then it is not graded and does not count toward any leaderboard until it is submitted.  
- [ ] Edge case: Attempts not submitted within 7 days are submitted automatically with the saved answers.

#### Scope

- In scope: Keeping and resuming attempts.  
- Out of scope: Time limits per test. Open question: should official tests have a time limit?

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 5



## \[User Story\] US-EST-46 \- Configure my privacy settings

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-46 - Configure my privacy settings"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to choose whether my profile is public and whether my name appears in leaderboards,  
so that I control which of my data other users can see.

#### Context

- Problem: US-EST-31 refers to data "authorized by me", but no story let users authorize anything.  
- Current behavior: Not available. Depends on persistent privacy settings, which are not yet in the class diagram.  
- Expected behavior: Two settings: profile visibility (public/private) and leaderboard display (public name/anonymous).

#### Acceptance Criteria

- [ ] Given my privacy settings, when I make my profile private, then others only see my public name (US-COM-08).  
- [ ] Given my privacy settings, when I choose to appear anonymously, then leaderboards show "Anonymous student" instead of my name, keeping my position.  
- [ ] Given I change a setting, then the change applies immediately on every page.  
- [ ] Edge case: I always see my own name and position highlighted in leaderboards.

#### Scope

- In scope: Profile visibility and leaderboard display settings.  
- Out of scope: Badge visibility (US-EST-41), full name and email (US-EST-42), account deletion (US-EST-48). Open question: what should the defaults be?

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-EST-47 \- View my notifications

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-47 - View my notifications"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an authenticated user (student or administrator),  
I want to see my notifications in one place,  
so that I do not miss events that affect me.

#### Context

- Problem: US-EST-26 and US-EST-34 send notifications, but there was no place to see them.  
- Current behavior: Not available. Implementation dependency: notification persistence and delivery are not yet in the class diagram.  
- Expected behavior: A notification icon with an unread counter and a list of notifications.

#### Acceptance Criteria

- [ ] Given I have unread notifications, then the icon shows how many.  
- [ ] Given I open the list, then I see my notifications newest first, each with a link to the related content.  
- [ ] Given I open or mark a notification as read, then the counter is updated.  
- [ ] Edge case: Notifications older than 90 days are deleted automatically.

#### Scope

- In scope: Notification centre and unread counter.  
- Out of scope: Email preferences.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 5



## \[User Story\] US-EST-48 \- Delete my account

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-48 - Delete my account"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to delete my account and personal data,  
so that I can exercise my right to erasure under data protection law (GDPR).

#### Context

- Problem: The platform processes personal data of EU users and must allow erasure.  
- Current behavior: Not available.  
- Expected behavior: Account deletion with confirmation; personal data is deleted and leaderboard entries are anonymized.

#### Acceptance Criteria

- [ ] Given my profile, when I request deletion and confirm with my password, then my account and personal data are deleted and I am logged out.  
- [ ] Given my account is deleted, then my past leaderboard entries are shown as "Deleted user".  
- [ ] Given my account is deleted, then my custom tests without attempts are deleted, and those with attempts are made private and shown as by "Deleted user" (US-EST-40).  
- [ ] Edge case: Administrator accounts cannot delete themselves if they are the last administrator.

#### Scope

- In scope: Self-service account deletion.  
- Out of scope: Data export. Open question: anonymize or remove leaderboard entries?

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 5



## \[User Story\] US-EST-49 \- Retake an official test for practice

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-EST-49 - Retake an official test for practice"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want to retake an official test I have already completed,  
so that I can keep practicing with official material.

#### Context

- Problem: The stories did not say whether official tests can be repeated or which attempt counts.  
- Current behavior: Undefined.  
- Expected behavior: Official tests can be retaken; only the first attempt counts toward leaderboards (US-EST-05) and retakes are stored as practice.

#### Acceptance Criteria

- [ ] Given an official test I completed, when I open it, then I can start a new attempt and I am told it will not change my leaderboard score.  
- [ ] Given a retake, when it is graded, then I see my results and the attempt is stored in my history as "Practice"; it may raise my personal best (US-EST-35) but not my leaderboard score.  
- [ ] Given a retake, then no leaderboard and no badge based on competitive scores changes.  
- [ ] Edge case: If an administrator regrades the test (US-ADM-17), only the first attempt is recalculated for leaderboard purposes.

#### Scope

- In scope: Retaking official tests as practice.  
- Out of scope: Changing the counting rule. Open question: first or best attempt?

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



# 4.1. Administration Panel and Permissions

## \[User Story\] US-ADM-01 \- Access the administration panel

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-01 - Access the administration panel"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to access a centralized administration panel,  
so that I can manage the platform's administrative features from one place.

#### Context

- Problem: Administrative features need a single entry point.  
- Current behavior: Not implemented.  
- Expected behavior: A panel with sections for subjects, official tests, moderation, content reports, platform problems, badges, leaderboards, users, statistics and audit log.

#### Acceptance Criteria

- [ ] Given I am an administrator, when I log in, then I see a link to the administration panel.  
- [ ] Given the panel, then I see its sections and counters for pending items (open reports, unresolved issues).  
- [ ] Given the panel, when I select a section, then I am taken to it.  
- [ ] Edge case: If a section has nothing pending, its counter is hidden.

#### Scope

- In scope: Panel layout, navigation and pending counters.  
- Out of scope: The features of each section (other ADM stories).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-ADM-02 \- Restrict administrative access

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-02 - Restrict administrative access"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want administrative features to be accessible only to administrators,  
so that unauthorized users cannot modify sensitive content.

#### Context

- Problem: Hiding links is not enough; administrative endpoints must be protected.  
- Current behavior: Not implemented.  
- Expected behavior: Every administrative endpoint checks the Administrator role on the server.

#### Acceptance Criteria

- [ ] Given a student or visitor, when they call any administrative endpoint, then the server returns 403 (or 401 if not logged in).  
- [ ] Given a non-administrator, then the administration panel link is not shown.  
- [ ] Given an unauthorized access attempt, then it is recorded in the audit log (US-ADM-35).  
- [ ] Edge case: If an administrator's role is revoked (US-ADM-34), their next request no longer has administrative access.

#### Scope

- In scope: Server-side role checks on administrative endpoints.  
- Out of scope: General permission enforcement (US-SEC-01).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Critical  
- Story Points: 3



## \[User Story\] US-ADM-03 \- Confirm sensitive actions

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-03 - Confirm sensitive actions"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to confirm destructive actions or actions that affect other users before they are executed,  
so that I avoid accidental changes.

#### Context

- Problem: Administrative mistakes can affect many students.  
- Current behavior: Not implemented.  
- Expected behavior: A confirmation dialog that names the item and the impact of the action.

#### Acceptance Criteria

- [ ] Given a destructive or sensitive action, when I trigger it, then a dialog names the affected item and its impact (for example "This test has 120 attempts").  
- [ ] Given the dialog, when I cancel, then nothing changes.  
- [ ] Given a very high-impact action (deleting a subject, closing a leaderboard period), then I must type the item's name to confirm.  
- [ ] Edge case: Confirmations are also enforced on the server; a request without the confirmation flag is rejected.

#### Scope

- In scope: Confirmation dialogs for sensitive actions.  
- Out of scope: Undo functionality.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 2



# 4.2. Subject Management

## \[User Story\] US-ADM-04 \- Create a subject

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-04 - Create a subject"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to create a new subject,  
so that I can add it to the platform when it becomes part of the curriculum.

#### Context

- Problem: Tests and leaderboards are organized by subject.  
- Current behavior: Not implemented.  
- Expected behavior: A form with name, code and description.

#### Acceptance Criteria

- [ ] Given the subject form, when I submit a name, a code and a description, then the subject is created and active.  
- [ ] Given a name or code already in use, when I submit, then I get a validation error.  
- [ ] Given a new subject, then it appears in the subjects list (US-COM-01) and has an empty leaderboard.  
- [ ] Edge case: Name and code are trimmed and compared case-insensitively.

#### Scope

- In scope: Subject creation.  
- Out of scope: Importing subjects from the university.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 2



## \[User Story\] US-ADM-05 \- Edit a subject

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-05 - Edit a subject"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to modify the information of an existing subject,  
so that I can keep its data up to date.

#### Context

- Problem: Subject data can change.  
- Current behavior: Not implemented.  
- Expected behavior: Name, code and description can be edited without affecting tests or results.

#### Acceptance Criteria

- [ ] Given a subject, when I edit its name, code or description and save, then the changes are shown everywhere.  
- [ ] Given a name or code already in use, when I save, then I get a validation error.  
- [ ] Given a subject with tests and results, when I edit it, then its tests, results and leaderboard are not affected.  
- [ ] Edge case: Changes are recorded in the audit log (US-ADM-35).

#### Scope

- In scope: Editing subject data.  
- Out of scope: Moving tests between subjects (US-ADM-09).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



## \[User Story\] US-ADM-06 \- Delete or archive a subject

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-06 - Delete or archive a subject"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to delete a subject that is no longer needed, or archive it if it already has content,  
so that I can remove subjects that are no longer part of the academic path without losing results.

#### Context

- Problem: Deleting a subject with tests would destroy results and leaderboards.  
- Current behavior: The story did not cover subjects that have tests or results.  
- Expected behavior: Subjects without tests can be deleted; subjects with tests or results can only be archived.

#### Acceptance Criteria

- [ ] Given a subject with no tests, when I delete it and confirm, then it is deleted.  
- [ ] Given a subject with tests or results, when I try to delete it, then I am offered to archive it instead.  
- [ ] Given an archived subject, then it disappears from browsing, search and new test creation, but its results and past leaderboards remain.  
- [ ] Edge case: An archived subject can be restored.

#### Scope

- In scope: Deletion and archiving of subjects.  
- Out of scope: Deleting individual tests (US-ADM-11).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-ADM-07 \- View and manage a subject's tests

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-07 - View and manage a subject's tests"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to view all the tests associated with a subject,  
so that I can review and manage its content.

#### Context

- Problem: Administrators need a per-subject view of content.  
- Current behavior: Not implemented.  
- Expected behavior: A list of all tests of the subject, official and custom, in every status.

#### Acceptance Criteria

- [ ] Given a subject, when I open its tests, then I see every official and custom test with status, author and number of attempts.  
- [ ] Given the list, when I filter by type or status, then only matching tests are shown.  
- [ ] Given a test in the list, then I can open the relevant action (edit, withdraw, withdraw for moderation).  
- [ ] Edge case: Custom tests in Draft are not listed, because drafts are private to their author.

#### Scope

- In scope: Per-subject test list and quick actions.  
- Out of scope: The actions themselves (US-ADM-10, US-ADM-13, US-ADM-21).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



# 4.3. Official Test Management

## \[User Story\] US-ADM-08 \- Create an official test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-08 - Create an official test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to create an official test,  
so that students have validated content based on exams or official material.

#### Context

- Problem: Official tests need the same metadata as custom tests plus their source.  
- Current behavior: Metadata undefined.  
- Expected behavior: A form with title, subject, year, description, difficulty, tags and source; the test is created as an official draft.

#### Acceptance Criteria

- [ ] Given the form, when I submit title, subject and year, then an official test is created in Draft status.  
- [ ] Given the form, then I can set description, difficulty, tags and source (for example "Final exam, June 2025").  
- [ ] Given a new official test, then students cannot see it until it is published (US-ADM-12).  
- [ ] Edge case: Only administrators can create tests of type official; the type cannot be set by students.

#### Scope

- In scope: Creation of official tests and their metadata.  
- Out of scope: Questions (US-EST-14 to US-EST-20).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Critical  
- Story Points: 5



## \[User Story\] US-ADM-09 \- Assign an official test to a subject

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-09 - Assign an official test to a subject"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to associate each official test with its subject,  
so that students can find it and its results count in the right leaderboard.

#### Context

- Problem: An official test's subject determines which leaderboard it affects.  
- Current behavior: Not implemented.  
- Expected behavior: The subject is required, and it can only be changed while the test has no recorded results.

#### Acceptance Criteria

- [ ] Given an official test, then a subject is required to save it.  
- [ ] Given an official test without results, when I change its subject, then the change is saved.  
- [ ] Given an official test with recorded results, when I try to change its subject, then the change is blocked with an explanation.  
- [ ] Edge case: Archived subjects cannot be selected.

#### Scope

- In scope: Subject assignment.  
- Out of scope: Moving results between leaderboards.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 2



## \[User Story\] US-ADM-10 \- Edit an official test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-10 - Edit an official test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to modify an official test,  
so that I can correct errors or update its content.

#### Context

- Problem: The role model requires that changes to a test with recorded results do not create inconsistencies.  
- Current behavior: The story did not cover tests with results.  
- Expected behavior: Any administrator can edit an official test; if it has results, a new version is created (US-ADM-31).

#### Acceptance Criteria

- [ ] Given an official test without attempts, when I edit and save it, then the changes are applied directly.  
- [ ] Given an official test with attempts, when I save changes, then I am warned of the number of attempts and a new version is created; past attempts keep their version.  
- [ ] Given the change affects grading of past attempts (wrong answer key), then I am offered the correction flow (US-ADM-17).  
- [ ] Edge case: Every change is recorded in the audit log.

#### Scope

- In scope: Editing official tests and versioning.  
- Out of scope: Regrading past attempts (US-ADM-17).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-ADM-11 \- Delete an official test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-11 - Delete an official test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to delete an official test,  
so that I can remove incorrect, obsolete or unnecessary content.

#### Context

- Problem: Deleting a test with results would break leaderboards and history.  
- Current behavior: The story did not cover tests with results.  
- Expected behavior: Tests without attempts can be deleted; tests with attempts can only be withdrawn.

#### Acceptance Criteria

- [ ] Given an official test without attempts, when I delete it and confirm, then it is deleted.  
- [ ] Given an official test with attempts, when I try to delete it, then I am offered to withdraw it instead (US-ADM-13).  
- [ ] Given a deleted test, then it no longer appears anywhere.  
- [ ] Edge case: The deletion is recorded in the audit log.

#### Scope

- In scope: Deleting official tests.  
- Out of scope: Correcting leaderboards (US-ADM-17).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-ADM-12 \- Publish an official test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-12 - Publish an official test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to publish a validated official test,  
so that students can take it and its results count toward the leaderboards.

#### Context

- Problem: Publishing must only happen with complete content.  
- Current behavior: Not implemented.  
- Expected behavior: Publishing requires the publication requirements (US-EST-23) and warns if the test has not been validated (US-ADM-16).

#### Acceptance Criteria

- [ ] Given an official draft that meets the requirements, when I publish it, then it becomes Published and appears in the catalog.  
- [ ] Given a test that has not been validated, when I publish it, then I see a warning and must confirm.  
- [ ] Given a published official test, then only attempts made from that moment count toward the leaderboards.  
- [ ] Edge case: Administrator attempts made during validation never count (US-ADM-15).

#### Scope

- In scope: Publishing official tests.  
- Out of scope: Validation (US-ADM-16).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-ADM-13 \- Temporarily withdraw an official test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-13 - Temporarily withdraw an official test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to temporarily withdraw a published official test,  
so that it cannot be taken while it is being reviewed or corrected.

#### Context

- Problem: Errors found in a published test must stop new attempts quickly.  
- Current behavior: Not implemented.  
- Expected behavior: Withdrawing hides the test, blocks new attempts and removes its contributions from the active leaderboards; attempt records are kept.

#### Acceptance Criteria

- [ ] Given a published official test, when I withdraw it and confirm, then it disappears from the catalog and no new attempts can start.  
- [ ] Given a withdrawn test, then its contributions are removed from the active leaderboards and its attempt records are kept (US-COM-11, US-ADM-30).  
- [ ] Given a withdrawn test, when I republish it, then it becomes available again and its contributions are restored (US-ADM-30).  
- [ ] Edge case: Attempts in progress when the test is withdrawn follow the rule in US-EST-01.

#### Scope

- In scope: Withdrawing and republishing official tests.  
- Out of scope: Correcting scores (US-ADM-17).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 2



## \[User Story\] US-ADM-14 \- View official test status

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-14 - View official test status"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to see whether each official test is Draft, Published or Withdrawn, and its history of status changes,  
so that I can control its availability.

#### Context

- Problem: Administrators need to know the state of every official test.  
- Current behavior: Not implemented.  
- Expected behavior: An official test list with status, filter and status history.

#### Acceptance Criteria

- [ ] Given the official tests list, then each test shows its status.  
- [ ] Given the list, when I filter by status, then only matching tests are shown.  
- [ ] Given a test, when I open its history, then I see each status change with date and administrator.  
- [ ] Edge case: The list can be sorted by last change.

#### Scope

- In scope: Status list and history for official tests.  
- Out of scope: Custom test status (US-EST-11).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 2



## \[User Story\] US-ADM-15 \- Take tests without participating in leaderboards

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-15 - Take tests without participating in leaderboards"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to take official tests without my result being included in any leaderboard,  
so that I can validate content without altering student results.

#### Context

- Problem: The role model also states that administrators cannot earn badges and are not publicly discoverable.  
- Current behavior: The story only covered leaderboards.  
- Expected behavior: Administrator attempts are graded and shown but excluded from leaderboards, badges and public listings.

#### Acceptance Criteria

- [ ] Given I am an administrator, when I take an official test, then I see my results normally.  
- [ ] Given an administrator attempt, then it is stored flagged as administrative and excluded from all leaderboards and statistics of student performance.  
- [ ] Given an administrator account, then it never earns badges and never appears in leaderboards or public profiles.  
- [ ] Edge case: If a student is promoted to administrator (US-ADM-34), their earlier student results remain in closed periods but they leave current leaderboards.

#### Scope

- In scope: Exclusion of administrator activity from competitive features.  
- Out of scope: Validation workflow (US-ADM-16).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



# 4.4. Official Test Validation

## \[User Story\] US-ADM-16 \- Validate an official test before publication

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-16 - Validate an official test before publication"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to preview any official test, check its answer key and mark it as validated,  
so that only reviewed content reaches students.

#### Context

- Problem: Section 4.4 and US-ADM-16 were missing; the role model says official tests are validated before publication, possibly by an administrator other than the author.  
- Current behavior: US-EST-21 and US-EST-22 only cover the author's own tests.  
- Expected behavior: Any administrator can preview and validate any official draft; the validation is recorded.

#### Acceptance Criteria

- [ ] Given an official draft, when I open it, then I can preview it, take it and see the answer key (US-EST-21, US-EST-22).  
- [ ] Given I am satisfied, when I click "Mark as validated", then the test shows "Validated by \[administrator\] on \[date\]".  
- [ ] Given a validated test is edited, then its validation is reset.  
- [ ] Edge case: Validation attempts never count toward leaderboards (US-ADM-15).

#### Scope

- In scope: Validation by any administrator.  
- Out of scope: Mandatory two-person review.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



# 4.5. Leaderboard Management

## \[User Story\] US-ADM-17 \- Correct a leaderboard affected by an error

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-17 - Correct a leaderboard affected by an error"
labels: ["user-story", "enhancement", "help wanted"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to correct results or recalculate a leaderboard when there is a justified error,  
so that the leaderboards stay reliable.

#### Context

- Problem: An error in an answer key means stored scores are wrong, but scores are otherwise immutable.  
- Current behavior: The story did not describe the correction process or its safeguards.  
- Expected behavior: A controlled correction: fix the answer key or annul a question, preview the impact, give a reason, apply and notify.

#### Acceptance Criteria

- [ ] Given an error in an official test, when I start a correction, then I can choose to regrade with a corrected answer key or to annul a question.  
- [ ] Given the correction, before applying it, then I see the number of affected students and the changes in scores and positions.  
- [ ] Given I apply the correction with a mandatory reason, then scores and leaderboards are recalculated, the action is recorded in the audit log and affected students are notified.  
- [ ] Edge case: Closed periods can only be corrected by explicitly reopening them, which requires typed confirmation.

#### Scope

- In scope: Regrading and recalculation with impact preview.  
- Out of scope: Manual editing of individual scores.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 8



## \[User Story\] US-ADM-18 \- Close leaderboard periods

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-18 - Close leaderboard periods"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to close the current leaderboard period and start a new one when platform policy requires it,  
so that results from different periods are kept separate.

#### Context

- Problem: The original wording ("reset or close leaderboards or any situation...") was unclear.  
- Current behavior: Not implemented.  
- Expected behavior: Closing a period saves a snapshot of every leaderboard and starts a new period from zero.

#### Acceptance Criteria

- [ ] Given the current period, when I close it with typed confirmation, then a snapshot of the global and subject leaderboards is stored.  
- [ ] Given a closed period, then it is viewable from the period filter (US-COM-07).  
- [ ] Given a new period, then all students start at zero and new official attempts count toward it.  
- [ ] Edge case: Closing is blocked while a correction (US-ADM-17) is in progress.  
- [ ] Edge case: Withdrawing or republishing a test after a period is closed does not change that period's snapshot (US-COM-11).

#### Scope

- In scope: Closing periods and storing snapshots.  
- Out of scope: Automatic closing. Open question: period length (academic year or semester)?

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 5



# 4.6. Content Moderation

## \[User Story\] US-ADM-19 \- View tests created by students

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-19 - View tests created by students"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to view the tests created and shared by students,  
so that I can monitor the content shared on the platform.

#### Context

- Problem: Shared content needs monitoring.  
- Current behavior: Not implemented.  
- Expected behavior: A list of published custom tests (public and private) and those withdrawn by moderation, with author, subject, visibility and number of reports.

#### Acceptance Criteria

- [ ] Given the moderation section, then I see custom tests with title, author, subject, visibility, publication date and number of open reports.  
- [ ] Given the list, when I sort by number of reports, then the most reported tests come first.  
- [ ] Given a test, when I open it, then I can see all its questions and answers.  
- [ ] Edge case: Drafts are not listed, because they are private to their author.

#### Scope

- In scope: Moderation list of custom tests.  
- Out of scope: Removing content (US-ADM-21).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-ADM-20 \- Review reported content

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-20 - Review reported content"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to view the content reports submitted by users,  
so that I can decide whether the reported content should be corrected or withdrawn.

#### Context

- Problem: Content reports need a review queue.  
- Current behavior: Not implemented.  
- Expected behavior: A queue of content reports grouped by test, with filters by reason and status.

#### Acceptance Criteria

- [ ] Given the queue, then I see each report with the reported test (and question, if any), reason, reporter, date and status.  
- [ ] Given several reports about the same test, then they are grouped under that test.  
- [ ] Given the queue, when I filter by reason or status, then only matching reports are shown.  
- [ ] Edge case: Starting work on a report marks it as "In review" for other administrators (US-ADM-28).

#### Scope

- In scope: Review queue.  
- Out of scope: Resolving reports (US-ADM-28); general platform problems (US-ADM-29).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-ADM-21 \- Remove inappropriate content

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-21 - Remove inappropriate content"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to remove a test or content created by a student when it is incorrect, duplicated, inappropriate or violates platform rules,  
so that I can maintain the platform's quality.

#### Context

- Problem: A moderation withdrawal must inform the author and be traceable.  
- Current behavior: The story did not link to the author notification or require a reason.  
- Expected behavior: Withdrawal for moderation with confirmation and a mandatory reason; the test becomes Withdrawn (moderation), its author is notified and related reports are resolved.

#### Acceptance Criteria

- [ ] Given a custom test, when I withdraw it for moderation, then I must confirm and give a reason.  
- [ ] Given the withdrawal, then the test becomes Withdrawn (moderation), can no longer be found or taken (not even by direct link) and its author is notified (US-ADM-32, US-EST-34).  
- [ ] Given open reports on that test, then they are marked as Resolved (US-ADM-28) and their reporters are notified (US-EST-43).  
- [ ] Edge case: Attempts already made remain in users' history, labelled as withdrawn (US-EST-37).

#### Scope

- In scope: Withdrawing custom tests for moderation.  
- Out of scope: Suspending the author (US-ADM-33).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



# 4.7. Statistics and Monitoring

## \[User Story\] US-ADM-22 \- View general statistics

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-22 - View general statistics"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to view general platform statistics,  
so that I can monitor its usage and operation.

#### Context

- Problem: The platform's usage needs monitoring.  
- Current behavior: Not implemented.  
- Expected behavior: A dashboard with key indicators and a date range.

#### Acceptance Criteria

- [ ] Given the statistics section, then I see registered students, active students (last 7 and 30 days), attempts per day and published tests (official/custom).  
- [ ] Given the dashboard, when I change the date range, then all indicators update.  
- [ ] Given the dashboard, then administrator activity is excluded from student indicators.  
- [ ] Edge case: With no data in the range, indicators show zero rather than errors.

#### Scope

- In scope: General indicators.  
- Out of scope: Exporting statistics.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 5



## \[User Story\] US-ADM-23 \- View student participation

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-23 - View student participation"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to view student participation in tests,  
so that I can identify which content is used more or less.

#### Context

- Problem: Administrators need to know which content is useful.  
- Current behavior: Not implemented.  
- Expected behavior: Rankings of the most and least taken tests.

#### Acceptance Criteria

- [ ] Given the participation view, then I see tests ordered by attempts and by unique students.  
- [ ] Given the view, when I filter by subject, type or date range, then the data updates.  
- [ ] Given the view, then published tests with no attempts are listed.  
- [ ] Edge case: Administrator attempts are excluded.

#### Scope

- In scope: Participation rankings.  
- Out of scope: Per-student activity reports.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 3



## \[User Story\] US-ADM-24 \- View statistics by subject

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-24 - View statistics by subject"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to view statistics for a specific subject,  
so that I can analyze its activity and results.

#### Context

- Problem: Activity varies by subject.  
- Current behavior: Not implemented.  
- Expected behavior: Per-subject indicators and score distribution.

#### Acceptance Criteria

- [ ] Given a subject, then I see the number of tests, attempts, participating students and average score.  
- [ ] Given a subject, then I see the score distribution of its official tests.  
- [ ] Given the view, when I change the date range, then the data updates.  
- [ ] Edge case: Subjects without attempts show an empty state.

#### Scope

- In scope: Per-subject statistics.  
- Out of scope: Comparing subjects.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 3



## \[User Story\] US-ADM-25 \- View statistics for a test

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-25 - View statistics for a test"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to view statistics for a specific published test,  
so that I can assess participation and results.

#### Context

- Problem: Per-test data also reveals problematic questions.  
- Current behavior: Not implemented.  
- Expected behavior: Per-test indicators including the correct-answer rate of each question.

#### Acceptance Criteria

- [ ] Given a test, then I see attempts, unique students and average, minimum and maximum score.  
- [ ] Given a test, then I see the percentage of correct answers for each question.  
- [ ] Given a question with an unusually low correct rate, then it is highlighted as possibly wrong.  
- [ ] Edge case: Administrator attempts are excluded.

#### Scope

- In scope: Per-test statistics.  
- Out of scope: Correcting the test (US-ADM-17).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 3



# 4.8. Badge Management

## \[User Story\] US-ADM-26 \- Create and configure badges

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-26 - Create and configure badges"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to create a badge with a name, description, icon and a supported requirement type and value,  
so that the platform can automatically recognize achievements from official tests.

#### Context

- Problem: Badges need to be defined somewhere, and only measurable official-test achievements should be rewarded.  
- Current behavior: My earlier "Manage the badge catalog" story covered this; it is replaced by this story and US-ADM-27.  
- Expected behavior: A badge editor where the requirement is chosen from supported types based on official tests (for example "complete N official tests", "score at least X% in an official test", "reach the top N of a subject leaderboard") plus a value.

#### Acceptance Criteria

- [ ] Given the badge editor, when I enter name, description, icon, requirement type and value, then the badge is created and appears in the requirements list (US-EST-28).  
- [ ] Given the requirement type list, then only supported types based on official tests are offered; free-form rules are not allowed.  
- [ ] Given an invalid value (for example negative, or above 100%), when I save, then I get a validation error.  
- [ ] Edge case: Editing a badge's requirement never removes it from students who already earned it.

#### Scope

- In scope: Creating and editing badges.  
- Out of scope: Activation (US-ADM-27); manual awarding, which the role model does not allow.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 5



## \[User Story\] US-ADM-27 \- Activate or deactivate badges

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-27 - Activate or deactivate badges"
labels: ["user-story", "enhancement", "good first issue"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to control whether a badge can still be awarded,  
so that obsolete requirements can be retired without erasing achievements already earned.

#### Context

- Problem: Badges can become obsolete.  
- Current behavior: Not available.  
- Expected behavior: An active/inactive switch per badge.

#### Acceptance Criteria

- [ ] Given an active badge, when I deactivate it, then no student can earn it from then on.  
- [ ] Given a deactivated badge, then students who earned it keep it and still see it.  
- [ ] Given an inactive badge, when I reactivate it, then students who meet its requirement receive it at the next evaluation.  
- [ ] Edge case: Inactive badges are hidden from the requirements list (US-EST-28) for students who have not earned them.

#### Scope

- In scope: Activation state of badges.  
- Out of scope: Deleting badges.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 2



# 4.9. Reports and Issue Resolution

## \[User Story\] US-ADM-28 \- Resolve or dismiss submitted reports

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-28 - Resolve or dismiss submitted reports"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to move a report through its permitted states and record who closed it and when,  
so that every reported problem has a traceable outcome.

#### Context

- Problem: Reports need a clear lifecycle and accountability.  
- Current behavior: My earlier "Resolve or dismiss reports and issues" story covered this; it is replaced by this one.  
- Expected behavior: Reports follow Pending → In review → Resolved or Dismissed (Pending → Dismissed is also allowed); closing stores the administrator, the date and a comment.

#### Acceptance Criteria

- [ ] Given a Pending report, when I start working on it, then its status becomes In review.  
- [ ] Given a report In review, when I mark it Resolved or Dismissed with a comment, then the closing administrator and date are stored and the reporter is notified (US-EST-43).  
- [ ] Given a transition that is not permitted (for example Resolved → Pending), then it is rejected.  
- [ ] Edge case: The same lifecycle applies to content reports (US-ADM-20) and general platform problems (US-ADM-29).

#### Scope

- In scope: Report state machine and closure record.  
- Out of scope: Withdrawing the reported content (US-ADM-21).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 3



## \[User Story\] US-ADM-29 \- Review general platform problems

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-29 - Review general platform problems"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to investigate reports that do not reference a test or question,  
so that technical incidents are handled alongside content-moderation reports.

#### Context

- Problem: Platform problems (US-EST-33) are not about content and need their own view.  
- Current behavior: I had merged them into US-ADM-20; they are now separate.  
- Expected behavior: A list of platform problems with category, description, page URL, screenshot and reporter, using the same states as content reports.

#### Acceptance Criteria

- [ ] Given the platform problems view, then I see each problem with category, description, page URL, attached screenshot, reporter and date.  
- [ ] Given the view, when I filter by category or status, then only matching problems are shown.  
- [ ] Given a problem, when I resolve or dismiss it, then the lifecycle of US-ADM-28 applies.  
- [ ] Edge case: Problems reported from the same page in a short period are grouped to help spot incidents.

#### Scope

- In scope: Platform problems queue.  
- Out of scope: Content reports (US-ADM-20).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



# 4.10. Ranking and Historical Integrity

## \[User Story\] US-ADM-30 \- Recalculate rankings after publication changes

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-30 - Recalculate rankings after publication changes"
labels: ["user-story", "enhancement", "help wanted"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want rankings to be recalculated automatically when an official test is withdrawn or republished,  
so that active subject and global totals only include currently eligible tests.

#### Context

- Problem: The eligibility rule of US-COM-11 must be applied automatically.  
- Current behavior: Not available.  
- Expected behavior: Withdrawing or republishing an official test triggers a recalculation of the affected subject leaderboard and of the global leaderboard.

#### Acceptance Criteria

- [ ] Given I withdraw a published official test (US-ADM-13), then the affected subject and global leaderboards are recalculated without its contributions.  
- [ ] Given I republish it, then they are recalculated including its eligible attempts again, also those submitted while it was withdrawn.  
- [ ] Given a recalculation, then stored attempt scores are never changed; only leaderboard totals are.  
- [ ] Edge case: Closed periods (US-ADM-18) are not recalculated.  
- [ ] Edge case: Each recalculation is recorded in the audit log (US-ADM-35).

#### Scope

- In scope: Automatic recalculation on withdrawal and republication.  
- Out of scope: Corrections of answer keys (US-ADM-17).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: High  
- Story Points: 5



## \[User Story\] US-ADM-31 \- Preserve past submissions when editing question scores

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-31 - Preserve past submissions when editing question scores"
labels: ["user-story", "enhancement", "help wanted"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to update an official question's score without rewriting the scores of attempts already submitted,  
so that historical results remain stable and any change that affects competition can be reviewed.

#### Context

- Problem: The role model says recorded scores stay fixed, and editing a test with results must not create inconsistencies.  
- Current behavior: My earlier "Preserve recorded results when a test changes" story (versioning) had the same goal; merged here.  
- Expected behavior: Each attempt stores the version of the test it was taken on (questions, options, correct answers, scores). Editing a test with submitted attempts creates a new version; old attempts keep their scores.

#### Acceptance Criteria

- [ ] Given an official test with submitted attempts, when I change a question's score, then a new version is created and existing attempt scores do not change.  
- [ ] Given the change, then I see a notice that past attempts are unaffected and can open the correction flow (US-ADM-17) if the change is meant to fix an error that affects the rankings.  
- [ ] Given a past attempt, when its results are opened, then they show the questions, answers and scores of the version taken.  
- [ ] Edge case: The same versioning applies to other edits (statement, options, correct answer) and to custom tests.  
- [ ] Edge case: Editing a test with no submitted attempts does not create a new version.

#### Scope

- In scope: Test versioning and preservation of stored scores.  
- Out of scope: Regrading past attempts (US-ADM-17).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Critical  
- Story Points: 8



# 4.11. Moderation Notifications

## \[User Story\] US-ADM-32 \- Notify an author about a moderation withdrawal

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-32 - Notify an author about a moderation withdrawal"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want the affected student to receive a notification when I withdraw their custom test for moderation,  
so that they know which test was withdrawn and the moderation reason.

#### Context

- Problem: Authors must know why their content disappeared.  
- Current behavior: Implementation dependency: notification persistence and delivery are not in the current class diagram and need an approved design.  
- Expected behavior: When I withdraw a custom test for moderation (US-ADM-21), a notification with the test title, reason and date is created for its author; the reason is mandatory.

#### Acceptance Criteria

- [ ] Given I withdraw a custom test for moderation, then I must enter a reason before confirming.  
- [ ] Given the withdrawal is confirmed, then a notification is created for the author with title, reason and date (received as described in US-EST-34).  
- [ ] Given the notification, then it never reveals who reported the test.  
- [ ] Edge case: If the author's account is suspended or deleted, no notification is sent, but the reason is stored with the test.

#### Scope

- In scope: Creating the moderation notification.  
- Out of scope: How the student sees it (US-EST-34, US-EST-47).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



# 4.12. User and Platform Management

## \[User Story\] US-ADM-33 \- Suspend and reactivate user accounts

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-33 - Suspend and reactivate user accounts"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to suspend a student account that breaks the platform rules and reactivate it later,  
so that I can stop abuse without deleting data.

#### Context

- Problem: Moderation could only remove content, not stop repeat offenders.  
- Current behavior: Not available.  
- Expected behavior: Suspension with a reason; suspended users cannot log in and are hidden from public pages.

#### Acceptance Criteria

- [ ] Given a student account, when I suspend it with a reason and confirm, then the user cannot log in (US-AUT-02) and is notified by email.  
- [ ] Given a suspended account, then its profile and custom tests are hidden.  
- [ ] Given a suspended account, when I reactivate it, then access and visibility are restored.  
- [ ] Edge case: An administrator cannot suspend their own account.

#### Scope

- In scope: Suspension and reactivation.  
- Out of scope: Permanent bans with data deletion.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 5



## \[User Story\] US-ADM-34 \- Assign or revoke the administrator role

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-34 - Assign or revoke the administrator role"
labels: ["user-story", "enhancement", "question"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to grant or revoke the administrator role,  
so that the administration team can change over time.

#### Context

- Problem: No story explained how administrators are created.  
- Current behavior: Not defined.  
- Expected behavior: Administrators can promote students and demote other administrators, with confirmation and audit.

#### Acceptance Criteria

- [ ] Given a student, when I grant the administrator role and confirm, then they get access to the panel on their next request.  
- [ ] Given an administrator, when I revoke the role, then they lose administrative access immediately.  
- [ ] Given any role change, then it is recorded in the audit log.  
- [ ] Edge case: The last remaining administrator cannot be demoted.

#### Scope

- In scope: Role assignment.  
- Out of scope: Fine-grained permissions. Open question: should the first administrator be created by a setup script?

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



## \[User Story\] US-ADM-35 \- View the audit log

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-35 - View the audit log"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to view a log of administrative actions,  
so that every change that affects content, users or leaderboards is traceable.

#### Context

- Problem: Corrections and moderation withdrawals affect students and must be accountable.  
- Current behavior: Not available.  
- Expected behavior: An append-only log with who did what, when and why.

#### Acceptance Criteria

- [ ] Given an administrative action (publish, withdraw, moderation withdrawal, correction, ranking recalculation, period closure, badge change, report closure, role change, suspension), then an entry is stored with administrator, action, target, date and reason.  
- [ ] Given the audit log, when I filter by administrator, action or date, then matching entries are shown.  
- [ ] Given an entry, then it cannot be edited or deleted from the application.  
- [ ] Edge case: Unauthorized access attempts (US-ADM-02) are also logged.

#### Scope

- In scope: Audit log storage and viewer.  
- Out of scope: External log shipping.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 5



## \[User Story\] US-ADM-36 \- Feature tests on the home page

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-ADM-36 - Feature tests on the home page"
labels: ["user-story", "enhancement", "good first issue"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want to choose which tests are featured on the home page,  
so that I can highlight relevant content, such as upcoming exam material.

#### Context

- Problem: US-COM-02 shows featured tests but no one chose them.  
- Current behavior: Not defined.  
- Expected behavior: Administrators mark published tests as featured and set their order.

#### Acceptance Criteria

- [ ] Given a published public test, when I mark it as featured, then it appears on the home page.  
- [ ] Given the featured list, when I reorder it, then the home page follows that order.  
- [ ] Given a featured test is withdrawn or made private, then it disappears from the home page automatically.  
- [ ] Edge case: At most 6 tests can be featured at once.

#### Scope

- In scope: Featuring and ordering tests.  
- Out of scope: Automatic recommendations.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Low  
- Story Points: 2



# 5\. Cross-cutting Security

## \[User Story\] US-SEC-01 \- Enforce permissions on the server

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-SEC-01 - Enforce permissions on the server"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As a student,  
I want every permission rule to be enforced on the server,  
so that nobody can bypass the interface to cheat or access data they should not see.

#### Context

- Problem: The role model requires restrictions to be enforced on the server, not only in the UI, but no story covered it as a whole.  
- Current behavior: Not defined.  
- Expected behavior: A central authorization layer checks role and ownership on every endpoint.

#### Acceptance Criteria

- [ ] Given any endpoint, then it checks the caller's role and ownership before returning data or changing state.  
- [ ] Given a request crafted to change scores, roles, badges or other users' content, then it is rejected with 403\.  
- [ ] Given the test suite, then each endpoint has authorization tests for visitor, student, other student and administrator.  
- [ ] Edge case: Object IDs in URLs cannot be used to access other users' resources (no insecure direct object references).

#### Scope

- In scope: Authorization layer and its tests.  
- Out of scope: Rate limiting (US-SEC-02).

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Critical  
- Story Points: 5



## \[User Story\] US-SEC-02 \- Rate limit public requests

```yaml
name: User Story
about: Capture a user story with acceptance criteria and planning details
title: "[User Story] US-SEC-02 - Rate limit public requests"
labels: ["user-story", "enhancement"]
assignees: []
```

&nbsp;

#### Story

As an administrator,  
I want public and sensitive endpoints to be rate limited,  
so that large-scale data extraction and abuse are prevented.

#### Context

- Problem: The role model says public requests may be rate limited to reduce scraping.  
- Current behavior: Not defined.  
- Expected behavior: Limits per IP for public endpoints and per account for sensitive actions.

#### Acceptance Criteria

- [ ] Given a client exceeding the limit on public endpoints, then further requests get 429 with a retry time.  
- [ ] Given login, password recovery, registration and reporting, then they have stricter limits.  
- [ ] Given a normal user browsing, then they never reach the limits.  
- [ ] Edge case: Limits are configurable without redeploying.

#### Scope

- In scope: Rate limiting of public and sensitive endpoints.  
- Out of scope: CAPTCHA.

#### Definition of Done

- [ ] Acceptance criteria met  
- [ ] Tests added/updated  
- [ ] Documentation updated (if needed)  
- [ ] Reviewed and approved

#### Estimation & Planning

- Priority: Medium  
- Story Points: 3



