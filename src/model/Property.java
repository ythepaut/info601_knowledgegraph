package model;

public class Property<T> {
    private T elt;

    public Property(T elt) {
        this.elt = elt;
    }

    public void set(T elt) {
        this.elt = elt;
    }

    public T get() {
        return elt;
    }
}
