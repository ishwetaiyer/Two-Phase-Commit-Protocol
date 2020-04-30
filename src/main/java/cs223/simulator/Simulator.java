package cs223.simulator;

import cs223.coordination.Coordinator;
import cs223.node.Node;
import cs223.node.NodeNotImplementedException;
import cs223.transaction.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Simulator {

    Coordinator coordinator;
    BlockingQueue<Transaction> transactions = new ArrayBlockingQueue<>(10);

    public Simulator() {

    }

    /**
     * This constructor reads a json config, constructs the coordinator and nodes, and tests if all nodes are connected
     * in the beginning.
     *
     * @param config Json config, initialized by {@link Configuration#initialize()}
     */
    public Simulator(JSONObject config) {
        Map<Integer, Node> nodes = new HashMap<>();
        config.forEach((k, v) -> {
            String key = k.toString();

            switch (key) {
                case "nodes":
                    parseNodesConfigurations(nodes, (JSONArray) v);
                    break;
                case "workload":
                    parseWorkloadConfiguration((JSONArray) v);
            }
        });

        // coordinator = new CoordinatorImpl(nodes);
        testNodeConnections(nodes);

    }

    private void testNodeConnections(Map<Integer, Node> nodes) {
        AtomicReference<Boolean> disrupted = new AtomicReference<>(false);
        List<Node> disconnectedNodes = new ArrayList<>();
        nodes.values().forEach(node -> {
            boolean connected = node.testConnection();
            if (!connected) {
                disconnectedNodes.add(node);
                disrupted.set(true);
            }
        });
        if (disrupted.get()) {
            System.err.println("Following nodes are not connected");
            disconnectedNodes.forEach(System.err::println);
            throw new RuntimeException("Check node configs!");
        }
    }

    private void parseWorkloadConfiguration(JSONArray workloadConfig) {

    }

    private void parseNodesConfigurations(Map<Integer, Node> nodes, JSONArray nodesConfigurations) {
        nodesConfigurations.forEach(c -> {
            JSONObject nodeConfig = (JSONObject) c;
            Node node;
            try {
                node = NodeBuilder.build(nodeConfig).orElseThrow(() ->
                        new IllegalArgumentException("Could not parse config: " + nodeConfig.toJSONString()));
                if (nodes.containsKey(node.nodeNumber())) {
                    System.err.println("Duplicate node found");
                    System.err.println("Node: " + node + " is same as " + nodes.containsKey(node.nodeNumber()));
                    System.exit(1);
                }
                nodes.put(node.nodeNumber(), node);
            } catch (NodeNotImplementedException e) {
                System.err.println("Skipping node of type: " + e.nodeType + " as it is not implemented yet.");;
            }

        });

        // if no nodes are registered, inform the user and exit.
        if (nodes.isEmpty()) {
            System.err.println("Please implement at least one node type, or change your configuration");
            System.exit(1);
        }
    }

    /**
     * Fires off a single producer (for producing transactions), and multiple consumers (for processing them)
     */
    public void simulate() {

        checkLogsForReccovery();

        fireProducer(transactions);

        fireCoordinators(transactions);
    }

    private void checkLogsForReccovery() {
        // TODO: 2/28/2020 Implement recovery y parsing log files
    }

    private void fireCoordinators(BlockingQueue<Transaction> transactions) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new TransactionConsumer(transactions, coordinator));
        }
        executorService.shutdown();
    }

    private void fireProducer(BlockingQueue<Transaction> transactions) {
        TransactionProducer producer = new TransactionProducer(transactions);
        new Thread(producer).start();
    }
}
