package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Pow.
 *
 * @author toneg0d, JavaSaBr
 */
public class Pow extends AbstractInterpolation {

    /**
     * The Power.
     */
    protected final int power;

    public Pow(int power, @NotNull String name) {
        super(name);
        this.power = power;
    }

    @Override
    public float apply(float a) {
        if (a <= 0.5f) return (float) Math.pow(a * 2, power) / 2;
        return (float) Math.pow((a - 1) * 2, power) / (power % 2 == 0 ? -2 : 2) + 1;
    }
}
