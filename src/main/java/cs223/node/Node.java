package cs223.node;

import cs223.message.MessageType;
import cs223.transaction.Statement;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Node is the common interface.
 */
public interface Node {
    /**
     * @return type of node. Used for logging only.
     */
    NodeType type();

    /**
     * Assigned when a node is created by the coordinator. Should be unique for each node.
     *
     * @return
     */
    default Integer nodeNumber() {
        return 0;
    }

    /**
     * Used by coordinator to prepare nodes for transaction. Multi-threaded.
     *
     * @param transactionId The transaction in question
     * @param statements    The statements in a transaction which are to be prepared by this
     * @return A future of #MessageType.
     */
    Future<MessageType> prepareTransaction(Long transactionId, List<Statement> statements);

    /**
     * Used by coordinator to send a COMMIT transaction for each node.
     *
     * @param transactionId The transaction in question
     * @return A boolean returning whether the commit was successful or not. Used by coordinator for logging.
     */
    Future<Boolean> commitTransaction(Long transactionId, boolean commit);

    /**
     * Used by coordinator during restarting from a failure. It can use this method to ask for a status of a transaction.
     *
     * @see Status
     */
    Future<Status> getStatus(Integer transactionId);

    /**
     * Used for debugging purposes to testConnection, during initialization.
     */
    boolean testConnection();
}
