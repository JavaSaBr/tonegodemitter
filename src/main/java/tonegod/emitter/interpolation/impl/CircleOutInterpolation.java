package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Circle out interpolation.
 *
 * @author toneg0d, JavaSaBr
 */
public class CircleOutInterpolation extends AbstractInterpolation {

    public CircleOutInterpolation(@NotNull final String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        a--;
        return (float) Math.sqrt(1 - a * a);
    }
}
