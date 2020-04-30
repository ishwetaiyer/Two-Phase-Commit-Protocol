package cs223.coordination;

import cs223.message.MessageType;
import cs223.node.Node;
import cs223.node.NodeStatus;
import cs223.transaction.Statement;
import cs223.transaction.Transaction;
import cs223.transaction.TransactionStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

public interface Coordinator {
    Set<Node> getNodes();
    Future<MessageType> prepareNode(Integer nodeNumber, Long transactionId, List<Statement> statements);
    Future<Boolean> commitTransaction(Integer nodeNumber, Transaction transaction, MessageType commitOrAbort);
    void markTransactionComplete(Transaction transaction);

    void markTransactionFailed(Transaction transaction);

    void log(Long transactionId, TransactionStatus status);
    void log(Integer nodeNumber, Long transactionId, NodeStatus status);


    boolean failsAfterSendingPrepared();
}
