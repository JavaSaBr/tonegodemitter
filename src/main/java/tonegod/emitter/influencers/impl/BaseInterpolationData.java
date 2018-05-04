package tonegod.emitter.influencers.impl;

import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.interpolation.Interpolation;

/**
 * @author JavaSaBr
 */
public class BaseInterpolationData implements JmeCloneable {

    /**
     * The interpolation.
     */
    @NotNull
    Interpolation interpolation;

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
    public @NotNull Object jmeClone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cloneFields(@NotNull Cloner cloner, @NotNull Object original) {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BaseInterpolationData that = (BaseInterpolationData) obj;
        if (index != that.index) return false;
        if (Float.compare(that.interval, interval) != 0) return false;
        if (Float.compare(that.duration, duration) != 0) return false;
        return interpolation.equals(that.interpolation);
    }

    @Override
    public int hashCode() {
        int result = interpolation.hashCode();
        result = 31 * result + index;
        result = 31 * result + (interval != +0.0f ? Float.floatToIntBits(interval) : 0);
        result = 31 * result + (duration != +0.0f ? Float.floatToIntBits(duration) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseInterpolationData{" + "interpolation=" + interpolation + ", index=" + index + ", interval=" +
                interval + ", duration=" + duration + '}';
    }
}