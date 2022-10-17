package de.derfrzocker.spigot.utils.function;

@FunctionalInterface
public interface TripleFunction<F, S, T, R> {

    R apply(F first, S second, T third);

}
