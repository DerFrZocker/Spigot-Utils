package de.derfrzocker.spigot.utils.function;

@FunctionalInterface
public interface TripleConsumer<F, S, T> {

    void accept(F f, S s, T t);
}
