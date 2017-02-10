package tonegod.emitter;

import org.jetbrains.annotations.NotNull;

/**
 * The list of types particle emission points.
 *
 * @author t0neg0d, JavaSaBr
 */
public enum EmissionPoint {
    CENTER(Messages.EMISSION_POINT_CENTER),
    EDGE_TOP(Messages.EMISSION_POINT_EDGE_TOP),
    EDGE_BOTTOM(Messages.EMISSION_POINT_EDGE_BOTTOM);

    @NotNull
    private static final EmissionPoint[] VALUES = values();

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