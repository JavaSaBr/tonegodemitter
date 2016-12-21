package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class CircleInInterpolation extends AbstractInterpolation {

    public CircleInInterpolation(@NotNull final String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        return 1 - (float) Math.sqrt(1 - a * a);
    }
}
