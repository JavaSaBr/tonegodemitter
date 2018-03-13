package tonegod.emitter.influencers.impl;

import org.jetbrains.annotations.NotNull;
import tonegod.emitter.influencers.InfluencerData;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;

public class ConditionInfluencer extends DelegatorInfluencer<ConditionInfluencer.ConditionData> {

    public static class ConditionData<T> implements InfluencerData<ConditionData<T>> {
        public T data;

        @Override
        public ConditionData<T> create() {
            return new ConditionData<>();
        }
    }

    public interface InfluencerCondition<T> {

        void initialize(ParticleData particleData, ConditionData<T> influencerData);

        /**
         *
         * @return true if the condition is met
         */
        boolean update(ParticleData particleData, ConditionData<T> influencerData, float tpf);
    }


    private InfluencerCondition condition;

    public ConditionInfluencer() {
    }

    public ConditionInfluencer(ParticleInfluencer particleInfluencer, InfluencerCondition condition) {
        super(particleInfluencer);

        this.condition = condition;
    }

    public InfluencerCondition getCondition() {
        return condition;
    }

    public void setCondition(InfluencerCondition condition) {
        this.condition = condition;
    }

    @Override
    protected void firstInitializeImpl(@NotNull ParticleData particleData) {
        super.firstInitializeImpl(particleData);

        particleInfluencer.setEnabled(false);
    }

    @Override
    protected void initializeImpl(@NotNull ParticleData particleData, ConditionData influencerData) {
        super.initializeImpl(particleData, influencerData);

        condition.initialize(particleData, influencerData);
    }

    @Override
    protected void updateImpl(@NotNull ParticleData particleData, final ConditionData influencerData, float tpf) {
        // This is relying in the fact that influencers are being called in order for every particle.
        particleInfluencer.setEnabled(condition.update(particleData, influencerData, tpf));
    }

    @Override
    public @NotNull String getName() {
        return ConditionInfluencer.class.getSimpleName();
    }
}
