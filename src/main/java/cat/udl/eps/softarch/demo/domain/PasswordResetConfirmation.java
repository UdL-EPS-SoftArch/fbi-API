package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public class PasswordResetConfirmation extends PasswordResetCommand {
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String value) {
        this.token = value;
    }

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String value) {
        this.newPassword = value;
    }

}
