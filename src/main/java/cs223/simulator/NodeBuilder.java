package cs223.simulator;

import cs223.message.MessageType;
import cs223.node.Node;
import cs223.node.NodeNotImplementedException;
import cs223.node.NodeType;
import cs223.node.Status;
import cs223.transaction.Statement;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static cs223.node.NodeType.*;

public class NodeBuilder {

    static int number = 1;
    static Lock l = new ReentrantLock();

    static Optional<Node> build(JSONObject nodeConfig) throws NodeNotImplementedException {
        NodeType nodeType = NodeType.valueOf(String.valueOf(nodeConfig.get("type")));
        JSONObject config = (JSONObject) Optional.ofNullable(nodeConfig.get("config")).orElseThrow(() -> new NoSuchFieldError("config in " + nodeConfig));
        switch (nodeType) {
            case DEFAULT: {
                return Optional.of(new Node() {
                    @Override
                    public NodeType type() {
                        return null;
                    }

                    @Override
                    public Future<MessageType> prepareTransaction(Long transactionId, List<Statement> statements) {
                        return null;
                    }

                    @Override
                    public Future<Boolean> commitTransaction(Long transactionId, boolean commit) {
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
                });
            }
            case POSTGRESQL: {
                throw new NodeNotImplementedException(POSTGRESQL);
//                String url = String.valueOf(Optional.ofNullable(config.get("dbURL")).orElseThrow(() -> new NoSuchFieldError("dbURL")));
//                Map<String, String> params = (Map<String, String>) Optional.ofNullable(config.get("params")).orElseThrow(() -> new NoSuchFieldError("params"));
//                Node node = null;
//                try {
//                    l.lock();
//                    node = new PostgresqlNode(number++, url, params);
//                } finally {
//                    l.unlock();
//                }
//
//                return Optional.of(node);
            }
            case FAKE: {
                throw new NodeNotImplementedException(FAKE);
//                Node node = null;
//                try {
//                    l.lock();
//                    node = new FakeNode(number++);
//                } finally {
//                    l.unlock();
//                }
//                return Optional.of(node);
            }
            case SHWETA_NODE:
                throw new NodeNotImplementedException(SHWETA_NODE);

                // TODO: 2/25/2020 Shweta add your node here
            default:
                return Optional.empty();
        }
    }
}
