package model.link;

import model.node.ConceptNode;
import model.node.Node;
import org.json.JSONObject;

public class AkoLink extends Link {
    public AkoLink() {
        super("ako", true);
    }

    @Override
    public boolean isCompatible(Node from, Node to) {
        return from instanceof ConceptNode && to instanceof ConceptNode;
    }

    @Override
    public boolean isSameLink(Link other) {
        return other instanceof AkoLink;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = super.toJSONObject();
        obj.put("type", "AKO");
        return obj;
    }

    @Override
    public void checkInheritProperties(Node from, Node to) {
        super.inheritProperties(from, to);
    }
}
