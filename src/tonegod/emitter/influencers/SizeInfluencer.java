package tonegod.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.UnsafeArray;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;
import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for influence to size of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class SizeInfluencer implements ParticleInfluencer {

    /**
     * The list of sizes.
     */
    private final UnsafeArray<Vector3f> sizes;

    /**
     * The list of colors.
     */
    private final UnsafeArray<Interpolation> interpolations;

    private final Vector3f tempV3a;
    private final Vector3f tempV3b;

    /**
     * The start size.
     */
    private final Vector3f startSize;

    /**
     * The end size.
     */
    private final Vector3f endSize;

    private float blend;
    private float randomSizeTolerance;
    private float fixedDuration;

    private boolean initialized;
    private boolean randomSize;
    private boolean cycle;
    private boolean enabled;

    public SizeInfluencer() {
        this.sizes = ArrayFactory.newUnsafeArray(Vector3f.class);
        this.interpolations = ArrayFactory.newUnsafeArray(Interpolation.class);
        this.enabled = true;
        this.tempV3a = new Vector3f();
        this.tempV3b = new Vector3f();
        this.startSize = new Vector3f(0.1f, 0.1f, 0.1f);
        this.endSize = new Vector3f(0, 0, 0);
        this.randomSizeTolerance = 0.5f;
    }

    @NotNull
    @Override
    public String getName() {
        return "Size influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled) return;

        particleData.sizeInterval += tpf;

        if (particleData.sizeInterval >= particleData.sizeDuration) {
            updateSize(particleData);
        }

        blend = particleData.sizeInterpolation.apply(particleData.sizeInterval / particleData.sizeDuration);
        particleData.size.interpolateLocal(particleData.startSize, particleData.endSize, blend);
    }

    private void updateSize(@NotNull final ParticleData particleData) {
        particleData.sizeIndex++;

        if (particleData.sizeIndex == sizes.size() - 1) {
            particleData.sizeIndex = 0;
        }

        getNextSizeRange(particleData);

        particleData.sizeInterpolation = interpolations.get(particleData.sizeIndex);
        particleData.sizeInterval -= particleData.sizeDuration;
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {

        if (!initialized) {
            if (sizes.isEmpty()) {
                addSize(1f);
                addSize(0f);
            } else if (sizes.size() == 1) {
                setEnabled(false);
            }
            initialized = true;
        }

        particleData.sizeIndex = 0;
        particleData.sizeInterval = 0f;
        particleData.sizeDuration = (cycle) ? fixedDuration : particleData.startlife / ((float) sizes.size() - 1 - particleData.sizeIndex);

        getNextSizeRange(particleData);

        particleData.sizeInterpolation = interpolations.get(particleData.sizeIndex);
    }

    private void getNextSizeRange(@NotNull final ParticleData particleData) {
        if (particleData.sizeIndex == 0) { //endSize.equals(Vector3f.ZERO)) {
            particleData.startSize.set(sizes.get(particleData.sizeIndex));
            if (randomSize) {
                tempV3a.set(particleData.startSize);
                tempV3b.set(tempV3a).multLocal(randomSizeTolerance);
                tempV3a.subtractLocal(tempV3b);
                tempV3b.multLocal(FastMath.nextRandomFloat());
                tempV3a.addLocal(tempV3b);
                particleData.startSize.set(tempV3a);
            }
        } else {
            particleData.startSize.set(particleData.endSize);
        }

        if (sizes.size() > 1) {

            if (particleData.sizeIndex == sizes.size() - 1) {
                particleData.endSize.set(sizes.get(0));
            } else {
                particleData.endSize.set(sizes.get(particleData.sizeIndex + 1));
            }

            if (randomSize) {
                tempV3a.set(particleData.endSize);
                tempV3b.set(tempV3a).multLocal(randomSizeTolerance);
                tempV3a.subtractLocal(tempV3b);
                tempV3b.multLocal(FastMath.nextRandomFloat());
                tempV3a.addLocal(tempV3b);
                particleData.endSize.set(tempV3a);
            }

        } else {
            particleData.endSize.set(particleData.startSize);
        }

        particleData.size.set(particleData.startSize);
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.size.set(0, 0, 0);
        particleData.startSize.set(0f, 0f, 0f);
        particleData.endSize.set(0, 0, 0);
    }

    public void addSize(final float size) {
        addSize(size, Interpolation.LINEAR);
    }

    public void addSize(@NotNull final Vector3f size) {
        addSize(size, Interpolation.LINEAR);
    }

    public void addSize(final float size, @NotNull final Interpolation interpolation) {
        addSize(new Vector3f(size, size, size), interpolation);
    }

    public void addSize(@NotNull final Vector3f size, @NotNull final Interpolation interpolation) {
        sizes.add(size.clone());
        interpolations.add(interpolation);
    }

    /**
     * @return the list of sizes.
     */
    @NotNull
    public Array<Vector3f> getSizes() {
        return sizes;
    }

    /**
     * @param index the index.
     * @return the size for the index.
     */
    @NotNull
    public Vector3f getSize(final int index) {
        return sizes.get(index);
    }

    /**
     * @return the list of interpolations.
     */
    @NotNull
    public Array<Interpolation> getInterpolations() {
        return interpolations;
    }

    /**
     * @param index the index.
     * @return the interpolation for the index.
     */
    @NotNull
    public Interpolation getInterpolation(final int index) {
        return interpolations.get(index);
    }

    /**
     * Change a size for the index.
     *
     * @param size  the new size.
     * @param index the index.
     */
    public void updateSize(final @NotNull Vector3f size, final int index) {
        sizes.set(index, size);
    }

    /**
     * Change a interpolation for the index.
     *
     * @param interpolation the new interpolation.
     * @param index         the index.
     */
    public void updateInterpolation(final @NotNull Interpolation interpolation, final int index) {
        interpolations.set(index, interpolation);
    }

    /**
     * Remove a size and interpolation for the index.
     *
     * @param index the index.
     */
    public void removeSize(final int index) {
        sizes.slowRemove(index);
        interpolations.slowRemove(index);
    }

    /**
     * Remove all sizes with interpolations.
     */
    public void removeAll() {
        sizes.clear();
        interpolations.clear();
    }

    /**
     * Remove last a size and interpolation.
     */
    public void removeLast() {
        if (sizes.isEmpty()) return;
        interpolations.fastRemove(interpolations.size() - 1);
        sizes.fastRemove(sizes.size() - 1);
    }

    public void setRandomSize(final boolean randomSize) {
        this.randomSize = randomSize;
    }

    public boolean isRandomSize() {
        return randomSize;
    }

    public void setRandomSizeTolerance(final float randomSizeTolerance) {
        this.randomSizeTolerance = randomSizeTolerance;
    }

    public float getRandomSizeTolerance() {
        return randomSizeTolerance;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {

        final int[] interpolationIds = interpolations.stream()
                .mapToInt(InterpolationManager::getId).toArray();

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(sizes.toArray(new Vector3f[sizes.size()]), "sizes", null);
        capsule.write(interpolationIds, "interpolations", null);
        capsule.write(randomSize, "randomSize", false);
        capsule.write(randomSizeTolerance, "randomSizeTolerance", 0.5f);
        capsule.write(fixedDuration, "fixedDuration", 0f);
        capsule.write(enabled, "enabled", true);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {

        final InputCapsule capsule = importer.getCapsule(this);
        final Savable[] loadedSizes = capsule.readSavableArray("sizes", null);

        ArrayUtils.forEach(loadedSizes, sizes, (savable, toStore) -> toStore.add((Vector3f) savable));

        final int[] loadedInterpolations = capsule.readIntArray("interpolations", null);

        ArrayUtils.forEach(loadedInterpolations, interpolations,
                (id, toStore) -> toStore.add(InterpolationManager.getInterpolation(id)));

        randomSize = capsule.readBoolean("randomSize", false);
        randomSizeTolerance = capsule.readFloat("randomSizeTolerance", 0.5f);
        fixedDuration = capsule.readFloat("fixedDuration", 0f);
        enabled = capsule.readBoolean("enabled", true);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            SizeInfluencer clone = (SizeInfluencer) super.clone();
            clone.sizes.addAll(sizes);
            clone.interpolations.addAll(interpolations);
            clone.setFixedDuration(fixedDuration);
            clone.setRandomSizeTolerance(randomSizeTolerance);
            clone.setRandomSize(randomSize);
            clone.setEnabled(enabled);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Animated texture should cycle and use the provided duration between frames (0 diables
     * cycling)
     *
     * @param fixedDuration duration between frame updates
     */
    public void setFixedDuration(final float fixedDuration) {
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
    public float getFixedDuration() {
        return fixedDuration;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
