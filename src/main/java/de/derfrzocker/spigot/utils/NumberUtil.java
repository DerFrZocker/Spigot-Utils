package de.derfrzocker.spigot.utils;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class NumberUtil {

    /**
     * Returns a integer from a double and a random
     * The returned integer is
     * a) the value of the given double with out decimal
     * b) the value of the given double with out decimal + 1
     * <p>
     * The given random while be used to decide which of the two cases get used.
     * If the value of Random#nextDouble is bigger than the decimal value of the given value,
     * than it while return case a. If it is less than case b.
     *
     * @param value  to use
     * @param random to use
     * @return an integer of the given random and double value
     * @throws IllegalArgumentException if random is null
     */
    public static int getInt(final double value, @NotNull final Random random) {
        Validate.notNull(random, "Random can not be null");

        final int intValue = (int) value;
        final double rest = value - intValue;

        if (rest == 0)
            return intValue;

        if (rest < random.nextDouble())
            return intValue;
        else
            return intValue + 1;
    }

}
