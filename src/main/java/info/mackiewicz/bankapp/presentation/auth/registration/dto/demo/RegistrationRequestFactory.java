package info.mackiewicz.bankapp.presentation.auth.registration.dto.demo;

import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Creates registration requests for demo users with funny random names and passwords.
 */
@Component
@RequiredArgsConstructor
public class RegistrationRequestFactory {

    private static final Random RANDOM = new Random();
    private static final int PESEL_LENGTH = 11;
    private static final int PHONE_NUMBER_LENGTH = 9;
    private static final int MAX_PHONE_NUMBER = 999999999;
    private static final int RANDOM_DIGIT_BOUND = 10;
    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);

    private static final String[] FIRSTNAMES = {
            "Janusz", "Grazyna", "Seba", "Karyna", "Brajan",
            "Dżesika", "Krzysio", "Bozena", "Mirek", "Halina",
    };

    private static final String[] LASTNAMES = {
            "Kowalski", "Nowak", "Stonoga", "Kielbasa", "Beczka",
            "Ziemniak", "Parówka", "Kaszanka", "Pierogi", "Bigos",
    };

    @Value("${bankapp.demo.passwords:DemoHaslo1!,DemoHaslo2@,DemoHaslo3$,DemoHaslo4%,DemoHaslo5&,DemoHaslo6*,DemoHaslo7!,DemoHaslo8@,DemoHaslo9?,DemoHaslo0!}")
    private List<String> demoPasswords;

    private static String stripDiacritics(String input) {
        String normalized = Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    private static String generateRandomPesel() {
        StringBuilder pesel = new StringBuilder();
        for (int i = 0; i < PESEL_LENGTH; i++) {
            pesel.append(RANDOM.nextInt(RANDOM_DIGIT_BOUND));
        }
        return pesel.toString();
    }

    private static String generateRandomPhoneNumber() {
        return String.format("%0" + PHONE_NUMBER_LENGTH + "d", RANDOM.nextInt(MAX_PHONE_NUMBER));
    }

    private static String randomChoice(String[] options) {
        return options[RANDOM.nextInt(options.length)];
    }

    /**
     * Creates a demo registration request with a random funny name and password.
     *
     * @return a RegistrationRequest with random demo data and the selected password
     */
    public DemoUserData createDemoRegistrationRequest() {
        String firstname = randomChoice(FIRSTNAMES);
        String lastname = randomChoice(LASTNAMES);
        String password = demoPasswords.get(RANDOM.nextInt(demoPasswords.size()));

        // Generate unique email to avoid conflicts
        // Strip diacritics (ż→z, ó→o, etc.) to ensure ASCII-only email local part
        String uniqueSuffix = String.valueOf(System.nanoTime() % 1000000);
        String email = stripDiacritics(firstname) + "." + stripDiacritics(lastname) + uniqueSuffix + "@demo.bankapp";

        RegistrationRequest request = new RegistrationRequest();
        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setEmail(email);
        request.setPassword(password);
        request.setConfirmPassword(password);
        request.setDateOfBirth(DEFAULT_DATE_OF_BIRTH);
        request.setPesel(generateRandomPesel());
        request.setPhoneNumber(generateRandomPhoneNumber());

        return new DemoUserData(request, password);
    }

    /**
     * Bundles a registration request with the plaintext password for returning to the user.
     */
    public record DemoUserData(RegistrationRequest request, String plaintextPassword) {
    }
}
