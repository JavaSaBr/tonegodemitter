package tonegod.emitter.interpolation.impl;

import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class SineOutInterpolation extends AbstractInterpolation {

    public SineOutInterpolation(@NotNull final String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        return FastMath.sin(a * FastMath.PI / 2);
    }
}
