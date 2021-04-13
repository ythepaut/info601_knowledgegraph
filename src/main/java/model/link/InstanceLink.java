package model.link;

import model.node.ConceptNode;
import model.node.InstanceNode;
import model.node.Node;
import org.json.JSONObject;

public class InstanceLink extends Link {
    public InstanceLink() {
        super("instance", true);
    }

    @Override
    public boolean isCompatible(Node from, Node to) {
        return (from instanceof ConceptNode && to instanceof InstanceNode) ||
               (from instanceof InstanceNode && to instanceof ConceptNode);
    }

    @Override
    public boolean isSameLink(Link other) {
        return other instanceof InstanceLink;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = super.toJSONObject();
        obj.put("type", "INSTANCE");
        return obj;
    }

    @Override
    public void checkInheritProperties(Node from, Node to) {
        super.inheritProperties(from, to);
    }
}
