package tonegod.emitter.interpolation.impl;

import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

/**
 * The type Fade interpolation.
 *
 * @author toneg0d, JavaSaBr
 */
public class FadeInterpolation extends AbstractInterpolation {

    public FadeInterpolation(final @NotNull String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        return FastMath.clamp(a * a * a * (a * (a * 6 - 15) + 10), 0, 1);
    }
}
