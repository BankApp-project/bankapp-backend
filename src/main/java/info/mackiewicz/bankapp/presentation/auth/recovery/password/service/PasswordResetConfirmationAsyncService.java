package info.mackiewicz.bankapp.presentation.auth.recovery.password.service;

import info.mackiewicz.bankapp.system.notification.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetConfirmationAsyncService {

    private final EmailService emailService;

    @Async("taskExecutor")
    public void sendPasswordResetConfirmation(String email, String fullNameOfUser) {
        try {
            emailService.sendPasswordResetConfirmation(email, fullNameOfUser);
        } catch (Exception e) {
            log.error("Async password reset confirmation email failed for: {}", email, e);
        }
    }
}
