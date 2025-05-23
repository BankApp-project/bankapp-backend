package info.mackiewicz.bankapp.system.transaction.processing.error;

import info.mackiewicz.bankapp.core.transaction.model.Transaction;

/**
 * Observer interface for transaction error notifications
 */
public interface TransactionErrorObserver {
    /**
     * Called when a transaction encounters an error
     * 
     * @param transaction The transaction that encountered the error
     * @param error The exception that was thrown
     */
    void onTransactionError(Transaction transaction, Exception error);
}