package tonegod.emitter.influencers.impl;

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

import static com.jme3.math.FastMath.interpolateLinear;

/**
 * The implementation of the {@link ParticleInfluencer} to change alpha of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class AlphaInfluencer extends AbstractInterpolatedParticleInfluencer<BaseInterpolationData> {

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
    protected void updateImpl(@NotNull final ParticleData particleData, final BaseInterpolationData data, final float tpf) {
        data.interval += tpf;

        if (data.index >= alphas.size()) {
            data.index = 0;
        }

        if (data.interval >= data.duration) {
            updateInterpolation(data, getAlphas());
        }

        final Interpolation interpolation = data.interpolation;
        final SafeArrayList<Float> alphas = getAlphas();
        final Float[] alphasArray = alphas.getArray();
        final int alphaIndex = data.index;

        blend = interpolation.apply(data.interval / data.duration);
        startAlpha = alphasArray[alphaIndex];

        final float endAlpha;

        if (alphaIndex == alphas.size() - 1) {
            endAlpha = alphasArray[0];
        } else {
            endAlpha = alphasArray[alphaIndex + 1];
        }

        particleData.alpha = interpolateLinear(blend, startAlpha, endAlpha);

        super.updateImpl(particleData, data, tpf);
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
    protected void initializeImpl(@NotNull final ParticleData particleData, final BaseInterpolationData data) {
        final SafeArrayList<Interpolation> interpolations = getInterpolations();

        if (isRandomStartAlpha()) {
            data.index = FastMath.nextRandomInt(0, interpolations.size() - 1);
        } else {
            data.index = 0;
        }

        data.interval = 0F;
        data.duration = isCycle() ? getFixedDuration() : particleData.startLife / ((float) interpolations.size() - 1);

        particleData.alpha = alphas.get(data.index);

        data.interpolation = interpolations.get(data.index);

        super.initializeImpl(particleData, data);
    }

    /**
     * Return true if enabled using random start alpha.
     *
     * @return true if enabled using random start alpha.
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
     * Adds the alpha step value using linear interpolation to the chain of values used throughout the particles life
     * span
     *
     * @param alpha the alpha step.
     */
    public void addAlpha(final float alpha) {
        addAlpha(alpha, Interpolation.LINEAR);
    }

    /**
     * Adds the alpha step value to the chain of values used throughout the particles life span
     *
     * @param alpha         the alpha step.
     * @param interpolation the interpolation.
     */
    public void addAlpha(final float alpha, @NotNull final Interpolation interpolation) {
        addInterpolation(interpolation);
        alphas.add(alpha);
    }

    /**
     * Returns the array with all alpha step values
     *
     * @return the alpha steps.
     */
    public @NotNull SafeArrayList<Float> getAlphas() {
        return alphas;
    }

    /**
     * Get the alpha step by the index.
     *
     * @param index the index.
     * @return the alpha for the index.
     */
    public @NotNull Float getAlpha(final int index) {
        return alphas.get(index);
    }

    /**
     * Change the alpha step by the index.
     *
     * @param alpha the new alpha step.
     * @param index the index.
     */
    public void updateAlpha(@NotNull final Float alpha, final int index) {
        alphas.set(index, alpha);
    }

    /**
     * Remove the last alpha and interpolation.
     */
    public void removeLast() {

        final SafeArrayList<Float> alphas = getAlphas();
        if (alphas.isEmpty()) {
            return;
        }

        final int index = alphas.size() - 1;

        removeInterpolation(index);
        alphas.remove(index);
    }

    /**
     * Remove the alpha step value at the given index.
     *
     * @param index the index.
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
