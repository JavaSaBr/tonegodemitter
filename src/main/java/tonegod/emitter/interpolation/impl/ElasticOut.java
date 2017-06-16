package tonegod.emitter.interpolation.impl;

import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

/**
 * The type Elastic out.
 *
 * @author toneg0d, JavaSaBr
 */
public class ElasticOut extends Elastic {

    /**
     * Instantiates a new Elastic out.
     *
     * @param value the value
     * @param power the power
     * @param name  the name
     */
    public ElasticOut(final float value, final float power, @NotNull final String name) {
        super(value, power, name);
    }

    @Override
    public float apply(float a) {
        a = 1 - a;
        return (1 - (float) Math.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f);
    }
}