package model;

public abstract class GraphElement {
    private final boolean search;

    protected GraphElement(boolean search) {
        this.search = search;
    }

    public boolean isSearched() {
        return search;
    }
}
