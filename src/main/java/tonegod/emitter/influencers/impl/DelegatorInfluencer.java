package tonegod.emitter.influencers.impl;

import org.jetbrains.annotations.NotNull;
import tonegod.emitter.influencers.InfluencerData;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;

public abstract class DelegatorInfluencer<T extends InfluencerData> extends AbstractParticleInfluencer<T> {

    protected ParticleInfluencer particleInfluencer;

    public DelegatorInfluencer() {
    }

    public DelegatorInfluencer(ParticleInfluencer particleInfluencer) {
        this.particleInfluencer = particleInfluencer;
    }

    public ParticleInfluencer getParticleInfluencer() {
        return particleInfluencer;
    }

    public void setParticleInfluencer(ParticleInfluencer particleInfluencer) {
        this.particleInfluencer = particleInfluencer;
    }

    @Override
    protected void firstInitializeImpl(@NotNull ParticleData particleData) {
        super.firstInitializeImpl(particleData);

        particleData.getEmitterNode().addInfluencer(particleInfluencer);
    }
}
