package tonegod.emitter.interpolation.impl;

import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

/**
 * The type Elastic.
 *
 * @author toneg0d, JavaSaBr
 */
public class Elastic extends AbstractInterpolation {

    /**
     * The Value.
     */
    protected final float value;
    /**
     * The Power.
     */
    protected final float power;

    /**
     * Instantiates a new Elastic.
     *
     * @param value the value
     * @param power the power
     * @param name  the name
     */
    public Elastic(final float value, final float power, @NotNull final String name) {
        super(name);
        this.value = value;
        this.power = power;
    }

    @Override
    public float apply(float a) {
        if (a <= 0.5f) {
            a *= 2;
            return (float) Math.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f / 2;
        }
        a = 1 - a;
        a *= 2;
        return 1 - (float) Math.pow(value, power * (a - 1)) * FastMath.sin((a) * 20) * 1.0955f / 2;
    }
}
