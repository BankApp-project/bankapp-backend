package info.mackiewicz.bankapp.system.demo.builder;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Builder for demo transactions that bypasses normal processing pipeline.
 * Uses composition over TransferBuilder to set custom date and DONE status
 * (seed data, not real transactions).
 */
public class DemoTransferBuilder {

    private Account sourceAccount;
    private Account destinationAccount;
    private BigDecimal amount;
    private String title;
    private LocalDateTime date;

    public DemoTransferBuilder from(Account account) {
        this.sourceAccount = account;
        return this;
    }

    public DemoTransferBuilder to(Account account) {
        this.destinationAccount = account;
        return this;
    }

    public DemoTransferBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public DemoTransferBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public DemoTransferBuilder withDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public Transaction build() {
        Transaction transaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(amount)
                .withTitle(title)
                .build();
        transaction.setDate(date);
        transaction.setStatus(TransactionStatus.DONE);
        return transaction;
    }
}
