package model.link;

import model.node.InstanceNode;
import model.node.Node;

public class AssociationLink extends Link {
    public AssociationLink(String name, boolean oriented) {
        super(name, oriented);
    }

    public AssociationLink(boolean oriented) {
        this("association", oriented);
    }

    public AssociationLink() {
        this("association", false);
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
}
