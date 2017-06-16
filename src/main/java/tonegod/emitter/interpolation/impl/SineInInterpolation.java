package tonegod.emitter.interpolation.impl;

import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

/**
 * The type Sine in interpolation.
 *
 * @author toneg0d, JavaSaBr
 */
public class SineInInterpolation extends AbstractInterpolation {

    /**
     * Instantiates a new Sine in interpolation.
     *
     * @param name the name
     */
    public SineInInterpolation(@NotNull final String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        return 1 - FastMath.cos(a * FastMath.PI / 2);
    }
}
