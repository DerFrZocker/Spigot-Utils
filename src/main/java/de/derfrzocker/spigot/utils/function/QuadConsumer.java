package de.derfrzocker.spigot.utils.function;

@FunctionalInterface
public interface QuadConsumer<F, S, T, Q> {

    void accept(F f, S s, T t, Q q);
}
