package model.link;

public class AssociationLink extends Link {
    private String name;

    public AssociationLink(String name) {
        if (name == null)
            throw new IllegalArgumentException("Link name cannot be null");

        this.name = name;
    }

    public AssociationLink() {
        this("Composition");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
