package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
public class Test extends UriEntity<Long> {

    public enum TestType {
        OFFICIAL, CUSTOM
    }

    public enum Status {
        DRAFT, PUBLISHED, WITHDRAWN
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public static final int MAX_TAGS = 5;
    public static final int MAX_TAG_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Subject subject;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private User author;

    @NotBlank
    private String title;

    // Title without case or accents, for the catalog search (US-COM-04); kept in sync on save
    @JsonIgnore
    private String searchTitle;

    // Search metadata (US-EST-07, US-ADM-08, used by US-COM-04): optional difficulty and up to 5 tags
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Size(max = MAX_TAGS, message = "A test can have at most 5 tags")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "test_tag", joinColumns = @JoinColumn(name = "test_id"))
    @Column(name = "tag", length = MAX_TAG_LENGTH)
    private Set<@Length(max = MAX_TAG_LENGTH, message = "Each tag can have at most 30 characters") String> tags =
            new LinkedHashSet<>();

    @Length(max = 500)
    @Column(length = 500)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TestType testType = TestType.CUSTOM;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;

    private Integer year;

    @PositiveOrZero
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer maxScore = 0;

    @NotNull
    private Boolean isPublic = false;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant publishedAt;

    @CreatedDate
    @DateTimeFormat
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    @LastModifiedDate
    @DateTimeFormat
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant updatedAt;

    /** Tags are stored trimmed, in lowercase and without duplicates. */
    public void setTags(Collection<String> tags) {
        this.tags = SearchText.normalizeTags(tags);
    }

    @PrePersist
    @PreUpdate
    void updateSearchTitle() {
        searchTitle = SearchText.normalize(title);
    }
}
