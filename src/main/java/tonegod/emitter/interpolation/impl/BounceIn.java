package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Bounce in.
 *
 * @author toneg0d, JavaSaBr
 */
public class BounceIn extends BounceOut {

    /**
     * Instantiates a new Bounce in.
     *
     * @param widths  the widths
     * @param heights the heights
     * @param name    the name
     */
    public BounceIn(@NotNull final float[] widths, @NotNull final float[] heights, @NotNull final String name) {
        super(widths, heights, name);
    }

    /**
     * Instantiates a new Bounce in.
     *
     * @param bounces the bounces
     * @param name    the name
     */
    public BounceIn(final int bounces, @NotNull final String name) {
        super(bounces, name);
    }

    @Override
    public float apply(float a) {
        return 1 - super.apply(1 - a);
    }
}