package model;

public abstract class GraphElement {
    private boolean search;

    protected GraphElement(boolean search) {
        this.search = search;
    }

    public boolean isSearched() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }
}
