package info.mackiewicz.bankapp.presentation.auth.registration.dto.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.shared.annotations.Password;
import info.mackiewicz.bankapp.shared.annotations.PasswordConfirmation;
import info.mackiewicz.bankapp.shared.annotations.PasswordMatches;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Registration request DTO for demo users")
@PasswordMatches
@AllArgsConstructor
@NoArgsConstructor
public class DemoRegistrationRequest implements PasswordConfirmation {

    @NotBlank(message = "Email is required")
    @Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "Invalid email format")
    @Schema(description = "Email must be a valid email address", example = "john.smith@example.com")
    @JsonProperty("email")
    private String email;

    @Password
    @Schema(description = "Password must be at least 8 characters long, contain at least one digit, " +
            "one lowercase letter, one uppercase letter, and one special character from the set: @$!%*?&", minLength = 8, pattern = ValidationConstants.PASSWORD_PATTERN, example = "StrongP@ss123")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Password confirmation must match the password", example = "StrongP@ss123")
    private String confirmPassword;

    @JsonIgnore
    public EmailAddress getEmail() {
        return new EmailAddress(email);
    }
}
