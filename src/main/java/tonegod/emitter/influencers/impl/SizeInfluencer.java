package tonegod.emitter.influencers.impl;

import com.jme3.export.*;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;
import tonegod.emitter.util.RandomUtils;

import java.io.IOException;
import java.util.Random;

/**
 * The implementation of the {@link ParticleInfluencer} to change size of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class SizeInfluencer extends AbstractInterpolatedParticleInfluencer<SizeInfluencer.SizeInfluencerData> {

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

    public SizeInfluencer(float first, @NotNull float... sizes) {
        this();
        addSize(first);
        for (float size : sizes) {
            addSize(size);
        }
    }

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
    public @NotNull SizeInfluencer.SizeInfluencerData newDataObject() {
        return new SizeInfluencerData();
    }

    @Override
    protected void updateImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull SizeInfluencer.SizeInfluencerData data,
            float tpf
    ) {

        data.interval += tpf;

        if (data.index >= sizes.size()) {
            data.index = 0;
        }

        if (data.interval >= data.duration) {
            updateSize(data, particleData);
        }

        Interpolation interpolation = data.interpolation;

        blend = interpolation.apply(data.interval / data.duration);
        particleData.size.interpolateLocal(data.startSize, data.endSize, blend);

        super.updateImpl(emitterNode, particleData, data, tpf);
    }

    /**
     * Updates the particle's size.
     *
     * @param data the influencer's data.
     * @param particleData the particle's data.
     */
    private void updateSize(@NotNull SizeInfluencerData data, @NotNull ParticleData particleData) {
        data.index++;

        if (data.index >= sizes.size()) {
            data.index = 0;
        }

        calculateNextSizeRange(data, particleData);

        SafeArrayList<Interpolation> interpolations = getInterpolations();
        data.interpolation = interpolations.get(data.index);
        data.interval -= data.duration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull ParticleData particleData) {

        SafeArrayList<Vector3f> sizes = getSizes();

        if (sizes.isEmpty()) {
            addSize(1f);
            addSize(0f);
        } else if (sizes.size() == 1) {
            setEnabled(false);
        }

        super.firstInitializeImpl(particleData);
    }

    @Override
    protected void initializeImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull SizeInfluencer.SizeInfluencerData data
    ) {

        SafeArrayList<Interpolation> interpolations = getInterpolations();

        data.index = 0;
        data.interval = 0F;
        data.duration = isCycle() ? getFixedDuration() :
            particleData.startLife / ((float) interpolations.size() - 1 - data.index);

        calculateNextSizeRange(data, particleData);

        data.interpolation = interpolations.get(data.index);

        super.initializeImpl(emitterNode, particleData, data);
    }

    /**
     * Calculates next size.
     *
     * @param data the influencer's data.
     * @param particleData the particle's data.
     */
    private void calculateNextSizeRange(@NotNull SizeInfluencerData data, @NotNull ParticleData particleData) {

        SafeArrayList<Vector3f> sizes = getSizes();

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
                Random random = RandomUtils.getRandom();
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
    protected void resetImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull SizeInfluencer.SizeInfluencerData data
    ) {
        particleData.size.set(1, 1, 1);
        super.resetImpl(emitterNode, particleData, data);
    }

    /**
     * Adds the new size.
     *
     * @param size the size.
     */
    public void addSize(float size) {
        addSize(size, Interpolation.LINEAR);
    }

    /**
     * Adds the new size.
     *
     * @param size the size.
     */
    public void addSize(@NotNull Vector3f size) {
        addSize(size, Interpolation.LINEAR);
    }

    /**
     * Adds the new size with the interpolation.
     *
     * @param size          the size.
     * @param interpolation the interpolation.
     */
    public void addSize(float size, @NotNull Interpolation interpolation) {
        addSize(new Vector3f(size, size, size), interpolation);
    }

    /**
     * Adds the new size with the interpolation.
     *
     * @param size          the size.
     * @param interpolation the interpolation.
     */
    public void addSize(@NotNull Vector3f size, @NotNull Interpolation interpolation) {
        addInterpolation(interpolation);
        sizes.add(size.clone());
    }

    /**
     * Gets the list of sizes.
     *
     * @return the list of sizes.
     */
    public @NotNull SafeArrayList<Vector3f> getSizes() {
        return sizes;
    }

    /**
     * Gets size by the index.
     *
     * @param index the index.
     * @return the size by the index.
     */
    public @NotNull Vector3f getSize(int index) {
        return sizes.get(index);
    }

    /**
     * Sets the size by the index.
     *
     * @param size  the new size.
     * @param index the index.
     */
    public void updateSize(@NotNull Vector3f size, int index) {
        sizes.set(index, size);
    }

    /**
     * Removes a size and interpolation for the index.
     *
     * @param index the index.
     */
    public void removeSize(int index) {
        removeInterpolation(index);
        sizes.remove(index);
    }

    /**
     * Removes all sizes with interpolations.
     */
    public void removeAll() {
        clearInterpolations();
        sizes.clear();
    }

    /**
     * Removes a last size and interpolation.
     */
    public void removeLast() {

        SafeArrayList<Vector3f> sizes = getSizes();
        if (sizes.isEmpty()) {
            return;
        }

        int index = sizes.size() - 1;

        removeInterpolation(index);
        sizes.remove(index);
    }

    /**
     * Sets true if need to use random size.
     *
     * @param randomSize true if need to use random size.
     */
    public void setRandomSize(boolean randomSize) {
        this.randomSize = randomSize;
    }

    /**
     * Returns true if the random size is enabled.
     *
     * @return true if the random size is enabled.
     */
    public boolean isRandomSize() {
        return randomSize;
    }

    /**
     * Sets random size tolerance.
     *
     * @param randomSizeTolerance the random size tolerance.
     */
    public void setRandomSizeTolerance(float randomSizeTolerance) {
        this.randomSizeTolerance = randomSizeTolerance;
    }

    /**
     * Gets random size tolerance.
     *
     * @return the random size tolerance.
     */
    public float getRandomSizeTolerance() {
        return randomSizeTolerance;
    }

    @Override
    public void write(@NotNull JmeExporter exporter) throws IOException {
        super.write(exporter);

        OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(sizes.toArray(new Vector3f[sizes.size()]), "sizes", null);
        capsule.write(randomSize, "randomSize", false);
        capsule.write(randomSizeTolerance, "randomSizeTolerance", 0.5f);
    }

    @Override
    public void read(@NotNull JmeImporter importer) throws IOException {
        super.read(importer);

        InputCapsule capsule = importer.getCapsule(this);
        Savable[] readSizes = capsule.readSavableArray("sizes", null);

        if (readSizes != null) {
            for (Savable size : readSizes) {
                sizes.add((Vector3f) size);
            }
        }

        randomSize = capsule.readBoolean("randomSize", false);
        randomSizeTolerance = capsule.readFloat("randomSizeTolerance", 0.5f);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        SizeInfluencer clone = (SizeInfluencer) super.clone();
        clone.sizes = new SafeArrayList<>(Vector3f.class);
        clone.sizes.addAll(sizes);
        clone.setRandomSizeTolerance(randomSizeTolerance);
        clone.setRandomSize(randomSize);
        return clone;
    }
}
