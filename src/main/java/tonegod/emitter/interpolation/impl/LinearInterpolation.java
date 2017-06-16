package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Linear interpolation.
 *
 * @author toneg0d, JavaSaBr
 */
public class LinearInterpolation extends AbstractInterpolation {

    /**
     * Instantiates a new Linear interpolation.
     *
     * @param name the name
     */
    public LinearInterpolation(final @NotNull String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        return a;
    }
}
