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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DemoRegistrationServiceTest {

    private static final String TEST_USERNAME = "janusz.kielbasa123456";
    private static final String TEST_PASSWORD = "DemoHaslo1!";

    @Mock
    private RegistrationMapper registrationMapper;

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

        User mockUser = mock(User.class);
        when(registrationMapper.toUser(request)).thenReturn(mockUser);

        User createdUser = mock(User.class);
        when(createdUser.getId()).thenReturn(100);
        when(createdUser.getUsername()).thenReturn(TEST_USERNAME);
        when(userService.createUser(mockUser)).thenReturn(createdUser);

        Account mockAccount = mock(Account.class);
        when(accountService.createAccount(100)).thenReturn(mockAccount);

        // Act
        DemoRegistrationResponse result = demoRegistrationService.registerDemoUser();

        // Assert
        assertEquals(TEST_USERNAME, result.username());
        assertEquals(TEST_PASSWORD, result.password());

        verify(requestFactory).createDemoRegistrationRequest();
        verify(registrationMapper).toUser(request);
        verify(mockUser).setDemo(true);
        verify(userService).createUser(mockUser);
        verify(accountService).createAccount(100);
        verify(demoDataGenerator).generateTransactionHistory(mockAccount);
    }

    @Test
    void whenRegistrationFails_thenThrowDemoRegistrationException() {
        // Arrange
        RegistrationRequest request = mock(RegistrationRequest.class);
        DemoUserData demoData = new DemoUserData(request, TEST_PASSWORD);
        when(requestFactory.createDemoRegistrationRequest()).thenReturn(demoData);

        User mockUser = mock(User.class);
        when(registrationMapper.toUser(request)).thenReturn(mockUser);
        doThrow(RuntimeException.class).when(userService).createUser(any(User.class));

        // Act & Assert
        assertThrows(DemoRegistrationException.class, () -> demoRegistrationService.registerDemoUser());
    }
}
