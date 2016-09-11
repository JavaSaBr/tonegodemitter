package tonegod.emitter.influencers;

import com.jme3.export.Savable;

import tonegod.emitter.particle.ParticleData;

/**
 * @author t0neg0d
 */
public interface ParticleInfluencer extends Savable, Cloneable {
    /**
     * This method clones the influencer instance.
     *
     * @return cloned instance
     */
    public ParticleInfluencer clone();

    /**
     * Update loop for the particle influencer
     *
     * @param p   The particle to update
     * @param tpf The time since last frame
     */
    void update(ParticleData p, float tpf);

    /**
     * Called when a particle is emitted.
     *
     * @param p The particle being emitted
     */
    void initialize(ParticleData p);

    /**
     * Called once the life span of the particle has been reached.
     *
     * @param p The particle that was removed
     */
    void reset(ParticleData p);

    /**
     * Enables/disables the influencer without removing it from the chain. It is worth noting that
     * initialize can still be used whether or not the influencer has been disabled.
     */
    void setEnabled(boolean enabled);

    /**
     * Returns if the influencer is currently enabled
     */
    boolean isEnabled();

    /**
     * Returns the influencer's class
     */
    Class getInfluencerClass();
}