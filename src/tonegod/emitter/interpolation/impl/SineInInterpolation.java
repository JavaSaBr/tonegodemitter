package tonegod.emitter.interpolation.impl;

import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class SineInInterpolation extends AbstractInterpolation {

    public SineInInterpolation(@NotNull final String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        return 1 - FastMath.cos(a * FastMath.PI / 2);
    }
}
