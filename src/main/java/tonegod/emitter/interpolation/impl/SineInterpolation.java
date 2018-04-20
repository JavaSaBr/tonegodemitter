package tonegod.emitter.interpolation.impl;

import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

/**
 * The type Sine interpolation.
 *
 * @author toneg0d, JavaSaBr
 */
public class SineInterpolation extends AbstractInterpolation {

    public SineInterpolation(@NotNull String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        return (1 - FastMath.cos(a * FastMath.PI)) / 2;
    }
}
