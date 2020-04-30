package cs223.simulator;

import cs223.transaction.DummyTransaction;
import cs223.transaction.Transaction;

import java.util.concurrent.BlockingQueue;

import static cs223.transaction.TransactionType.END;
import static cs223.transaction.TransactionType.NORMAL;

public class TransactionProducer implements Runnable {

    /**
     * transactions is the common queue between producer and consumer. Producer threads puts transaction
     * in this queue after reading them from a file.
     */
    private BlockingQueue<Transaction> transactions;

    public TransactionProducer(BlockingQueue<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void run() {

        // TODO: 2/25/2020 replace this logic with actual file parsing
        for (int i = 0; i < 100; i++) {
            log("Producing transaction: " + i);
            try {
                // replace the logic below with file parsing and grouping transactions.
                // remember to give each transaction a unique id!
                Transaction transaction = new DummyTransaction(NORMAL, (long) i);
                transactions.put(transaction);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log("Producer produced transaction: " + i);
        }

        // exit here
        Transaction transaction = new DummyTransaction(END, (long) -1);
        try {
            transactions.put(transaction);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void log(String s) {
        System.out.println(Thread.currentThread().getName() + ": " + s);
    }
}
