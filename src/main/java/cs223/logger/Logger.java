package cs223.logger;

import cs223.transaction.TransactionStatus;

public interface Logger {
    void log(Long id, TransactionStatus complete);
}
