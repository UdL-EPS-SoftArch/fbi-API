package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

/** Resend the verification link to an email (US-AUT-07). */
@Entity
public class EmailVerificationRequest extends EmailVerificationCommand {
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
