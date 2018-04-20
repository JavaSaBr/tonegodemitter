package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.influencers.InterpolatedParticleInfluencer;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;

import java.io.IOException;
import java.util.List;

/**
 * The base implementation of the {@link InterpolatedParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractInterpolatedParticleInfluencer<D> extends AbstractWithDataParticleInfluencer<D>
    implements InterpolatedParticleInfluencer<D> {

    /**
     * The list of interpolations.
     */
    @NotNull
    private SafeArrayList<Interpolation> interpolations;

    /**
     * The fixed duration.
     */
    private float fixedDuration;

    /**
     * The blend value.
     */
    protected float blend;

    /**
     * The flag of cycling changing.
     */
    private boolean cycle;

    public AbstractInterpolatedParticleInfluencer() {
        this.interpolations = new SafeArrayList<>(Interpolation.class);
    }

    @Override
    public boolean isUsedDataObject() {
        return true;
    }

    /**
     * Update the interpolation.
     *
     * @param data  the influencer's data.
     * @param steps the list of steps.
     */
    protected void updateInterpolation(@NotNull BaseInterpolationData data, @NotNull List<?> steps) {

        data.index++;

        if (data.index >= steps.size()) {
            data.index = 0;
        }

        SafeArrayList<Interpolation> interpolations = getInterpolations();
        data.interpolation = interpolations.get(data.index);
        data.interval -= data.duration;
    }

    @Override
    public final int getStepCount() {
        return interpolations.size();
    }

    /**
     * Add a new interpolation to the list.
     *
     * @param interpolation the interpolation
     */
    protected final void addInterpolation(@NotNull Interpolation interpolation) {
        interpolations.add(interpolation);
    }

    /**
     * Remove the interpolation from the list.
     *
     * @param index the index
     */
    protected final void removeInterpolation(int index) {
        interpolations.remove(index);
    }

    /**
     * Remove all interpolations from the list.
     */
    protected final void clearInterpolations() {
        interpolations.clear();
    }

    @Override
    public final boolean isCycle() {
        return cycle;
    }

    /**
     * Sets cycle.
     *
     * @param cycle the flag of cycling changing.
     */
    protected final void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    @Override
    public final void setFixedDuration(float fixedDuration) {
        if (fixedDuration != 0) {
            this.cycle = true;
            this.fixedDuration = fixedDuration;
        } else {
            this.cycle = false;
            this.fixedDuration = 0;
        }
    }

    /**
     * Returns the current duration used between frames for cycled animation
     */
    public final float getFixedDuration() {
        return fixedDuration;
    }

    @Override
    public final @NotNull Interpolation getInterpolation(int index) throws RuntimeException {
        if (index < 0 || index >= interpolations.size()) {
            throw new RuntimeException("The index " + index + " isn't correct.");
        }
        return interpolations.get(index);
    }

    @Override
    public final void updateInterpolation(@NotNull Interpolation interpolation, int index) throws RuntimeException {
        if (index < 0 || index >= interpolations.size()) {
            throw new RuntimeException("The index " + index + " isn't correct.");
        }
        interpolations.set(index, interpolation);
    }

    @Override
    public final @NotNull SafeArrayList<Interpolation> getInterpolations() {
        return interpolations;
    }

    @Override
    public void write(@NotNull JmeExporter exporter) throws IOException {
        super.write(exporter);

        int[] interpolationIds = new int[interpolations.size()];

        for (int i = 0; i < interpolations.size(); i++) {
            interpolationIds[i] = InterpolationManager.getId(interpolations.get(i));
        }

        OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(interpolationIds, "interpolations", null);
        capsule.write(cycle, "cycle", false);
        capsule.write(fixedDuration, "fixedDuration", 0.125f);
    }

    @Override
    public void read(@NotNull JmeImporter importer) throws IOException {
        super.read(importer);

        InputCapsule capsule = importer.getCapsule(this);
        int[] interpolationIds = capsule.readIntArray("interpolations", null);

        for (int id : interpolationIds) {
            interpolations.add(InterpolationManager.getInterpolation(id));
        }

        cycle = capsule.readBoolean("cycle", false);
        fixedDuration = capsule.readFloat("fixedDuration", 0.125f);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        AbstractInterpolatedParticleInfluencer clone = (AbstractInterpolatedParticleInfluencer) super.clone();
        clone.interpolations = new SafeArrayList<>(Interpolation.class);
        clone.interpolations.addAll(interpolations);
        clone.cycle = cycle;
        clone.fixedDuration = fixedDuration;
        return clone;
    }
}
