package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
public class Subject extends UriEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Length(max = 500)
    @Column(length = 500)
    private String description;

    @CreatedDate
    @DateTimeFormat
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    @LastModifiedDate
    @DateTimeFormat
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant updatedAt;
}
