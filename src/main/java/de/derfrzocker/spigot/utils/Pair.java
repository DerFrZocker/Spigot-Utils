package de.derfrzocker.spigot.utils;

import org.jetbrains.annotations.Nullable;

public class Pair<F, S> {

    @Nullable
    private final F first;

    @Nullable
    private final S second;

    public Pair(@Nullable final F first, @Nullable final S second) {
        this.first = first;
        this.second = second;
    }

    @Nullable
    public F getFirst() {
        return first;
    }

    @Nullable
    public S getSecond() {
        return second;
    }

}
