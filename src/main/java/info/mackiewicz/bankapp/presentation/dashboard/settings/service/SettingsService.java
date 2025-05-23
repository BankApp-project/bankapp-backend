package info.mackiewicz.bankapp.presentation.dashboard.settings.service;

import info.mackiewicz.bankapp.core.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.interfaces.PersonalInfo;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.presentation.dashboard.settings.controller.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.settings.controller.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.presentation.dashboard.settings.controller.dto.UserSettingsDTO;
import info.mackiewicz.bankapp.presentation.dashboard.settings.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SettingsService {

    private final UserService userService;
    private final PasswordValidationService passwordValidationService;
    private final UsernameValidationService usernameValidationService;

    /**
     * Retrieves the user settings for the authenticated user.
     *
     * @param user The authenticated user
     * @return UserSettingsDTO containing user settings information
     */
    public UserSettingsDTO getUserSettings(PersonalInfo user) {
        return UserSettingsDTO.fromUser(user);
    }

    /**
     * Updates the user settings for the authenticated user.
     *
     * @param user        The authenticated user
     * @param request The new settings to be applied
     * @throws UserNotFoundException if the user does not exist
     * @throws InvalidPasswordException   if the current password is incorrect
     * @throws PasswordsMismatchException if the new password and confirmation do
     *                                    not match
     * @throws PasswordSameException      if the new password is the same as the old
     *                                    one
     */
    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        // Delegate all password validation to the PasswordValidationService
        passwordValidationService.validatePasswordChange(
                request.getCurrentPassword(),
                request.getPassword(),
                request.getConfirmPassword(),
                user.getPassword());

        // Set and encode the new password
        user.setPassword(request.getPassword());
        userService.updateUser(user);
        log.info("Changed password for user: {}", user.getUsername());
    }

    /**
     * Changes the username for the authenticated user.
     *
     * @param user    The authenticated user
     * @param request The request containing the new username
     * @throws UserNotFoundException if the user does not exist
     * @throws UsernameAlreadyTakenException if the new username is already taken
     * @throws UsernameSameException if the new username is the same as the old one
     * @throws ForbiddenUsernameException if the new username is forbidden
     */
    @Transactional
    public void changeUsername(User user, ChangeUsernameRequest request) {
        String oldUsername = user.getUsername();
        String newUsername = request.getNewUsername();

        // Validates username change
        usernameValidationService.validateUsername(newUsername, oldUsername);

        user.setUsername(newUsername);
        userService.updateUser(user);
        log.info("Changed username for user {}: {} -> {}", user.getId(), oldUsername, newUsername);
    }
}