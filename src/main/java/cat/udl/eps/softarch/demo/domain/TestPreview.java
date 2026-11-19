package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class TestPreview extends UriEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Test test;

    @Transient
    private List<PreviewAnswer> answers;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer score;

    @Data
    public static class PreviewAnswer {
        private Long questionId;
        private Long selectedOptionId;
    }
}
