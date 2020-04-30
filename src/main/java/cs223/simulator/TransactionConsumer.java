package cs223.simulator;

import cs223.coordination.Coordinator;
import cs223.message.MessageType;
import cs223.transaction.Statement;
import cs223.transaction.Transaction;
import cs223.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cs223.transaction.TransactionType.END;

/**
 * A transaction consumer listens on {@link #transactions} queue, picks up a transaction from it, and then processes it.
 * It keeps on doing that till it sees a transaction of type {@link cs223.transaction.TransactionType#END}, after which
 * it puts it back in the queue and quits.
 */
public class TransactionConsumer implements Runnable {
    private BlockingQueue<Transaction> transactions;
    private Coordinator coordinator;
    private final int nodesSize;
    private int thisThread;

    private static AtomicInteger threadNumber = new AtomicInteger(0);
    public TransactionConsumer(BlockingQueue<Transaction> transactions, Coordinator coordinator) {
        this.transactions = transactions;
        this.coordinator = coordinator;
        this.nodesSize = coordinator.getNodes().size();
    }

    @Override
    public void run() {
        thisThread = threadNumber.getAndIncrement();
            Transaction transaction = null;
            do {
                try {
                    transaction = transactions.take();
                    runTransaction(transaction);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (transaction != null && transaction.type() != END && thisThread != 1);

            // when we get an END transaction,
            // just put it back in the queue for other consumers.
            // This way, other consumers will also find it and and then shut down eventually.
            log("Finished");
        try {
            transactions.put(transaction);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Used to stop the thread
     */
    public void stop() {
        thisThread = -1;
    }

    /**
     * 2PC main logic here
     *
     */
    private void runTransaction(Transaction transaction) throws InterruptedException {
        coordinator.log(transaction.getId(), TransactionStatus.STARTED);
        // sleep to check if consumers are actually working in parallel

        // find the nodes involved
        Map<Integer, List<Statement>> nodeNumberToStatements = new HashMap<>();
        for (Statement s : transaction.getStatements()) {
            Integer nodeNumber = s.hashCode() % nodesSize;
            nodeNumberToStatements.computeIfAbsent(nodeNumber, k -> new ArrayList<>()).add(s);
        }

        // begin 2 pc:
        // step 1: prepare nodes
        Map<Integer, Future<MessageType>> messages = new HashMap<>(nodeNumberToStatements.size());
        nodeNumberToStatements.forEach(
                (nodeNumber, statements) -> messages.put(nodeNumber, coordinator.prepareNode(nodeNumber, transaction.getId(), statements)));


        if (coordinator.failsAfterSendingPrepared()) {
            Thread.currentThread().interrupt();
        };

        // step 3: wait for every node to reply back with timeout
        boolean commit = true;
        for (Map.Entry<Integer, Future<MessageType>> entry : messages.entrySet()) {
            Future<MessageType> f = entry.getValue();
            Integer nodeNumber = entry.getKey();
            try {
                // TODO: 2/28/2020 Timeout value?
                commit &= f.get(10, TimeUnit.SECONDS) == MessageType.COMMIT;
            } catch (InterruptedException | ExecutionException e) {
                // should not happen
                commit = false;
            } catch (TimeoutException e) {
                commit = false;
            }
        }

        final boolean finalCommit = commit;
        Map<Integer, Future<Boolean>> commitReceived = new HashMap<>(nodeNumberToStatements.size());
        nodeNumberToStatements.keySet().forEach(nodeId -> commitReceived.put(nodeId, coordinator.commitTransaction(nodeId, transaction, finalCommit ? MessageType.COMMIT : MessageType.ABORT)));

        // step 5: commit or abort
        boolean acknowledged = true;
        for (Map.Entry<Integer, Future<Boolean>> entry : commitReceived.entrySet()) {
            Future<Boolean> f = entry.getValue();
            Integer nodeNumber = entry.getKey();
            try {
                acknowledged &= f.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                acknowledged = false;
            }
        }

        if (acknowledged) {
            coordinator.markTransactionComplete(transaction);
        } else {
            coordinator.markTransactionFailed(transaction);
        }

    }

    private void log(String s) {
        System.out.println(Thread.currentThread().getName() + ": " + s);
    }
}
