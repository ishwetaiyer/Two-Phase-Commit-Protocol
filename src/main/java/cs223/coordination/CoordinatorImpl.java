package cs223.coordination;

import cs223.logger.Logger;
import cs223.message.MessageType;
import cs223.node.Node;
import cs223.node.NodeStatus;
import cs223.transaction.Statement;
import cs223.transaction.Transaction;
import cs223.transaction.TransactionStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static cs223.message.MessageType.ABORT;
import static cs223.message.MessageType.COMMIT;

public class CoordinatorImpl implements Coordinator {

    ExecutorService service = Executors.newFixedThreadPool(10);
    ConcurrentMap<Long, Information> protocolDatabase;
    Logger logger;
    HashMap<Integer, Node> idToNodes;

    @Override
    public Set<Node> getNodes() {
        return new HashSet<>(idToNodes.values());
    }

    @Override
    public Future<MessageType> prepareNode(Integer nodeNumber, Long transactionId, List<Statement> statements) {
        Node node = idToNodes.get(nodeNumber);
        if (node == null) {
            return service.submit(() -> ABORT);
        }
        return node.prepareTransaction(transactionId, statements);
    }

    @Override
    public Future<Boolean> commitTransaction(Integer nodeNumber, Transaction transaction, MessageType commitOrAbort) {
        Node node = idToNodes.get(nodeNumber);
        if (node == null) {
            return service.submit(() -> Boolean.FALSE);
        }
        return node.commitTransaction(transaction.getId(), commitOrAbort == COMMIT);
    }

    @Override
    public void markTransactionComplete(Transaction transaction) {
        logger.log(transaction.getId(), TransactionStatus.COMPLETE);
    }

    @Override
    public void markTransactionFailed(Transaction transaction) {
        logger.log(transaction.getId(), TransactionStatus.FAILED);
    }

    @Override
    public void log(Long transactionId, TransactionStatus status) {

    }

    @Override
    public void log(Integer nodeNumber, Long transactionId, NodeStatus status) {

    }

    @Override
    public boolean failsAfterSendingPrepared() {
        return false;
    }

}
