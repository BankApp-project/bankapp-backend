package info.mackiewicz.bankapp.system.demo.data;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.core.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.system.demo.actor.ActorAccountProvider;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DemoDataGeneratorTest {

    private ActorAccountProvider actorAccountProvider;
    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private DemoDataGenerator generator;

    @Captor
    private ArgumentCaptor<List<Transaction>> transactionsCaptor;

    private Account demoAccount;
    private Account acmeCorpAccount;
    private Account landlordAccount;
    private Account starbugsAccount;
    private Account frogShopAccount;
    private Account kolegaMarekAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        actorAccountProvider = mock(ActorAccountProvider.class);
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        generator = new DemoDataGenerator(actorAccountProvider, transactionRepository, accountRepository);

        // Create mock accounts with different IBANs (same bank, different owners)
        demoAccount = createMockAccount(1, "0000000000100001");
        acmeCorpAccount = createMockAccount(-2, "0000000000020001");
        landlordAccount = createMockAccount(-3, "0000000000030001");
        starbugsAccount = createMockAccount(-4, "0000000000040001");
        frogShopAccount = createMockAccount(-5, "0000000000050001");
        kolegaMarekAccount = createMockAccount(-6, "0000000000060001");

        when(actorAccountProvider.getAcmeCorpAccount()).thenReturn(acmeCorpAccount);
        when(actorAccountProvider.getLandlordAccount()).thenReturn(landlordAccount);
        when(actorAccountProvider.getStarbugsAccount()).thenReturn(starbugsAccount);
        when(actorAccountProvider.getFrogShopAccount()).thenReturn(frogShopAccount);
        when(actorAccountProvider.getKolegaMarekAccount()).thenReturn(kolegaMarekAccount);

        when(transactionRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void generateTransactionHistory_shouldCreateTransactions() {
        generator.generateTransactionHistory(demoAccount);

        verify(transactionRepository).saveAll(transactionsCaptor.capture());
        List<Transaction> transactions = transactionsCaptor.getValue();

        assertFalse(transactions.isEmpty(), "Should generate at least some transactions");
        // 2 months ≈ 60 days of coffee + 2 salaries + 2 rents + ~18 frogshop + ~8 marek = ~90+ transactions
        assertTrue(transactions.size() > 50, "Should generate many transactions (got " + transactions.size() + ")");
    }

    @Test
    void generateTransactionHistory_allTransactionsShouldHaveDoneStatus() {
        generator.generateTransactionHistory(demoAccount);

        verify(transactionRepository).saveAll(transactionsCaptor.capture());
        List<Transaction> transactions = transactionsCaptor.getValue();

        for (Transaction tx : transactions) {
            assertEquals(TransactionStatus.DONE, tx.getStatus(),
                    "All demo transactions should have DONE status");
        }
    }

    @Test
    void generateTransactionHistory_allTransactionsShouldHaveDatesInPast() {
        generator.generateTransactionHistory(demoAccount);

        verify(transactionRepository).saveAll(transactionsCaptor.capture());
        List<Transaction> transactions = transactionsCaptor.getValue();

        LocalDateTime now = LocalDateTime.now().plusMinutes(1); // small buffer
        LocalDate twoMonthsAgo = LocalDate.now().minusMonths(2).minusDays(1);

        for (Transaction tx : transactions) {
            assertNotNull(tx.getDate(), "Transaction should have a date");
            assertTrue(tx.getDate().isBefore(now), "Transaction date should be in the past");
            assertTrue(tx.getDate().toLocalDate().isAfter(twoMonthsAgo),
                    "Transaction date should be within 2 months");
        }
    }

    @Test
    void generateTransactionHistory_shouldSetFinalBalanceOnAccount() {
        generator.generateTransactionHistory(demoAccount);

        verify(demoAccount).setBalance(any(BigDecimal.class));
        verify(accountRepository).save(demoAccount);
    }

    @Test
    void generateTransactionHistory_balanceShouldReflectTransactions() {
        generator.generateTransactionHistory(demoAccount);

        verify(transactionRepository).saveAll(transactionsCaptor.capture());
        List<Transaction> transactions = transactionsCaptor.getValue();

        // Calculate expected balance: salary is incoming, rest is outgoing
        BigDecimal expectedBalance = BigDecimal.ZERO;
        for (Transaction tx : transactions) {
            if (tx.getDestinationAccount() == demoAccount) {
                expectedBalance = expectedBalance.add(tx.getAmount());
            } else if (tx.getSourceAccount() == demoAccount) {
                expectedBalance = expectedBalance.subtract(tx.getAmount());
            }
        }

        ArgumentCaptor<BigDecimal> balanceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(demoAccount).setBalance(balanceCaptor.capture());

        assertEquals(0, expectedBalance.compareTo(balanceCaptor.getValue()),
                "Final balance should match sum of all transactions");
    }

    @Test
    void generateTransactionHistory_shouldHaveAllTransactionTypes() {
        generator.generateTransactionHistory(demoAccount);

        verify(transactionRepository).saveAll(transactionsCaptor.capture());
        List<Transaction> transactions = transactionsCaptor.getValue();

        // Check that we have incoming (salary) and outgoing (rent, coffee, etc.)
        boolean hasIncoming = transactions.stream()
                .anyMatch(tx -> tx.getDestinationAccount() == demoAccount);
        boolean hasOutgoing = transactions.stream()
                .anyMatch(tx -> tx.getSourceAccount() == demoAccount);

        assertTrue(hasIncoming, "Should have incoming transactions (salary)");
        assertTrue(hasOutgoing, "Should have outgoing transactions (rent, coffee, etc.)");
    }

    private Account createMockAccount(int id, String accountNumber) {
        Iban iban = new Iban.Builder()
                .countryCode(CountryCode.PL)
                .bankCode("485")
                .branchCode("1123")
                .nationalCheckDigit("4")
                .accountNumber(accountNumber)
                .build();

        Account account = mock(Account.class);
        when(account.getId()).thenReturn(id);
        when(account.getIban()).thenReturn(iban);
        return account;
    }
}
