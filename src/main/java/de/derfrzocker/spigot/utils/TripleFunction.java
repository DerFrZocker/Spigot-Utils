package de.derfrzocker.spigot.utils;

@FunctionalInterface
public interface TripleFunction<F, S, T, R> {

    R apply(F first, S second, T third);

}
