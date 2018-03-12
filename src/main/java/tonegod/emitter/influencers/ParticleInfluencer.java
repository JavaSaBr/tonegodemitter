package tonegod.emitter.influencers;

import com.jme3.export.Savable;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.particle.ParticleData;

/**
 * The interface for implementing particle influencers.
 *
 * @author t0neg0d, JavaSaBr
 */
public interface ParticleInfluencer<T extends InfluencerData> extends Savable, Cloneable {

    /**
     * Gets name.
     *
     * @return the name of this influencer.
     */
    @NotNull String getName();

    /**
     * This method clones the influencer instance.
     *
     * @return cloned instance
     */
    @NotNull ParticleInfluencer clone();

    /**
     * Update loop for the particle influencer
     *
     * @param particleData The particle to update
     * @param tpf          The time since last frame
     */
    void update(@NotNull ParticleData particleData, T influencerData, float tpf);

    /**
     * Called when a particle is emitted.
     *
     * @param particleData The particle being emitted
     */
    void initialize(@NotNull ParticleData particleData, T influencerData);

    /**
     * Called once the life span of the particle has been reached.
     *
     * @param particleData The particle that was removed
     */
    void reset(@NotNull ParticleData particleData);

    /**
     * Enables/disables the influencer without removing it from the chain. It is worth noting that
     * initialize can still be used whether or not the influencer has been disabled.
     *
     * @param enable the enable
     */
    void setEnabled(boolean enable);

    /**
     * Returns if the influencer is currently enabled
     *
     * @return the boolean
     */
    boolean isEnabled();
}