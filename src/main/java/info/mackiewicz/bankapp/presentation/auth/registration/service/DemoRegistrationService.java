package info.mackiewicz.bankapp.presentation.auth.registration.service;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationMapper;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.demo.DemoRegistrationResponse;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.demo.RegistrationRequestFactory;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.demo.RegistrationRequestFactory.DemoUserData;
import info.mackiewicz.bankapp.presentation.auth.registration.exception.DemoRegistrationException;
import info.mackiewicz.bankapp.system.demo.data.DemoDataGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for creating demo accounts with pre-generated transaction history.
 * Flow: create demo user → create account → generate 2-month history → return credentials.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DemoRegistrationService {

    private final RegistrationMapper registrationMapper;
    private final RegistrationRequestFactory requestFactory;
    private final UserService userService;
    private final AccountService accountService;
    private final DemoDataGenerator demoDataGenerator;

    /**
     * Creates a demo account with full transaction history.
     * No email/password input required - everything is auto-generated.
     *
     * @return credentials (username + plaintext password) for the demo account
     */
    @Transactional
    public DemoRegistrationResponse registerDemoUser() {
        try {
            log.info("Creating demo account");

            // 1. Generate demo user data
            DemoUserData demoData = requestFactory.createDemoRegistrationRequest();
            RegistrationRequest request = demoData.request();
            String plaintextPassword = demoData.plaintextPassword();

            // 2. Create user with demo flag set before persistence
            User user = registrationMapper.toUser(request);
            user.setDemo(true);
            User createdUser = userService.createUser(user);

            // 3. Create account (no welcome bonus, no welcome email)
            Account demoAccount = accountService.createAccount(createdUser.getId());

            // 4. Generate 2-month transaction history
            demoDataGenerator.generateTransactionHistory(demoAccount);

            log.info("Demo account created: username={}", createdUser.getUsername());

            return DemoRegistrationResponse.builder()
                    .withUsername(createdUser.getUsername())
                    .withPassword(plaintextPassword)
                    .build();

        } catch (Exception e) {
            throw new DemoRegistrationException("Demo account creation failed", e);
        }
    }
}
