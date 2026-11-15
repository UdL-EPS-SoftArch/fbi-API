package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public class PasswordResetRequest extends PasswordResetCommand {
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

}
