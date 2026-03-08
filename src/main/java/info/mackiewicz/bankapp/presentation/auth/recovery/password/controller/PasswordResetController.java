package info.mackiewicz.bankapp.presentation.auth.recovery.password.controller;

import info.mackiewicz.bankapp.presentation.auth.recovery.password.controller.dto.PasswordChangeForm;
import info.mackiewicz.bankapp.presentation.auth.recovery.password.controller.dto.PasswordResetRequest;
import info.mackiewicz.bankapp.system.error.handling.dto.BaseApiError;
import info.mackiewicz.bankapp.system.error.handling.dto.ValidationApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * PasswordResetController defines the endpoints for managing password reset functionality.
 * This interface is responsible for handling user requests to initiate and complete the
 * password reset process.
 * <p>
 * It includes operations for:
 * - Requesting a password reset by providing an email address.
 * - Completing the password reset by submitting a token and new password.
 */
@SecurityRequirements
@Tag(name = "Password Reset")
@RequestMapping("/api/public")
public interface PasswordResetController {

    @Operation(
            summary = "Initiate password reset process (asynchronous)",
            description = """
                    Starts the password reset flow by accepting a user's email address.
                    
                    The request is accepted and handled asynchronously in the background.
                    If a syntactically valid email address is provided, an email containing a password reset link *may* be sent to that address.
                    
                    **Security Note:** To prevent email address enumeration, this endpoint **always returns a 202 Accepted** response, regardless of whether the provided email address exists in the system.<br>
                    A 202 Accepted response indicates only that the request was accepted for processing. It does not confirm whether the email address is registered or if an email was dispatched.
                    
                    Provided email must pass this pattern: 
                    ```
                    ^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$
                    ```
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = """
                            Request accepted for asynchronous processing. 
                            If the provided email address is registered and syntactically valid, 
                            a password reset link *may* be sent. 
                            (Always returns 202 Accepted for security reasons, regardless of email existence.)
                            """
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request: The provided email address is either missing or syntactically incorrect.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while accepting the request.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))
            )
    })
    @PostMapping("/password-reset-requests")
    ResponseEntity<Void> requestReset(@Valid @RequestBody PasswordResetRequest request);

    @Operation(
            summary = "Complete password reset",
            description = """
                    Completes the password reset flow initiated via `/password-reset-requests`.
                    
                    This endpoint requires a password reset token (from the reset email link) and the user's new password in the request body (`PasswordChangeForm`).
                    
                    If the token is valid and the password update succeeds, password reset is completed immediately.
                    A confirmation email is sent asynchronously after successful completion.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset completed successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload (e.g., password mismatch, weak password, missing fields).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "TOKEN_NOT_FOUND: Reset link could not be found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "TOKEN_EXPIRED or TOKEN_USED: Reset link is expired or already used.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during password reset.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))
            )
    })
    @PostMapping("/password-reset-confirmations")
    ResponseEntity<Void> completeReset(@Valid @RequestBody PasswordChangeForm request);
}
