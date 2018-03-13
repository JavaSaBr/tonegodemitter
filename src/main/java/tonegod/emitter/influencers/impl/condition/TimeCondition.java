package tonegod.emitter.influencers.impl.condition;

import tonegod.emitter.influencers.impl.ConditionInfluencer;
import tonegod.emitter.influencers.impl.ConditionInfluencer.ConditionData;
import tonegod.emitter.particle.ParticleData;

public class TimeCondition implements ConditionInfluencer.InfluencerCondition<Float> {

    private float time;

    public TimeCondition() { }

    public TimeCondition(float time) {
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    @Override
    public void initialize(ParticleData particleData, ConditionData<Float> influencerData) {
        influencerData.data = 0f;
    }

    @Override
    public boolean update(ParticleData particleData, ConditionData<Float> influencerData, float tpf) {
        return (influencerData.data += tpf) >= time;
    }
}
