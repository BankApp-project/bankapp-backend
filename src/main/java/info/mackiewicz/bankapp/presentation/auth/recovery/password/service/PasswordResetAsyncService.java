package info.mackiewicz.bankapp.presentation.auth.recovery.password.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetAsyncService {

    private final PasswordResetService passwordResetService;

    @Async("taskExecutor")
    public void requestReset(String email) {
        try {
            passwordResetService.requestReset(email);
        } catch (Exception e) {
            log.error("Async password reset request failed", e);
        }
    }
}
