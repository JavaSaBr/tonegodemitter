package tonegod.emitter.influencers.impl;

import static com.jme3.math.FastMath.interpolateLinear;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.Messages;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;

/**
 * The implementation of the {@link ParticleInfluencer} to change alpha of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class AlphaInfluencer extends AbstractInterpolatedParticleInfluencer {

    /**
     * The list of alphas.
     */
    @NotNull
    private SafeArrayList<Float> alphas;

    /**
     * The start alpha.
     */
    private float startAlpha;

    /**
     * The flag of using random start alpha.
     */
    private boolean randomStartAlpha;

    public AlphaInfluencer() {
        this.alphas = new SafeArrayList<>(Float.class);
        this.startAlpha = 1;
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_ALPHA;
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        particleData.alphaInterval += tpf;

        if (particleData.alphaIndex >= alphas.size()) {
            particleData.alphaIndex = 0;
        }

        if (particleData.alphaInterval >= particleData.alphaDuration) {
            updateAlpha(particleData);
        }

        final Interpolation interpolation = particleData.alphaInterpolation;
        final SafeArrayList<Float> alphas = getAlphas();
        final Float[] alphasArray = alphas.getArray();
        final int alphaIndex = particleData.alphaIndex;

        blend = interpolation.apply(particleData.alphaInterval / particleData.alphaDuration);
        startAlpha = alphasArray[alphaIndex];

        final float endAlpha;

        if (alphaIndex == alphas.size() - 1) {
            endAlpha = alphasArray[0];
        } else {
            endAlpha = alphasArray[alphaIndex + 1];
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

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
        particleData.alphaInterpolation = interpolations.get(particleData.alphaIndex);
        particleData.alphaInterval -= particleData.alphaDuration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull final ParticleData particleData) {

        final SafeArrayList<Float> alphas = getAlphas();

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

        final SafeArrayList<Interpolation> interpolations = getInterpolations();

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
     * Is random start alpha boolean.
     *
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
     * Adds a alpha step value using linear interpolation to the chain of values used throughout the particles life
     * span
     *
     * @param alpha the alpha
     */
    public void addAlpha(final float alpha) {
        addAlpha(alpha, Interpolation.LINEAR);
    }

    /**
     * Adds a alpha step value to the chain of values used throughout the particles life span
     *
     * @param alpha         the alpha
     * @param interpolation the interpolation
     */
    public void addAlpha(final float alpha, @NotNull final Interpolation interpolation) {
        addInterpolation(interpolation);
        alphas.add(alpha);
    }

    /**
     * Returns an array containing all alpha step values
     *
     * @return the alphas
     */
    public @NotNull SafeArrayList<Float> getAlphas() {
        return alphas;
    }

    /**
     * Gets alpha.
     *
     * @param index the index.
     * @return the alpha for the index.
     */
    public @NotNull Float getAlpha(final int index) {
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

        final SafeArrayList<Float> alphas = getAlphas();
        if (alphas.isEmpty()) return;

        final int index = alphas.size() - 1;

        removeInterpolation(index);
        alphas.remove(index);
    }

    /**
     * Removes the alpha step value at the given index
     *
     * @param index the index
     */
    public void removeAlpha(final int index) {
        removeInterpolation(index);
        alphas.remove(index);
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

        final Float[] values = alphas.getArray();
        final double[] alphasToSave = new double[alphas.size()];

        for (int i = 0; i < values.length; i++) {
            alphasToSave[i] = values[i];
        }

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(alphasToSave, "alphas", null);
        capsule.write(randomStartAlpha, "randomStartAlpha", false);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);
        final double[] readAlphas = capsule.readDoubleArray("alphas", null);

        if (readAlphas != null) {
            for (final double value : readAlphas) {
                alphas.add((float) value);
            }
        }

        randomStartAlpha = capsule.readBoolean("randomStartAlpha", false);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        final AlphaInfluencer clone = (AlphaInfluencer) super.clone();
        clone.alphas = new SafeArrayList<>(Float.class);
        clone.alphas.addAll(alphas);
        clone.randomStartAlpha = randomStartAlpha;
        return clone;
    }
}
