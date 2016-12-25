package tonegod.emitter.influencers.impl;

import static com.jme3.math.FastMath.interpolateLinear;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.UnsafeArray;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for influence to alpha of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public final class AlphaInfluencer extends AbstractInterpolatedParticleInfluencer {

    /**
     * The list of alphas.
     */
    private final UnsafeArray<Float> alphas;

    /**
     * The start alpha.
     */
    private float startAlpha;

    /**
     * The end alpha.
     */
    private float endAlpha;

    /**
     * The flag of using random start alpha.
     */
    private boolean randomStartAlpha;

    public AlphaInfluencer() {
        this.alphas = ArrayFactory.newUnsafeArray(Float.class);
        this.startAlpha = 1;
    }

    @NotNull
    @Override
    public String getName() {
        return "Alpha influencer";
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        particleData.alphaInterval += tpf;

        if (particleData.alphaInterval >= particleData.alphaDuration) {
            updateAlpha(particleData);
        }

        blend = particleData.alphaInterpolation.apply(particleData.alphaInterval / particleData.alphaDuration);

        final Array<Float> alphas = getAlphas();
        final Float[] alphasArray = alphas.array();

        startAlpha = alphasArray[particleData.alphaIndex];

        if (particleData.alphaIndex == alphas.size() - 1) {
            endAlpha = alphasArray[0];
        } else {
            endAlpha = alphasArray[particleData.alphaIndex + 1];
        }

        particleData.alpha = interpolateLinear(blend, startAlpha, endAlpha);

        super.updateImpl(particleData, tpf);
    }

    /**
     * Update an alpha value for the particle data.
     *
     * @param particleData the particle data.
     */
    private void updateAlpha(@NotNull final ParticleData particleData) {
        particleData.alphaIndex++;

        if (particleData.alphaIndex >= alphas.size()) {
            particleData.alphaIndex = 0;
        }

        final Array<Interpolation> interpolations = getInterpolations();
        particleData.alphaInterpolation = interpolations.get(particleData.alphaIndex);
        particleData.alphaInterval -= particleData.alphaDuration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull final ParticleData particleData) {

        final Array<Float> alphas = getAlphas();

        if (alphas.isEmpty()) {
            addAlpha(1F);
            addAlpha(0F);
        } else if (alphas.size() == 1) {
            setEnabled(false);
        }

        super.firstInitializeImpl(particleData);
    }

    @Override
    protected void initializeImpl(@NotNull final ParticleData particleData) {

        final Array<Interpolation> interpolations = getInterpolations();

        if (isRandomStartAlpha()) {
            particleData.alphaIndex = FastMath.nextRandomInt(0, interpolations.size() - 1);
        } else {
            particleData.alphaIndex = 0;
        }

        particleData.alphaInterval = 0F;
        particleData.alphaDuration = isCycle() ? getFixedDuration() : particleData.startlife / ((float) interpolations.size() - 1);
        particleData.alpha = alphas.get(particleData.alphaIndex);
        particleData.alphaInterpolation = interpolations.get(particleData.alphaIndex);

        super.initializeImpl(particleData);
    }

    /**
     * @return true if using random start alpha.
     */
    public boolean isRandomStartAlpha() {
        return randomStartAlpha;
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.alpha = 0;
        super.reset(particleData);
    }

    /**
     * Adds a alpha step value using linear interpolation to the chain of values used throughout the
     * particles life span
     */
    public void addAlpha(final float alpha) {
        addAlpha(alpha, Interpolation.LINEAR);
    }

    /**
     * Adds a alpha step value to the chain of values used throughout the particles life span
     */
    public void addAlpha(final float alpha, @NotNull final Interpolation interpolation) {
        addInterpolation(interpolation);
        alphas.add(alpha);
    }

    /**
     * Returns an array containing all alpha step values
     */
    @NotNull
    public Array<Float> getAlphas() {
        return alphas;
    }

    /**
     * @param index the index.
     * @return the alpha for the index.
     */
    @NotNull
    public Float getAlpha(final int index) {
        return alphas.get(index);
    }

    /**
     * Change an alpha for the index.
     *
     * @param alpha the new alpha.
     * @param index the index.
     */
    public void updateAlpha(@NotNull final Float alpha, final int index) {
        alphas.set(index, alpha);
    }

    /**
     * Remove a last alpha and interpolation.
     */
    public void removeLast() {

        final Array<Float> alphas = getAlphas();
        if (alphas.isEmpty()) return;

        final int index = alphas.size() - 1;

        removeInterpolation(index);
        alphas.fastRemove(index);
    }

    /**
     * Removes the alpha step value at the given index
     */
    public void removeAlpha(final int index) {
        removeInterpolation(index);
        alphas.slowRemove(index);
    }

    /**
     * Removes all added alpha step values
     */
    public void removeAll() {
        clearInterpolations();
        alphas.clear();
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final double[] alphasToSave = alphas.stream().
                mapToDouble(value -> value).toArray();

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(alphasToSave, "alphas", null);
        capsule.write(randomStartAlpha, "randomStartAlpha", false);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);

        ArrayUtils.forEach(capsule.readDoubleArray("alphas", null), alphas,
                (element, toStore) -> toStore.add((float) element));

        randomStartAlpha = capsule.readBoolean("randomStartAlpha", false);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final AlphaInfluencer clone = (AlphaInfluencer) super.clone();
        clone.alphas.addAll(alphas);
        clone.randomStartAlpha = randomStartAlpha;
        return clone;
    }
}
