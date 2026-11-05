package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;

@Entity
@Table(name = "DemoUser") //Avoid collision with system table User
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends UriEntity<Long> implements UserDetails {

	public enum Role {
		STUDENT, ADMIN
	}

	public static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(unique = true)
	private String username;

	// Private profile fields: only the owner and administrators can read them (2.4.7)
	@NotBlank
	@Email
	@Column(unique = true)
	@JsonSerialize(using = PrivateProfileFieldSerializer.class)
	private String email;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank
	@Length(min = 8, max = 256)
	private String password;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private boolean passwordReset;

	@JsonSerialize(using = PrivateProfileFieldSerializer.class)
	private String fullName;

	// Hidden from other users so that administrator accounts are not publicly discoverable
	@Enumerated(EnumType.STRING)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonSerialize(using = PrivateProfileFieldSerializer.class)
	private Role role = Role.STUDENT;

	private String profilePicture;

	@JsonSerialize(using = PrivateProfileFieldSerializer.class)
	private LocalDate birthDate;

	@CreatedDate
	@DateTimeFormat
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Instant createdAt;

	@LastModifiedDate
	@DateTimeFormat
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Instant updatedAt;

	public void encodePassword() {
		this.password = passwordEncoder.encode(this.password);
	}

	@Override
	@JsonValue(value = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonSerialize(using = PrivateProfileFieldSerializer.class)
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + role.name());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
