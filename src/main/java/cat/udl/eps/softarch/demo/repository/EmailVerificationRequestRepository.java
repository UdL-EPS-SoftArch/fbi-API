package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.EmailVerificationRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "email-verification-requests")
public interface EmailVerificationRequestRepository extends CrudRepository<EmailVerificationRequest, Long> {
}
