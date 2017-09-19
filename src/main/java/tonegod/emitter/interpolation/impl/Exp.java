package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Exp.
 *
 * @author toneg0d, JavaSaBr
 */
public class Exp extends AbstractInterpolation {

    /**
     * The Value.
     */
    protected final float value;
    /**
     * The Power.
     */
    protected final float power;
    /**
     * The Min.
     */
    protected final float min;
    /**
     * The Scale.
     */
    protected final float scale;

    public Exp(final float value, final float power, @NotNull final String name) {
        super(name);
        this.value = value;
        this.power = power;
        this.min = (float) Math.pow(value, -power);
        this.scale = 1 / (1 - min);
    }

    @Override
    public float apply(float a) {
        if (a <= 0.5f) return ((float) Math.pow(value, power * (a * 2 - 1)) - min) * scale / 2;
        return (2 - ((float) Math.pow(value, -power * (a * 2 - 1)) - min) * scale) / 2;
    }
}
