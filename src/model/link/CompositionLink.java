package model.link;

public class CompositionLink extends Link {
    private String name;

    public CompositionLink(String name) {
        if (name == null)
            throw new IllegalArgumentException("Link name cannot be null");

        this.name = name;
    }

    public CompositionLink() {
        this("Composition");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
