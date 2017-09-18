package tonegod.emitter.influencers;

import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.interpolation.Interpolation;

/**
 * The interface for implementing interpolated particle influencers.
 *
 * @author JavaSaBr
 */
public interface InterpolatedParticleInfluencer extends ParticleInfluencer {

    /**
     * Get the count of interpolation steps.
     *
     * @return the count of interpolation steps.
     */
    int getStepCount();

    /**
     * Get an interpolation for the step.
     *
     * @param index the index of the step.
     * @return the interpolation for the step.
     * @throws RuntimeException if the index is invalid.
     */
    @NotNull Interpolation getInterpolation(int index) throws RuntimeException;

    /**
     * Update a interpolation for the index.
     *
     * @param interpolation the new interpolation.
     * @param index         the index.
     * @throws RuntimeException if the index is invalid.
     */
    void updateInterpolation(@NotNull Interpolation interpolation, int index) throws RuntimeException;

    /**
     * Get the list of all exists interpolations.
     *
     * @return the list of interpolations.
     */
    @NotNull SafeArrayList<Interpolation> getInterpolations();

    /**
     * Is cycle boolean.
     *
     * @return true if using cycle changing.
     */
    boolean isCycle();

    /**
     * Returns the current duration used between frames for cycled animation
     *
     * @return the fixed duration
     */
    float getFixedDuration();

    /**
     * Animated texture should cycle and use the provided duration between frames (0 diables
     * cycling)
     *
     * @param fixedDuration duration between frame updates
     */
    void setFixedDuration(float fixedDuration);
}
