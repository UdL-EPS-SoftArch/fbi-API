package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.PasswordResetRequest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "password-reset-requests")
public interface PasswordResetRequestRepository extends CrudRepository<PasswordResetRequest, Long> {
}
