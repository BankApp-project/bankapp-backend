package info.mackiewicz.bankapp.presentation.auth.recovery.password.controller;

import info.mackiewicz.bankapp.presentation.auth.recovery.password.controller.dto.PasswordChangeForm;
import info.mackiewicz.bankapp.presentation.auth.recovery.password.controller.dto.PasswordResetRequest;
import info.mackiewicz.bankapp.presentation.auth.recovery.password.service.PasswordResetAsyncService;
import info.mackiewicz.bankapp.presentation.auth.recovery.password.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DefaultPasswordResetController implements PasswordResetController {

    private final PasswordResetAsyncService passwordResetAsyncService;
    private final PasswordResetService passwordResetService;

    public ResponseEntity<Void> requestReset(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetAsyncService.requestReset(request.getEmail());
        return ResponseEntity.accepted().build();
    }

    public ResponseEntity<Void> completeReset(@Valid @RequestBody PasswordChangeForm request) {
        passwordResetService.completeReset(request);
        return ResponseEntity.ok().build();
    }
}
