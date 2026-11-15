package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
public class Report extends UriEntity<Long> {

    public enum Status {
        PENDING, IN_REVIEW, RESOLVED, DISMISSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private User reporter;

    // Both null for a general platform report, only question null for a test report
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Test test;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Question question;

    @NotBlank
    private String reason;

    @Length(max = 2000)
    @Column(length = 2000)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User resolvedBy;

    @CreatedDate
    @DateTimeFormat
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    @DateTimeFormat
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant resolvedAt;
}
