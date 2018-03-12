package tonegod.emitter.influencers.impl;

import org.jetbrains.annotations.NotNull;
import tonegod.emitter.influencers.InfluencerData;
import tonegod.emitter.interpolation.Interpolation;

public class BaseInterpolationData implements InfluencerData<BaseInterpolationData> {
    /**
     * The interpolation.
     */
    @NotNull Interpolation interpolation;

    /**
     * The index.
     */
    int index;

    /**
     * The interval.
     */
    float interval;

    /**
     * The duration.
     */
    float duration;

    protected BaseInterpolationData() {
        this.duration = 1f;
        this.interpolation = Interpolation.LINEAR;
    }

    @Override
    public BaseInterpolationData create() {
        return new BaseInterpolationData();
    }
}