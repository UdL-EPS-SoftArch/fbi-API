package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

/** Open the verification link: the token received by email verifies the account (US-AUT-07). */
@Entity
public class EmailVerificationConfirmation extends EmailVerificationCommand {
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String value) {
        this.token = value;
    }
}
