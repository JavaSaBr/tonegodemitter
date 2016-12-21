package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class PowOut extends Pow {

    public PowOut(final int power, @NotNull final String name) {
        super(power, name);
    }

    @Override
    public float apply(float a) {
        return (float) Math.pow(a - 1, power) * (power % 2 == 0 ? -1 : 1) + 1;
    }
}
