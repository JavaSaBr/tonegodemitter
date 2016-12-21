package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class BounceIn extends BounceOut {

    public BounceIn(@NotNull final float[] widths, @NotNull final float[] heights, @NotNull final String name) {
        super(widths, heights, name);
    }

    public BounceIn(final int bounces, @NotNull final String name) {
        super(bounces, name);
    }

    @Override
    public float apply(float a) {
        return 1 - super.apply(1 - a);
    }
}