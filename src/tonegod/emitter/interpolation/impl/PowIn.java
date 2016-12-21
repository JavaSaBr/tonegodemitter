package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class PowIn extends Pow {

    public PowIn(final int power, @NotNull final String name) {
        super(power, name);
    }

    @Override
    public float apply(float a) {
        return (float) Math.pow(a, power);
    }
}
