package info.mackiewicz.bankapp.system.demo.builder;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DemoTransferBuilderTest {

    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        // Use same bank code so it resolves as TRANSFER_INTERNAL
        Iban sourceIban = new Iban.Builder()
                .countryCode(CountryCode.PL)
                .bankCode("485")
                .branchCode("1123")
                .nationalCheckDigit("4")
                .accountNumber("0000000000010001")
                .build();

        Iban destIban = new Iban.Builder()
                .countryCode(CountryCode.PL)
                .bankCode("485")
                .branchCode("1123")
                .nationalCheckDigit("4")
                .accountNumber("0000000000020001")
                .build();

        sourceAccount = mock(Account.class);
        when(sourceAccount.getIban()).thenReturn(sourceIban);
        when(sourceAccount.getId()).thenReturn(1);

        destinationAccount = mock(Account.class);
        when(destinationAccount.getIban()).thenReturn(destIban);
        when(destinationAccount.getId()).thenReturn(2);
    }

    @Test
    void build_shouldCreateTransactionWithDoneStatusAndCustomDate() {
        LocalDateTime customDate = LocalDateTime.of(2025, 12, 15, 14, 30);

        Transaction tx = new DemoTransferBuilder()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(new BigDecimal("100.00"))
                .withTitle("Test transfer")
                .withDate(customDate)
                .build();

        assertNotNull(tx);
        assertEquals(TransactionStatus.DONE, tx.getStatus());
        assertEquals(customDate, tx.getDate());
        assertEquals(new BigDecimal("100.00"), tx.getAmount());
        assertEquals("Test transfer", tx.getTitle());
        assertEquals(sourceAccount, tx.getSourceAccount());
        assertEquals(destinationAccount, tx.getDestinationAccount());
    }

    @Test
    void build_shouldOverrideDefaultNewStatus() {
        Transaction tx = new DemoTransferBuilder()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(new BigDecimal("50.00"))
                .withTitle("Status test")
                .withDate(LocalDateTime.now())
                .build();

        // TransferBuilder sets status to NEW, DemoTransferBuilder should override to DONE
        assertEquals(TransactionStatus.DONE, tx.getStatus());
    }
}
