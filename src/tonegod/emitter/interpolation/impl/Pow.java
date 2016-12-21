package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class Pow extends AbstractInterpolation {

    protected final int power;

    public Pow(final int power, @NotNull final String name) {
        super(name);
        this.power = power;
    }

    @Override
    public float apply(float a) {
        if (a <= 0.5f) return (float) Math.pow(a * 2, power) / 2;
        return (float) Math.pow((a - 1) * 2, power) / (power % 2 == 0 ? -2 : 2) + 1;
    }
}
