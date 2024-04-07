package ASTree;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String type;
    private String value;
    private List<Node> children;

    public Node(String type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public List<Node> getChildren() {
        return children;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
