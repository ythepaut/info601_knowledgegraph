package model.link;

import model.node.ConceptNode;
import model.node.Node;
import org.json.JSONObject;

public class CompositionLink extends Link {
    public CompositionLink(String name, boolean oriented) {
        super(name, oriented);
    }

    public CompositionLink(String name) {
        this(name, true);
    }

    public CompositionLink(boolean oriented) {
        this("composition", oriented);
    }

    public CompositionLink() {
        this("composition", true);
    }

    @Override
    public boolean isCompatible(Node from, Node to) {
        return from instanceof ConceptNode && to instanceof ConceptNode;
    }

    @Override
    public boolean isSameLink(Link other) {
        if (other instanceof CompositionLink) {
            return super.getName().equals(other.getName());
        }

        return false;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = super.toJSONObject();
        obj.put("type", "COMPOSITION");
        return obj;
    }
}
