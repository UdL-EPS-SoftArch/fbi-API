package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Notification;
import cat.udl.eps.softarch.demo.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface NotificationRepository extends CrudRepository<Notification, Long>, PagingAndSortingRepository<Notification, Long> {

    String OWNERSHIP_CLAUSE =
        "n.recipient.username = ?#{authentication.name} " +
        "OR ?#{hasRole('ADMIN')} = true";

    @Override
    @Query("SELECT n FROM Notification n WHERE " + OWNERSHIP_CLAUSE)
    Page<Notification> findAll(Pageable pageable);

    @Override
    @Query("SELECT n FROM Notification n WHERE n.id = :id AND (" + OWNERSHIP_CLAUSE + ")")
    Optional<Notification> findById(@Param("id") Long id);

    @Query("SELECT n FROM Notification n WHERE n.recipient = :user AND (" + OWNERSHIP_CLAUSE + ")")
    Page<Notification> findByRecipient(@Param("user") User recipient, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.recipient = :user AND n.seen = false AND (" + OWNERSHIP_CLAUSE + ")")
    Page<Notification> findUnseenByRecipient(@Param("user") User recipient, Pageable pageable);
}
