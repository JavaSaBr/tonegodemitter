package tonegod.emitter.interpolation.impl;

import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

/**
 * The type Elastic in.
 *
 * @author toneg0d, JavaSaBr
 */
public class ElasticIn extends Elastic {

    /**
     * Instantiates a new Elastic in.
     *
     * @param value the value
     * @param power the power
     * @param name  the name
     */
    public ElasticIn(final float value, final float power, @NotNull final String name) {
        super(value, power, name);
    }

    @Override
    public float apply(float a) {
        return (float) Math.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f;
    }
}
