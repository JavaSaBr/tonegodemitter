package tonegod.emitter;

import org.jetbrains.annotations.NotNull;

/**
 * The list of billboard modes.
 *
 * @author JavaSaBr
 */
public enum BillboardMode {
    /**
     * Facing direction follows the velocity as it changes
     */
    VELOCITY("Velocity"),
    /**
     * Facing direction follows the velocity as it changes, Y of particle always faces Z of velocity
     */
    VELOCITY_Z_UP("Velocity Z up"),
    /**
     * Facing direction follows the velocity as it changes, Y of particle always faces Z of velocity, Up of the particle
     * always faces X
     */
    VELOCITY_Z_UP_Y_LEFT("Velocity Z up Y left"),
    /**
     * Facing direction remains constant to the face of the particle emitter shape that the particle was emitted from
     */
    NORMAL("Normal"),
    /**
     * Facing direction remains constant for X, Z axis' to the face of the particle emitter shape that the particle was
     * emitted from. Y axis maps to UNIT_Y
     */
    NORMAL_Y_UP("Normal Y up"),
    /**
     * ParticleData always faces camera
     */
    CAMERA("Camera"),
    /**
     * ParticleData always faces X axis
     */
    UNIT_X("Unit X"),
    /**
     * ParticleData always faces Y axis
     */
    UNIT_Y("Unit Y"),
    /**
     * ParticleData always faces Z axis
     */
    UNIT_Z("Unit Z");

    @NotNull
    private static final BillboardMode[] VALUES = values();

    @NotNull
    public static BillboardMode valueOf(final int index) {
        return VALUES[index];
    }

    private final String uiName;

    BillboardMode(final String uiName) {
        this.uiName = uiName;
    }

    @Override
    public String toString() {
        return uiName;
    }
}