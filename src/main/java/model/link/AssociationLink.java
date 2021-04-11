package model.link;

import model.node.InstanceNode;
import model.node.Node;
import org.json.JSONObject;

public class AssociationLink extends Link {
    public AssociationLink(String name, boolean oriented) {
        super(name, oriented);
    }

    public AssociationLink(String name) {
        this(name, true);
    }

    public AssociationLink(boolean oriented) {
        this("association", oriented);
    }

    public AssociationLink() {
        this("association", true);
    }

    @Override
    public boolean isCompatible(Node from, Node to) {
        return from instanceof InstanceNode && to instanceof InstanceNode;
    }

    @Override
    public boolean isSameLink(Link other) {
        if (other instanceof AssociationLink) {
            return super.getName().equals(other.getName());
        }

        return false;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = super.toJSONObject();
        obj.put("type", "ASSOCIATION");
        return obj;
    }
}
