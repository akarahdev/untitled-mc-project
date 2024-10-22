package dev.akarah.functions;

@FunctionalInterface
public interface QuinFunction<A, B, C, D, E, F> {
    public F apply(A a, B b, C c, D d, E e);
}
