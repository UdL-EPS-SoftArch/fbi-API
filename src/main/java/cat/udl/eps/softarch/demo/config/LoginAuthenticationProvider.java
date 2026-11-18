package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.service.LoginThrottlingService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Email (or username) and password login (US-AUT-02), with brute-force throttling. The
 * account status (unverified email, suspended) is only revealed once the password is right,
 * so that wrong credentials always get the same generic answer.
 */
@Component
public class LoginAuthenticationProvider implements AuthenticationProvider {
    private final DaoAuthenticationProvider delegate;
    private final LoginThrottlingService throttling;

    public LoginAuthenticationProvider(BasicUserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                                       LoginThrottlingService throttling) {
        this.throttling = throttling;
        this.delegate = new DaoAuthenticationProvider(userDetailsService);
        delegate.setPasswordEncoder(passwordEncoder);
        delegate.setPreAuthenticationChecks(user -> { });
        delegate.setPostAuthenticationChecks(LoginAuthenticationProvider::checkAccountStatus);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accountKey = throttling.accountKey(authentication.getName());
        if (accountKey == null) {
            throw new BadCredentialsException("BAD_CREDENTIALS");
        }
        throttling.checkNotThrottled(accountKey);
        try {
            Authentication result = delegate.authenticate(authentication);
            throttling.recordSuccess(accountKey);
            return result;
        } catch (BadCredentialsException exception) {
            throttling.recordFailure(accountKey);
            throw exception;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return delegate.supports(authentication);
    }

    private static void checkAccountStatus(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new LockedException("ACCOUNT_SUSPENDED");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("EMAIL_NOT_VERIFIED");
        }
    }
}
