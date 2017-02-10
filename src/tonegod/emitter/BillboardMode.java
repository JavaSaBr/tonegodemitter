package tonegod.emitter;

import org.jetbrains.annotations.NotNull;

/**
 * The list of billboard modes.
 *
 * @author t0neg0d, JavaSaBr
 */
public enum BillboardMode {
    /**
     * Facing direction follows the velocity as it changes
     */
    VELOCITY(Messages.BILLBOARD_MODE_VELOCITY),
    /**
     * Facing direction follows the velocity as it changes, Y of particle always faces Z of velocity
     */
    VELOCITY_Z_UP(Messages.BILLBOARD_MODE_VELOCITY_Z_UP),
    /**
     * Facing direction follows the velocity as it changes, Y of particle always faces Z of velocity, Up of the particle
     * always faces X
     */
    VELOCITY_Z_UP_Y_LEFT(Messages.BILLBOARD_MODE_VELOCITY_Z_UP_Y_LEFT),
    /**
     * Facing direction remains constant to the face of the particle emitter shape that the particle was emitted from
     */
    NORMAL(Messages.BILLBOARD_MODE_NORMAL),
    /**
     * Facing direction remains constant for X, Z axis' to the face of the particle emitter shape that the particle was
     * emitted from. Y axis maps to UNIT_Y
     */
    NORMAL_Y_UP(Messages.BILLBOARD_MODE_NORMAL_Y_UP),
    /**
     * ParticleData always faces camera
     */
    CAMERA(Messages.BILLBOARD_MODE_CAMERA),
    /**
     * ParticleData always faces X axis
     */
    UNIT_X(Messages.BILLBOARD_MODE_UNIT_X),
    /**
     * ParticleData always faces Y axis
     */
    UNIT_Y(Messages.BILLBOARD_MODE_UNIT_Y),
    /**
     * ParticleData always faces Z axis
     */
    UNIT_Z(Messages.BILLBOARD_MODE_UNIT_Z);

    @NotNull
    private static final BillboardMode[] VALUES = values();

    @NotNull
    public static BillboardMode valueOf(final int index) {
        return VALUES[index];
    }

    @NotNull
    private final String uiName;

    BillboardMode(@NotNull final String uiName) {
        this.uiName = uiName;
    }

    @Override
    public String toString() {
        return uiName;
    }
}