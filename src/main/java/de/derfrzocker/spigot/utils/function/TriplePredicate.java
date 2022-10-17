package de.derfrzocker.spigot.utils.function;

@FunctionalInterface
public interface TriplePredicate<F, S, T> {

    boolean test(F first, S second, T third);

}
