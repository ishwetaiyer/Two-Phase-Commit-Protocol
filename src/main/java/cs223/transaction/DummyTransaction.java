package cs223.transaction;

import java.util.List;

public class DummyTransaction implements Transaction {
    TransactionType type;
    Long id;

    public DummyTransaction(TransactionType type, Long id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public List<Statement> getStatements() {
        return null;
    }

    @Override
    public TransactionType type() {
        return type;
    }

    @Override
    public Long getId() {
        return id;
    }
}
