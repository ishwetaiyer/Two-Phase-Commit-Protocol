package cs223.node;

public class NodeNotImplementedException extends Exception {
    public NodeType nodeType;
    public NodeNotImplementedException(NodeType type) {
        super("Node: " + type.name() + " hasn't been implemented yet.");
        this.nodeType = type;
    }
}
