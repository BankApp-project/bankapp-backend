package info.mackiewicz.bankapp.integration.presentation.dashboard.main.service;

import info.mackiewicz.bankapp.core.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.core.account.model.interfaces.DashboardAccountInfo;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.core.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestAccountService;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestConfig;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestUserService;
import info.mackiewicz.bankapp.presentation.dashboard.main.controller.dto.UserAccountsInfoResponse;
import info.mackiewicz.bankapp.presentation.dashboard.main.service.ApiDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(IntegrationTestConfig.class)
@AutoConfigureTestDatabase
@Transactional
class ApiDashboardServiceIntegrationTest {

    public static final BigDecimal DEFAULT_INITIAL_BALANCE = new BigDecimal("1000.00");

    @Autowired
    private ApiDashboardService apiDashboardService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IntegrationTestUserService testUserService;

    @Autowired
    private IntegrationTestAccountService testAccountService;

    @Autowired
    private AccountService accountService;


    private Account testAccount;
    private Account otherTestAccount;
    private User testUser;
    private User otherTestUser;

    // Helper methods for creating test data

    private void updateAccountBalance(Account account, BigDecimal balance) {
        TestAccountBuilder.setField(account, "balance", balance);
        accountRepository.save(account);
    }

    private Transaction createAndSaveTransaction(
            Account sourceAccount,
            Account destinationAccount,
            BigDecimal amount,
            TransactionStatus status) {

        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(amount);
        transaction.setStatus(status);
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setTitle("Test Transaction");
        transaction.setDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @BeforeEach
    void setUp() {
        // Prepare test data
        testUser = testUserService.createRandomTestUser();
        testAccount = testAccountService.createTestAccount(testUser.getId());

        otherTestUser = testUserService.createRandomTestUser();
        otherTestAccount = testAccountService.createTestAccount(otherTestUser.getId());
    }

    @Test
    @DisplayName("getAccountsInfo(): shouldReturnUserAccountsInfoWhenUserHasAccounts")
    void shouldReturnUserAccountsInfoWhenUserHasAccounts() {
        // given
        updateAccountBalance(testAccount, DEFAULT_INITIAL_BALANCE);

        int userId = testUser.getId();

        // when
        UserAccountsInfoResponse response = apiDashboardService.getAccountsInfo(userId);

        Optional<DashboardAccountInfo> accountInfo = response.getAccountsInfos().stream()
                .filter(info -> info.getId().equals(testAccount.getId()))
                .findFirst();

        //then
        assertEquals(response.getUserId(), userId);
        assertNotNull(response.getAccountsInfos(), "Accounts info list should not be null");
        assertTrue(accountInfo.isPresent());

        DashboardAccountInfo info = accountInfo.get();
        assertEquals(testAccount.getId(), info.getId());
        assertEquals(testUser.getFullName(), info.getOwnerFullname());
        assertEquals(testAccount.getFormattedIban(), info.getFormattedIban());
        assertEquals(DEFAULT_INITIAL_BALANCE, info.getBalance());
    }

    @Test
    @DisplayName("getAccountsInfo(): Should return empty list when user don't have any accounts")
    void shouldThrowExceptionWhenUserDontHaveAccounts() {
        User newUser = testUserService.createRandomTestUser();
        int userId = newUser.getId();

        // when
        UserAccountsInfoResponse response = apiDashboardService.getAccountsInfo(userId);

        Optional<DashboardAccountInfo> accountInfo = response.getAccountsInfos().stream()
                .filter(info -> info.getId().equals(testAccount.getId()))
                .findFirst();

        //then
        assertEquals(response.getUserId(), userId);
        assertTrue(newUser.getAccounts().isEmpty());
        assertTrue(accountInfo.isEmpty());
    }

    @Test
    @DisplayName("getAccountsInfo(): Should return only accounts for specific user")
    void shouldReturnOnlyAccountsForSpecificUser() {
        // Given
        User usr1 = testUserService.createRandomTestUser();
        User usr2 = testUserService.createRandomTestUser();
        Account testAcc1 = testAccountService.createTestAccount(usr1.getId());
        Account testAcc2 = testAccountService.createTestAccount(usr2.getId());
        Account testAcc3 = testAccountService.createTestAccount(usr1.getId());

        // When
        UserAccountsInfoResponse response = apiDashboardService.getAccountsInfo(usr1.getId());

        // Then
        assertThat(response.getUserId()).isEqualTo(usr1.getId());
        assertThat(response.getAccountsInfos()).hasSize(2);
        assertThat(response.getAccountsInfos())
                .extracting(DashboardAccountInfo::getId)
                .containsExactlyInAnyOrder(testAcc1.getId(), testAcc3.getId());
    }

    @Test
    @DisplayName("getWorkingBalance(): Should calculate working balance correctly")
    void shouldCalculateWorkingBalance() {
        // given
        updateAccountBalance(testAccount, DEFAULT_INITIAL_BALANCE);
        updateAccountBalance(otherTestAccount, DEFAULT_INITIAL_BALANCE);

        // Create a pending transaction
        createAndSaveTransaction(
                testAccount,
                otherTestAccount,
                new BigDecimal("300.00"),
                TransactionStatus.PENDING
        );

        // Create a new transaction
        createAndSaveTransaction(
                testAccount,
                otherTestAccount,
                new BigDecimal("200.00"),
                TransactionStatus.NEW
        );

        // Create a completed transaction (should not affect working balance)
        createAndSaveTransaction(
                testAccount,
                otherTestAccount,
                new BigDecimal("100.00"),
                TransactionStatus.DONE
        );

        // Create a faulty transaction (should not affect working balance)
        createAndSaveTransaction(
                testAccount,
                otherTestAccount,
                new BigDecimal("50"),
                TransactionStatus.VALIDATION_ERROR
        );

        BigDecimal expectedWorkingBalance = new BigDecimal("500.00"); // 1000 - 300 - 200

        // when
        BigDecimal workingBalance = apiDashboardService.getWorkingBalance(testAccount.getId());
        BigDecimal sourceAccRealBalance = accountRepository.findBalanceById(testAccount.getId()).get();
        BigDecimal destinationAccRealBalance = accountRepository.findBalanceById(otherTestAccount.getId()).get();
        // then
        assertThat(workingBalance).isEqualByComparingTo(expectedWorkingBalance);
        assertThat(sourceAccRealBalance).isEqualByComparingTo(DEFAULT_INITIAL_BALANCE);
        assertThat(destinationAccRealBalance).isEqualByComparingTo(DEFAULT_INITIAL_BALANCE);
    }

    @Test
    @DisplayName("getWorkingBalance(): Should throw exception when account not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        // given
        int nonExistentAccountId = 9999;

        // when & then
        assertThrows(AccountNotFoundByIdException.class, () ->
                apiDashboardService.getWorkingBalance(nonExistentAccountId)
        );
    }

    @Test
    @DisplayName("getWorkingBalance(): Should return correct working balance when no pending transactions exist")
    void shouldReturnCorrectWorkingBalanceWhenNoHoldTransactions() {
        // given
        updateAccountBalance(testAccount, DEFAULT_INITIAL_BALANCE);

        // Create only completed transactions (should not affect working balance)
        createAndSaveTransaction(
                testAccount,
                otherTestAccount,
                new BigDecimal("100.00"),
                TransactionStatus.DONE
        );

        createAndSaveTransaction(
                testAccount,
                otherTestAccount,
                new BigDecimal("200.00"),
                TransactionStatus.DONE
        );

        // when
        BigDecimal workingBalance = apiDashboardService.getWorkingBalance(testAccount.getId());

        // then
        assertThat(workingBalance).isEqualByComparingTo(DEFAULT_INITIAL_BALANCE);
    }

    @Test
    @DisplayName("getWorkingBalance(): Should handle multiple pending transactions")
    void shouldHandleMultiplePendingTransactions() {
        // given
        BigDecimal initialBalance = new BigDecimal("2000.00");
        updateAccountBalance(testAccount, initialBalance);

        // Create multiple pending transactions
        Transaction t1 = createAndSaveTransaction(testAccount, otherTestAccount, new BigDecimal("300.00"), TransactionStatus.PENDING);
        Transaction t2 = createAndSaveTransaction(testAccount, otherTestAccount, new BigDecimal("400.00"), TransactionStatus.NEW);
        Transaction t3 = createAndSaveTransaction(testAccount, otherTestAccount, new BigDecimal("500.00"), TransactionStatus.PENDING);
        Transaction t4 = createAndSaveTransaction(testAccount, otherTestAccount, new BigDecimal("100.00"), TransactionStatus.NEW);

        // Completed transaction (should not affect working balance)
        createAndSaveTransaction(testAccount, otherTestAccount, new BigDecimal("1000.00"), TransactionStatus.DONE);


        BigDecimal expectedHoldAmount = t1.getAmount().add(t2.getAmount()).add(t3.getAmount()).add(t4.getAmount());
        BigDecimal expectedWorkingBalance = initialBalance.subtract(expectedHoldAmount);

        // when
        BigDecimal workingBalance = apiDashboardService.getWorkingBalance(testAccount.getId());

        // then
        assertThat(workingBalance).isEqualByComparingTo(expectedWorkingBalance);
    }

    @Test
    @DisplayName("getWorkingBalance(): Should handle zero account balance")
    void shouldHandleZeroBalance() {
        // given
        BigDecimal initialBalance = BigDecimal.ZERO;
        updateAccountBalance(testAccount, initialBalance);

        // Create a pending transaction
        Transaction t1 = createAndSaveTransaction(testAccount, otherTestAccount, new BigDecimal("100.00"), TransactionStatus.PENDING);

        BigDecimal expectedWorkingBalance = initialBalance.subtract(t1.getAmount());

        // when
        BigDecimal workingBalance = apiDashboardService.getWorkingBalance(testAccount.getId());

        // then
        assertThat(workingBalance).isEqualByComparingTo(expectedWorkingBalance);
    }

    @Test
    @DisplayName("getWorkingBalance(): Should handle large amount transactions")
    void shouldHandleLargeAmounts() {
        // given
        BigDecimal initialBalance = new BigDecimal("10000000.00");
        updateAccountBalance(testAccount, initialBalance);

        // Create a pending transaction with a large amount
        Transaction t1 = createAndSaveTransaction(testAccount, otherTestAccount, new BigDecimal("9999999.99"), TransactionStatus.PENDING);

        BigDecimal expectedWorkingBalance = initialBalance.subtract(t1.getAmount());

        // when
        BigDecimal workingBalance = apiDashboardService.getWorkingBalance(testAccount.getId());

        // then
        assertThat(workingBalance).isEqualByComparingTo(expectedWorkingBalance);
    }

    @Test
    @DisplayName("getWorkingBalance(): Should correctly ignore incoming transactions")
    void shouldCorrectlyIgnoreIncomingTransactions() {
        // given
        BigDecimal initialBalance = new BigDecimal("1000.00");
        updateAccountBalance(testAccount, initialBalance);

        // Create an incoming transaction (PENDING status should not affect working balance)
        Transaction t1 = createAndSaveTransaction(otherTestAccount, testAccount, new BigDecimal("300.00"), TransactionStatus.PENDING);

        // Create an outgoing transaction (PENDING status should affect working balance)
        Transaction t2 = createAndSaveTransaction(testAccount, otherTestAccount, new BigDecimal("200.00"), TransactionStatus.PENDING);

        BigDecimal expectedWorkingBalance = initialBalance.subtract(t2.getAmount());

        // when
        BigDecimal workingBalance = apiDashboardService.getWorkingBalance(testAccount.getId());

        // then
        assertThat(workingBalance).isEqualByComparingTo(expectedWorkingBalance);
    }

    @ParameterizedTest
    @CsvSource({
            "1000.00, 300.00, 700.00",
            "500.50, 100.25, 400.25",
            "0.00, 0.00, 0.00",
            "1000.00, 0.00, 1000.00",
            "1000.00, 1000.00, 0.00",
            "1000.00, 1001.00, -1.00"
    })
    @DisplayName("getWorkingBalance(): Should handle various balance and hold amount combinations")
    void shouldHandleVariousBalanceAndHoldAmountCombinations(
            String initialBalanceStr, String holdAmountStr, String expectedBalanceStr) {
        // given
        BigDecimal initialBalance = new BigDecimal(initialBalanceStr);
        BigDecimal holdAmount = new BigDecimal(holdAmountStr);
        BigDecimal expectedWorkingBalance = new BigDecimal(expectedBalanceStr);

        updateAccountBalance(testAccount, initialBalance);

        if (holdAmount.compareTo(BigDecimal.ZERO) > 0) {
            createAndSaveTransaction(testAccount, otherTestAccount, holdAmount, TransactionStatus.PENDING);
        }

        // when
        BigDecimal workingBalance = apiDashboardService.getWorkingBalance(testAccount.getId());

        // then
        assertThat(workingBalance).isEqualByComparingTo(expectedWorkingBalance);
    }

    @Test
    @DisplayName("getWorkingBalance(): Should handle concurrent transaction status changes")
    void shouldHandleConcurrentTransactionStatusChanges() {
        // given
        updateAccountBalance(testAccount, DEFAULT_INITIAL_BALANCE);

        // Create a transaction in PENDING status
        Transaction pendingTransaction = createAndSaveTransaction(
                testAccount, otherTestAccount, new BigDecimal("300.00"), TransactionStatus.PENDING
        );

        // Calculate the working balance (should be 700.00)
        BigDecimal workingBalanceBefore = apiDashboardService.getWorkingBalance(testAccount.getId());

        // Update the transaction status to DONE
        pendingTransaction.setStatus(TransactionStatus.DONE);
        transactionRepository.save(pendingTransaction);

        // when
        BigDecimal workingBalanceAfter = apiDashboardService.getWorkingBalance(testAccount.getId());

        // then
        assertThat(workingBalanceBefore).isEqualByComparingTo(DEFAULT_INITIAL_BALANCE.subtract(pendingTransaction.getAmount()));
        assertThat(workingBalanceAfter).isEqualByComparingTo(DEFAULT_INITIAL_BALANCE);
    }

    @Test
    @DisplayName("getWorkingBalance(): Should handle account with negative balance")
    void shouldHandleNegativeBalance() {
        // given
        BigDecimal initialBalance = new BigDecimal("-500.00");
        updateAccountBalance(testAccount, initialBalance);

        createAndSaveTransaction(testAccount, otherTestAccount, new BigDecimal("200.00"), TransactionStatus.PENDING);

        BigDecimal expectedWorkingBalance = new BigDecimal("-700.00");

        // when
        BigDecimal workingBalance = apiDashboardService.getWorkingBalance(testAccount.getId());

        // then
        assertThat(workingBalance).isEqualByComparingTo(expectedWorkingBalance);
    }
}