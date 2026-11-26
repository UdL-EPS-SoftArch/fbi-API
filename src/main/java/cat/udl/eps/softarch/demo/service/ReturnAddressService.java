package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Return to the previous point after authentication (US-AUT-05): decides where the frontend
 * sends the user after logging in. Only internal paths are accepted (no open redirect) and a
 * test that is no longer available sends the user to the home page with a notice.
 */
@Service
public class ReturnAddressService {
    public static final String HOME_PAGE = "/";
    public static final String CONTENT_UNAVAILABLE = "The content you were looking for is no longer available";

    // A single leading "/" followed by safe path/query characters: rejects "//host", "/\host",
    // absolute URLs, "javascript:" and any other scheme
    private static final Pattern INTERNAL_PATH = Pattern.compile("^/(?![/\\\\])[A-Za-z0-9\\-._~/?=&%]*$");
    private static final Pattern TEST_PAGE = Pattern.compile("^/tests/(\\d{1,18})(?:[/?].*)?$");
    private static final int MAX_LENGTH = 2048;

    private final TestRepository testRepository;
    private final UserRepository userRepository;

    public ReturnAddressService(TestRepository testRepository, UserRepository userRepository) {
        this.testRepository = testRepository;
        this.userRepository = userRepository;
    }

    public record Destination(String redirectTo, String notice) {
        static Destination home() {
            return new Destination(HOME_PAGE, null);
        }
    }

    /** True for addresses inside the platform; anything else must be ignored. */
    public static boolean isInternal(String address) {
        return address != null && address.length() <= MAX_LENGTH && INTERNAL_PATH.matcher(address).matches();
    }

    /**
     * Where to go after logging in: the requested address or, when none is sent, the one saved
     * at registration. The saved one is used only once.
     */
    @Transactional
    public Destination afterLogin(User user, String requested) {
        String saved = userRepository.findByUsername(user.getUsername())
                .map(stored -> {
                    String returnTo = stored.getReturnTo();
                    if (returnTo != null) {
                        stored.setReturnTo(null);
                        userRepository.save(stored);
                    }
                    return returnTo;
                })
                .orElse(null);
        return resolve(requested == null || requested.isBlank() ? saved : requested);
    }

    private Destination resolve(String address) {
        if (!isInternal(address)) {
            return Destination.home();
        }
        Matcher testPage = TEST_PAGE.matcher(address);
        // Same visibility as the test detail page, for the user that has just logged in
        if (testPage.matches() && testRepository.findById(Long.valueOf(testPage.group(1))).isEmpty()) {
            return new Destination(HOME_PAGE, CONTENT_UNAVAILABLE);
        }
        return new Destination(address, null);
    }
}
