package dev.akarah.functions;

@FunctionalInterface
public interface SepFunction<A, B, C, D, E, F, G, H> {
    public H apply(A a, B b, C c, D d, E e, F f, G g);
}
