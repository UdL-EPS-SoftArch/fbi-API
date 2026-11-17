package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface UserRepository extends CrudRepository<User, Long>, PagingAndSortingRepository<User, Long> {

  // Administrator accounts are not publicly discoverable: listings only show them to
  // themselves and to other administrators. Private profile fields are hidden on
  // serialization (see PrivateProfileFieldSerializer).
  String DISCOVERABLE_CLAUSE =
      "u.role <> 'ADMIN' " +
      "OR u.username = ?#{authentication.name} " +
      "OR ?#{hasRole('ADMIN')} = true";

  @Override
  @Query("SELECT u FROM User u WHERE " + DISCOVERABLE_CLAUSE)
  Page<User> findAll(Pageable pageable);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.username = :username")
  @RestResource(exported = false)
  Optional<User> findByUsernameForUpdate(@Param("username") String username);

  // Used for authentication (BasicUserDetailsService): must not be filtered
  Optional<User> findByUsername(@Param("username") String username);

  @RestResource(exported = false)
  Optional<User> findByEmail(String email);

  // Login accepts the email or the username (US-AUT-02); not filtered, internal use only
  @RestResource(exported = false)
  default Optional<User> findByLoginIdentifier(String identifier) {
    if (identifier == null || identifier.isBlank()) {
      return Optional.empty();
    }
    String trimmed = identifier.trim();
    return findByEmail(trimmed).or(() -> findByUsername(trimmed));
  }

  boolean existsByUsername(@Param("username") String username);

  @Query("SELECT u FROM User u WHERE u.username LIKE CONCAT('%', :text, '%') AND (" + DISCOVERABLE_CLAUSE + ")")
  List<User> findByUsernameContaining(@Param("text") String text);
}
