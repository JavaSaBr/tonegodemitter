package tonegod.emitter;

import org.jetbrains.annotations.NotNull;

/**
 * The list of forced stretch axis.
 *
 * @author t0neg0d, JavaSaBr
 */
public enum ForcedStretchAxis {
    /**
     * X forced stretch axis.
     */
    X("X"),
    /**
     * Y forced stretch axis.
     */
    Y("Y"),
    /**
     * Z forced stretch axis.
     */
    Z("Z");

    @NotNull
    private static final ForcedStretchAxis[] VALUES = values();

    /**
     * Value of forced stretch axis.
     *
     * @param index the index
     * @return the forced stretch axis
     */
    public static @NotNull ForcedStretchAxis valueOf(final int index) {
        return VALUES[index];
    }

    /**
     * The UI name.
     */
    @NotNull
    private final String uiName;

    ForcedStretchAxis(@NotNull final String uiName) {
        this.uiName = uiName;
    }

    @Override
    public String toString() {
        return uiName;
    }
}