package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.PasswordChange;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "password-changes")
public interface PasswordChangeRepository extends CrudRepository<PasswordChange, Long> {
    @Override
    @RestResource(exported = false)
    java.util.Optional<PasswordChange> findById(Long id);

    @Override
    @RestResource(exported = false)
    Iterable<PasswordChange> findAll();

    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    @Override
    @RestResource(exported = false)
    void delete(PasswordChange entity);
}
