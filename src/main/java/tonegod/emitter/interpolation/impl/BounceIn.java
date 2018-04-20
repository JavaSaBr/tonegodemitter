package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Bounce in.
 *
 * @author toneg0d, JavaSaBr
 */
public class BounceIn extends BounceOut {

    public BounceIn(@NotNull float[] widths, @NotNull float[] heights, @NotNull String name) {
        super(widths, heights, name);
    }

    public BounceIn(int bounces, @NotNull String name) {
        super(bounces, name);
    }

    @Override
    public float apply(float a) {
        return 1 - super.apply(1 - a);
    }
}