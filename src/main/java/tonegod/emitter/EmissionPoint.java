package tonegod.emitter;

import org.jetbrains.annotations.NotNull;

/**
 * The list of types particle emission points.
 *
 * @author t0neg0d, JavaSaBr
 */
public enum EmissionPoint {
    /**
     * Center emission point.
     */
    CENTER(Messages.EMISSION_POINT_CENTER),
    /**
     * Edge top emission point.
     */
    EDGE_TOP(Messages.EMISSION_POINT_EDGE_TOP),
    /**
     * Edge bottom emission point.
     */
    EDGE_BOTTOM(Messages.EMISSION_POINT_EDGE_BOTTOM);

    @NotNull
    private static final EmissionPoint[] VALUES = values();

    /**
     * Value of emission point.
     *
     * @param index the index
     * @return the emission point
     */
    @NotNull
    public static EmissionPoint valueOf(final int index) {
        return VALUES[index];
    }

    /**
     * The UI name.
     */
    @NotNull
    private final String uiName;

    EmissionPoint(@NotNull final String uiName) {
        this.uiName = uiName;
    }

    @Override
    public String toString() {
        return uiName;
    }
}