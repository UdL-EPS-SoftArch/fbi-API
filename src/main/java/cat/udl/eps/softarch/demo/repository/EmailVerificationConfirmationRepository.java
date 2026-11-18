package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.EmailVerificationConfirmation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "email-verifications")
public interface EmailVerificationConfirmationRepository extends CrudRepository<EmailVerificationConfirmation, Long> {
}
