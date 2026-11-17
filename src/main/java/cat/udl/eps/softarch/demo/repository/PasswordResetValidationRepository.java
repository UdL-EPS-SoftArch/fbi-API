package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.PasswordResetValidation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "password-reset-validations")
public interface PasswordResetValidationRepository extends CrudRepository<PasswordResetValidation, Long> {
}
