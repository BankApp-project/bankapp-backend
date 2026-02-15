package info.mackiewicz.bankapp.presentation.auth.registration.dto.demo;

import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class RegistrationRequestFactoryTest {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(ValidationConstants.EMAIL_PATTERN);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(ValidationConstants.PASSWORD_PATTERN);

    @Autowired
    private RegistrationRequestFactory requestFactory;

    @RepeatedTest(50)
    void generatedRequest_shouldAlwaysPassEmailAndPasswordValidation() {
        RegistrationRequestFactory.DemoUserData demoData = requestFactory.createDemoRegistrationRequest();
        RegistrationRequest request = demoData.request();

        String email = request.getEmail().toString();
        assertTrue(EMAIL_PATTERN.matcher(email).matches(),
                "Generated email does not match EMAIL_PATTERN: " + email);

        String password = request.getPassword();
        assertTrue(password.length() >= ValidationConstants.PASSWORD_MIN_LENGTH,
                "Generated password is too short: " + password);
        assertTrue(PASSWORD_PATTERN.matcher(password).matches(),
                "Generated password does not match PASSWORD_PATTERN: " + password);
    }
}
