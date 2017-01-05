package tonegod.emitter;

import org.jetbrains.annotations.NotNull;

/**
 * The list of forced stretch axis.
 *
 * @author JavaSaBr
 */
public enum ForcedStretchAxis {
    X("x"),
    Y("y"),
    Z("z");

    @NotNull
    private static final ForcedStretchAxis[] VALUES = values();

    @NotNull
    public static ForcedStretchAxis valueOf(final int index) {
        return VALUES[index];
    }

    /**
     * The UI name.
     */
    @NotNull
    private final String uiName;

    ForcedStretchAxis(final @NotNull String uiName) {
        this.uiName = uiName;
    }

    @Override
    public String toString() {
        return uiName;
    }
}