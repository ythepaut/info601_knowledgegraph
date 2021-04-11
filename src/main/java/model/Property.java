package model;

/**
 * Property
 * @param <T> Type
 */
public class Property<T> {
    private T value;

    public Property(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return value.equals(other);
    }
}
