package dev.akarah.functions;

@FunctionalInterface
public interface SixFunction<A, B, C, D, E, F, G> {
    public G apply(A a, B b, C c, D d, E e, F f);
}
