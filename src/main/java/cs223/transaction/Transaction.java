package cs223.transaction;

import java.util.List;

/**
 * A transaction is a wrapper around a bunch of statements. Every coordinator thread acts on a single transaction.
 * One transaction should only be handled by one thread at a time.
 */
public interface Transaction {
    /**
     * @return statements enclosed in a transaction.
     * @see Statement
     */
    List<Statement> getStatements();

    /**
     * @return the transaction type.
     * @see TransactionType
     */
    TransactionType type();

    /**
     * Should be unique for each transaction.
     */
    Long getId();
}
