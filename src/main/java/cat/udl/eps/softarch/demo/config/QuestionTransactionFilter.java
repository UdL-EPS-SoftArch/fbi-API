package cat.udl.eps.softarch.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/** Keeps the repository operation and its content updates in one transaction. */
public class QuestionTransactionFilter extends OncePerRequestFilter {
    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private final PlatformTransactionManager transactionManager;

    public QuestionTransactionFilter(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean questionWrite = WRITE_METHODS.contains(request.getMethod())
                && (path.equals("/questions") || path.matches("/questions/[^/]+"));
        boolean attemptUpdate = Set.of("PUT", "PATCH").contains(request.getMethod())
                && path.matches("/testAttempts/[^/]+");
        return !questionWrite && !attemptUpdate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            chain.doFilter(request, response);
            if (response.getStatus() >= 400) {
                transactionManager.rollback(transaction);
            } else {
                transactionManager.commit(transaction);
            }
        } catch (IOException | ServletException | RuntimeException | Error failure) {
            if (!transaction.isCompleted()) {
                transactionManager.rollback(transaction);
            }
            throw failure;
        }
    }
}
