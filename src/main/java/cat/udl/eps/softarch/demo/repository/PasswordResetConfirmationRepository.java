package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.PasswordResetConfirmation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "password-reset-confirmations")
public interface PasswordResetConfirmationRepository extends CrudRepository<PasswordResetConfirmation, Long> {
}
