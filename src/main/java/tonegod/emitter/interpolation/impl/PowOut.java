package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Pow out.
 *
 * @author toneg0d, JavaSaBr
 */
public class PowOut extends Pow {

    /**
     * Instantiates a new Pow out.
     *
     * @param power the power
     * @param name  the name
     */
    public PowOut(final int power, @NotNull final String name) {
        super(power, name);
    }

    @Override
    public float apply(float a) {
        return (float) Math.pow(a - 1, power) * (power % 2 == 0 ? -1 : 1) + 1;
    }
}
