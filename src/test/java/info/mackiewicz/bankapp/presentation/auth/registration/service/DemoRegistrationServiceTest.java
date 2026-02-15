package info.mackiewicz.bankapp.presentation.auth.registration.service;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.demo.DemoRegistrationResponse;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.demo.RegistrationRequestFactory;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.demo.RegistrationRequestFactory.DemoUserData;
import info.mackiewicz.bankapp.presentation.auth.registration.exception.DemoRegistrationException;
import info.mackiewicz.bankapp.system.demo.data.DemoDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DemoRegistrationServiceTest {

    private static final String TEST_USERNAME = "janusz.kielbasa123456";
    private static final String TEST_PASSWORD = "DemoHaslo1!";

    @Mock
    private RegistrationService registrationService;

    @Mock
    private RegistrationRequestFactory requestFactory;

    @Mock
    private UserService userService;

    @Mock
    private AccountService accountService;

    @Mock
    private DemoDataGenerator demoDataGenerator;

    @InjectMocks
    private DemoRegistrationService demoRegistrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenRegisterDemoUser_thenReturnCredentials() {
        // Arrange
        RegistrationRequest request = mock(RegistrationRequest.class);
        DemoUserData demoData = new DemoUserData(request, TEST_PASSWORD);
        when(requestFactory.createDemoRegistrationRequest()).thenReturn(demoData);

        RegistrationResponse registrationResponse = RegistrationResponse.builder()
                .withUsername(TEST_USERNAME)
                .withFirstname("Janusz")
                .withLastname("Kielbasa")
                .withEmail("janusz@demo.bankapp")
                .build();
        when(registrationService.registerUser(any(RegistrationRequest.class))).thenReturn(registrationResponse);

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(100);
        when(userService.getUserByUsername(TEST_USERNAME)).thenReturn(mockUser);
        when(userService.updateUser(any(User.class))).thenReturn(mockUser);

        Account mockAccount = mock(Account.class);
        when(accountService.getAccountsByOwnersId(100)).thenReturn(List.of(mockAccount));

        // Act
        DemoRegistrationResponse result = demoRegistrationService.registerDemoUser();

        // Assert
        assertEquals(TEST_USERNAME, result.username());
        assertEquals(TEST_PASSWORD, result.password());

        verify(requestFactory).createDemoRegistrationRequest();
        verify(registrationService).registerUser(request);
        verify(mockUser).setDemo(true);
        verify(userService).updateUser(mockUser);
        verify(demoDataGenerator).generateTransactionHistory(mockAccount);
    }

    @Test
    void whenRegistrationFails_thenThrowDemoRegistrationException() {
        // Arrange
        RegistrationRequest request = mock(RegistrationRequest.class);
        DemoUserData demoData = new DemoUserData(request, TEST_PASSWORD);
        when(requestFactory.createDemoRegistrationRequest()).thenReturn(demoData);
        doThrow(RuntimeException.class).when(registrationService).registerUser(any(RegistrationRequest.class));

        // Act & Assert
        assertThrows(DemoRegistrationException.class, () -> demoRegistrationService.registerDemoUser());
    }
}
