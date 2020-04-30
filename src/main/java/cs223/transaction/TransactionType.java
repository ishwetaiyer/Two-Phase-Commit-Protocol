package cs223.transaction;

/**
 * TransactionType is used to help in producer/consumer signaling.
 * Producer produces a transaction, while consumers consume it. In this scenario,
 * we use the type {{@link #END} to signify that the producer has stopped producing.
 */
public enum TransactionType {
    NORMAL,
    END
}
