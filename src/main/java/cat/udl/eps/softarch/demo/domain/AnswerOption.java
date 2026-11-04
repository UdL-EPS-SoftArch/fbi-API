package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class AnswerOption extends UriEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Question question;

    @NotBlank
    @Length(max = 2000)
    @Column(length = 2000)
    private String optionText;

    // Never returned by the API, so the correct option is not revealed before submitting
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean isCorrect = false;

    private Integer orderIndex;
}
