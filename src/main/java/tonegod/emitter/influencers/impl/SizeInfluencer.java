package tonegod.emitter.influencers.impl;

import com.jme3.export.*;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.Messages;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;
import tonegod.emitter.util.RandomUtils;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * The implementation of the {@link ParticleInfluencer} to change size of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class SizeInfluencer extends AbstractInterpolatedParticleInfluencer {

    private static final int DATA_ID = ParticleData.reserveObjectDataId();

    @NotNull
    protected static final Callable<SizeInfluencerData> DATA_FACTORY = new Callable<SizeInfluencerData>() {
        @Override
        public SizeInfluencerData call() throws Exception {
            return new SizeInfluencerData();
        }
    };

    protected static class SizeInfluencerData extends BaseInterpolationData {

        /**
         * The start size.
         */
        @NotNull
        public final Vector3f startSize;

        /**
         * The end size.
         */
        @NotNull
        public final Vector3f endSize;

        private SizeInfluencerData() {
            this.startSize = new Vector3f(1, 1, 1);
            this.endSize = new Vector3f(0, 0, 0);
        }
    }

    /**
     * The list of sizes.
     */
    @NotNull
    private SafeArrayList<Vector3f> sizes;

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

    public SizeInfluencer() {
        this.sizes = new SafeArrayList<>(Vector3f.class);
        this.tempV3a = new Vector3f();
        this.tempV3b = new Vector3f();
        this.randomSizeTolerance = 0.5f;
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_SIZE;
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        final SizeInfluencerData data = particleData.getObjectData(DATA_ID);
        data.interval += tpf;

        if (data.index >= sizes.size()) {
            data.index = 0;
        }

        if (data.interval >= data.duration) {
            updateSize(data, particleData);
        }

        final Interpolation interpolation = data.interpolation;

        blend = interpolation.apply(data.interval / data.duration);
        particleData.size.interpolateLocal(data.startSize, data.endSize, blend);

        super.updateImpl(particleData, tpf);
    }

    /**
     * Update size.
     *
     * @param data the influencer's data.
     * @param particleData the particle's data.
     */
    private void updateSize(@NotNull final SizeInfluencerData data, @NotNull final ParticleData particleData) {
        data.index++;

        if (data.index >= sizes.size()) {
            data.index = 0;
        }

        calculateNextSizeRange(data, particleData);

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
        data.interpolation = interpolations.get(data.index);
        data.interval -= data.duration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull final ParticleData particleData) {

        final SafeArrayList<Vector3f> sizes = getSizes();

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
        particleData.initializeObjectData(DATA_ID, DATA_FACTORY);

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
        final SizeInfluencerData data = particleData.getObjectData(DATA_ID);
        data.index = 0;
        data.interval = 0F;
        data.duration = isCycle() ? getFixedDuration() :
                particleData.startLife / ((float) interpolations.size() - 1 - data.index);

        calculateNextSizeRange(data, particleData);

        data.interpolation = interpolations.get(data.index);

        super.initializeImpl(particleData);
    }

    /**
     * Calculate next size.
     *
     * @param data the influencer's data.
     * @param particleData the particle's data.
     */
    private void calculateNextSizeRange(@NotNull final SizeInfluencerData data, @NotNull final ParticleData particleData) {

        final SafeArrayList<Vector3f> sizes = getSizes();

        if (data.index == 0) {

            data.startSize.set(sizes.get(data.index));

            if (isRandomSize()) {
                final Random random = RandomUtils.getRandom();
                tempV3a.set(data.startSize);
                tempV3b.set(tempV3a).multLocal(randomSizeTolerance);
                tempV3a.subtractLocal(tempV3b);
                tempV3b.multLocal(random.nextFloat());
                tempV3a.addLocal(tempV3b);
                data.startSize.set(tempV3a);
            }

        } else {
            data.startSize.set(data.endSize);
        }

        if (sizes.size() > 1) {

            if (data.index == sizes.size() - 1) {
                data.endSize.set(sizes.get(0));
            } else {
                data.endSize.set(sizes.get(data.index + 1));
            }

            if (isRandomSize()) {
                final Random random = RandomUtils.getRandom();
                tempV3a.set(data.endSize);
                tempV3b.set(tempV3a).multLocal(randomSizeTolerance);
                tempV3a.subtractLocal(tempV3b);
                tempV3b.multLocal(random.nextFloat());
                tempV3a.addLocal(tempV3b);
                data.endSize.set(tempV3a);
            }

        } else {
            data.endSize.set(data.startSize);
        }

        particleData.size.set(data.startSize);
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.size.set(1, 1, 1);
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
     * Get the list of sizes.
     *
     * @return the list of sizes.
     */
    public @NotNull SafeArrayList<Vector3f> getSizes() {
        return sizes;
    }

    /**
     * Get size by the index.
     *
     * @param index the index.
     * @return the size by the index.
     */
    public @NotNull Vector3f getSize(final int index) {
        return sizes.get(index);
    }

    /**
     * Set the size by the index.
     *
     * @param size  the new size.
     * @param index the index.
     */
    public void updateSize(@NotNull final Vector3f size, final int index) {
        sizes.set(index, size);
    }

    /**
     * Remove a size and interpolation for the index.
     *
     * @param index the index.
     */
    public void removeSize(final int index) {
        removeInterpolation(index);
        sizes.remove(index);
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

        final SafeArrayList<Vector3f> sizes = getSizes();
        if (sizes.isEmpty()) {
            return;
        }

        final int index = sizes.size() - 1;

        removeInterpolation(index);
        sizes.remove(index);
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
     * Return true if the random size is enabled.
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
        final Savable[] readSizes = capsule.readSavableArray("sizes", null);

        if (readSizes != null) {
            for (final Savable size : readSizes) {
                sizes.add((Vector3f) size);
            }
        }

        randomSize = capsule.readBoolean("randomSize", false);
        randomSizeTolerance = capsule.readFloat("randomSizeTolerance", 0.5f);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        final SizeInfluencer clone = (SizeInfluencer) super.clone();
        clone.sizes = new SafeArrayList<>(Vector3f.class);
        clone.sizes.addAll(sizes);
        clone.setRandomSizeTolerance(randomSizeTolerance);
        clone.setRandomSize(randomSize);
        return clone;
    }
}
