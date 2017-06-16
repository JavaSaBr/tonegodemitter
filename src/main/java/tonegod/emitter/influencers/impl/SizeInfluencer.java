package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;

import com.ss.rlib.util.ArrayUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.array.UnsafeArray;
import tonegod.emitter.Messages;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;
import tonegod.emitter.util.RandomUtils;

/**
 * The implementation of the {@link ParticleInfluencer} to change size of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class SizeInfluencer extends AbstractInterpolatedParticleInfluencer {

    /**
     * The list of sizes.
     */
    @NotNull
    private UnsafeArray<Vector3f> sizes;

    /**
     * The vectors for temp calculating.
     */
    @NotNull
    private final Vector3f tempV3a;
    @NotNull
    private final Vector3f tempV3b;

    /**
     * The random size tolerance value.
     */
    private float randomSizeTolerance;

    /**
     * The flag of using random size.
     */
    private boolean randomSize;

    /**
     * Instantiates a new Size influencer.
     */
    public SizeInfluencer() {
        this.sizes = ArrayFactory.newUnsafeArray(Vector3f.class);
        this.tempV3a = new Vector3f();
        this.tempV3b = new Vector3f();
        this.randomSizeTolerance = 0.5f;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.PARTICLE_INFLUENCER_SIZE;
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        particleData.sizeInterval += tpf;

        if (particleData.sizeIndex >= sizes.size()) {
            particleData.sizeIndex = 0;
        }

        if (particleData.sizeInterval >= particleData.sizeDuration) {
            updateSize(particleData);
        }

        final Interpolation interpolation = particleData.sizeInterpolation;

        blend = interpolation.apply(particleData.sizeInterval / particleData.sizeDuration);
        particleData.size.interpolateLocal(particleData.startSize, particleData.endSize, blend);

        super.updateImpl(particleData, tpf);
    }

    /**
     * Update a size for the particle data.
     *
     * @param particleData the particle data.
     */
    private void updateSize(@NotNull final ParticleData particleData) {
        particleData.sizeIndex++;

        if (particleData.sizeIndex >= sizes.size()) {
            particleData.sizeIndex = 0;
        }

        calculateNextSizeRange(particleData);

        final Array<Interpolation> interpolations = getInterpolations();
        particleData.sizeInterpolation = interpolations.get(particleData.sizeIndex);
        particleData.sizeInterval -= particleData.sizeDuration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull final ParticleData particleData) {

        final Array<Vector3f> sizes = getSizes();

        if (sizes.isEmpty()) {
            addSize(1f);
            addSize(0f);
        } else if (sizes.size() == 1) {
            setEnabled(false);
        }

        super.firstInitializeImpl(particleData);
    }

    @Override
    protected void initializeImpl(@NotNull final ParticleData particleData) {

        final Array<Interpolation> interpolations = getInterpolations();

        particleData.sizeIndex = 0;
        particleData.sizeInterval = 0F;
        particleData.sizeDuration = isCycle() ? getFixedDuration() : particleData.startlife / ((float) interpolations.size() - 1 - particleData.sizeIndex);

        calculateNextSizeRange(particleData);

        particleData.sizeInterpolation = interpolations.get(particleData.sizeIndex);

        super.initializeImpl(particleData);
    }

    /**
     * Calculate a next size.
     *
     * @param particleData the particle data.
     */
    private void calculateNextSizeRange(@NotNull final ParticleData particleData) {

        final Array<Vector3f> sizes = getSizes();

        if (particleData.sizeIndex == 0) {

            particleData.startSize.set(sizes.get(particleData.sizeIndex));

            if (isRandomSize()) {
                final Random random = RandomUtils.getRandom();
                tempV3a.set(particleData.startSize);
                tempV3b.set(tempV3a).multLocal(randomSizeTolerance);
                tempV3a.subtractLocal(tempV3b);
                tempV3b.multLocal(random.nextFloat());
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

            if (isRandomSize()) {
                final Random random = RandomUtils.getRandom();
                tempV3a.set(particleData.endSize);
                tempV3b.set(tempV3a).multLocal(randomSizeTolerance);
                tempV3a.subtractLocal(tempV3b);
                tempV3b.multLocal(random.nextFloat());
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
        super.reset(particleData);
    }

    /**
     * Add the new size.
     *
     * @param size the size.
     */
    public void addSize(final float size) {
        addSize(size, Interpolation.LINEAR);
    }

    /**
     * Add the new size.
     *
     * @param size the size.
     */
    public void addSize(@NotNull final Vector3f size) {
        addSize(size, Interpolation.LINEAR);
    }

    /**
     * Add the new size with the interpolation.
     *
     * @param size          the size.
     * @param interpolation the interpolation.
     */
    public void addSize(final float size, @NotNull final Interpolation interpolation) {
        addSize(new Vector3f(size, size, size), interpolation);
    }

    /**
     * Add the new size with the interpolation.
     *
     * @param size          the size.
     * @param interpolation the interpolation.
     */
    public void addSize(@NotNull final Vector3f size, @NotNull final Interpolation interpolation) {
        addInterpolation(interpolation);
        sizes.add(size.clone());
    }

    /**
     * Gets sizes.
     *
     * @return the list of sizes.
     */
    @NotNull
    public Array<Vector3f> getSizes() {
        return sizes;
    }

    /**
     * Gets size.
     *
     * @param index the index.
     * @return the size for the index.
     */
    @NotNull
    public Vector3f getSize(final int index) {
        return sizes.get(index);
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
     * Remove a size and interpolation for the index.
     *
     * @param index the index.
     */
    public void removeSize(final int index) {
        removeInterpolation(index);
        sizes.slowRemove(index);
    }

    /**
     * Remove all sizes with interpolations.
     */
    public void removeAll() {
        clearInterpolations();
        sizes.clear();
    }

    /**
     * Remove a last size and interpolation.
     */
    public void removeLast() {

        final Array<Vector3f> sizes = getSizes();
        if (sizes.isEmpty()) return;

        final int index = sizes.size() - 1;

        removeInterpolation(index);
        sizes.fastRemove(index);
    }

    /**
     * Sets random size.
     *
     * @param randomSize the flag of using random size.
     */
    public void setRandomSize(final boolean randomSize) {
        this.randomSize = randomSize;
    }

    /**
     * Is random size boolean.
     *
     * @return true if the random size is enabled.
     */
    public boolean isRandomSize() {
        return randomSize;
    }

    /**
     * Sets random size tolerance.
     *
     * @param randomSizeTolerance the random size tolerance value.
     */
    public void setRandomSizeTolerance(final float randomSizeTolerance) {
        this.randomSizeTolerance = randomSizeTolerance;
    }

    /**
     * Gets random size tolerance.
     *
     * @return the random size tolerance value.
     */
    public float getRandomSizeTolerance() {
        return randomSizeTolerance;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(sizes.toArray(new Vector3f[sizes.size()]), "sizes", null);
        capsule.write(randomSize, "randomSize", false);
        capsule.write(randomSizeTolerance, "randomSizeTolerance", 0.5f);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);

        ArrayUtils.forEach(capsule.readSavableArray("sizes", null), sizes,
                (savable, toStore) -> toStore.add((Vector3f) savable));

        randomSize = capsule.readBoolean("randomSize", false);
        randomSizeTolerance = capsule.readFloat("randomSizeTolerance", 0.5f);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final SizeInfluencer clone = (SizeInfluencer) super.clone();
        clone.sizes = ArrayFactory.newUnsafeArray(Vector3f.class);
        clone.sizes.addAll(sizes);
        clone.setRandomSizeTolerance(randomSizeTolerance);
        clone.setRandomSize(randomSize);
        return clone;
    }
}
