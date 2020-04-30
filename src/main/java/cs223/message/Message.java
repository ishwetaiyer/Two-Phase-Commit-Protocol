package cs223.message;

import cs223.node.Node;

public class Message {
    MessageType type;
    Node node;

    public MessageType getType() {
        return type;
    }

    public Node getNode() {
        return node;
    }
}
