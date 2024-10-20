package dev.akarah.functions;

public interface TriFunction<A, B, C, D> {
    public D apply(A a, B b, C c);
}
