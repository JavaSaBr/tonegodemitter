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
import java.util.concurrent.Callable;

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
    public abstract @NotNull D newDataObject();

    @Override
    public boolean isUsedDataObject() {
        return true;
    }

    /**
     * Update the interpolation.
     *
     * @param data the influencer's data.
     */
    protected void updateInterpolation(@NotNull final BaseInterpolationData data, @NotNull final List<?> steps) {
        data.index++;

        if (data.index >= steps.size()) {
            data.index = 0;
        }

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
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
    protected final void addInterpolation(@NotNull final Interpolation interpolation) {
        interpolations.add(interpolation);
    }

    /**
     * Remove the interpolation from the list.
     *
     * @param index the index
     */
    protected final void removeInterpolation(final int index) {
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
    protected final void setCycle(final boolean cycle) {
        this.cycle = cycle;
    }

    @Override
    public final void setFixedDuration(final float fixedDuration) {
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
    public final @NotNull Interpolation getInterpolation(final int index) throws RuntimeException {
        if (index < 0 || index >= interpolations.size()) {
            throw new RuntimeException("The index " + index + " isn't correct.");
        }
        return interpolations.get(index);
    }

    @Override
    public final void updateInterpolation(final @NotNull Interpolation interpolation, final int index) throws RuntimeException {
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
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final int[] interpolationIds = new int[interpolations.size()];

        for (int i = 0; i < interpolations.size(); i++) {
            interpolationIds[i] = InterpolationManager.getId(interpolations.get(i));
        }

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(interpolationIds, "interpolations", null);
        capsule.write(cycle, "cycle", false);
        capsule.write(fixedDuration, "fixedDuration", 0.125f);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);
        final int[] interpolationIds = capsule.readIntArray("interpolations", null);

        for (final int id : interpolationIds) {
            interpolations.add(InterpolationManager.getInterpolation(id));
        }

        cycle = capsule.readBoolean("cycle", false);
        fixedDuration = capsule.readFloat("fixedDuration", 0.125f);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        final AbstractInterpolatedParticleInfluencer clone = (AbstractInterpolatedParticleInfluencer) super.clone();
        clone.interpolations = new SafeArrayList<>(Interpolation.class);
        clone.interpolations.addAll(interpolations);
        clone.cycle = cycle;
        clone.fixedDuration = fixedDuration;
        return clone;
    }
}
