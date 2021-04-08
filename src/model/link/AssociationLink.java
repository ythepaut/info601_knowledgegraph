package model.link;

import model.node.ConceptNode;
import model.node.InstanceNode;
import model.node.Node;

public class AssociationLink extends Link {
    private String name;

    public AssociationLink(String name, boolean blyat) {
        if (name == null)
            throw new IllegalArgumentException("Link name cannot be null");

        this.name = name;
    }

    public AssociationLink() {
        this("Composition", true);
    }

    @Override
    public boolean isCompatible(Node from, Node to) {
        return from instanceof InstanceNode && to instanceof InstanceNode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AssociationLink) {
            return name.equals(((AssociationLink) other).name);
        }

        return false;
    }
}
