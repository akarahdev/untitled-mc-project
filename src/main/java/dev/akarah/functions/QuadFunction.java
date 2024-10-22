package dev.akarah.functions;

@FunctionalInterface
public interface QuadFunction<A, B, C, D, E> {
    public E apply(A a, B b, C c, D d);
}
