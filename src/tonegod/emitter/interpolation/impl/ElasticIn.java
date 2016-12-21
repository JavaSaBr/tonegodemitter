package tonegod.emitter.interpolation.impl;

import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class ElasticIn extends Elastic {

    public ElasticIn(final float value, final float power, @NotNull final String name) {
        super(value, power, name);
    }

    @Override
    public float apply(float a) {
        return (float) Math.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f;
    }
}
