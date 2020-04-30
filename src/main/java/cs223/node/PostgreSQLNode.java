package cs223.node;

import cs223.message.MessageType;
import cs223.transaction.Statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



public class PostgreSQLNode extends AbstractNode {

    private Connection conn;
    private ExecutorService service;
    
    @Override
    public NodeType type() {
        return NodeType.POSTGRESQL;
    }

    @Override
    public Future<MessageType> prepareTransaction(Long transactionId, List<Statement> statements) {



        // TODO: use threadCount variable from json file instead of 1
        service = Executors.newFixedThreadPool(1);
        try {
            
            String prepare = "PREPARE TRANSACTION" + transactionId;
            conn.prepareStatement(prepare).execute();
            
            for (Statement s: statements)
                conn.prepareStatement(s.toString()).execute();

            return service.submit(() -> MessageType.COMMIT);

        } catch (SQLException e) {
            e.printStackTrace();
            return service.submit(() -> MessageType.ABORT);
        }

    }

    @Override
    public Future<Boolean> commitTransaction(Long transactionId, boolean commit) {

        if(commit)
        {
            try {
                java.sql.Statement c = conn.createStatement();
                c.execute("COMMIT");
                return service.submit(() -> true);
            } catch (SQLException e) {
                e.printStackTrace();
                return service.submit(() -> false);
            }
        }

        return null;
    }


    @Override
    public Future<Status> getStatus(Integer transactionId) {


        return null;
    }

    @Override
    public boolean testConnection() {
        return false;
    }
}
