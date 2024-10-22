package dev.akarah.event;

@FunctionalInterface
public interface EventHandler<T> {
    void run(T data);

    default void runObject(Object data) {
        this.run((T) data);
    }
}
