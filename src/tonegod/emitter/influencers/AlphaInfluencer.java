package tonegod.emitter.influencers;

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
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;
import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for influence to alpha of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class AlphaInfluencer implements ParticleInfluencer {

    /**
     * The list of interpolations.
     */
    private final UnsafeArray<Interpolation> interpolations;

    /**
     * The list of alphas.
     */
    private final UnsafeArray<Float> alphas;

    /**
     * The start alhpa.
     */
    private float startAlpha;

    /**
     * The end alpha.
     */
    private float endAlpha;

    private float blend;
    private float fixedDuration;

    private boolean randomStartAlpha;
    private boolean initialized;
    private boolean enabled;
    private boolean cycle;

    public AlphaInfluencer() {
        this.alphas = ArrayFactory.newUnsafeArray(Float.class);
        this.interpolations = ArrayFactory.newUnsafeArray(Interpolation.class);
        this.enabled = true;
        this.startAlpha = 1;
    }

    @NotNull
    @Override
    public String getName() {
        return "Alpha influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled) return;

        particleData.alphaInterval += tpf;

        if (particleData.alphaInterval >= particleData.alphaDuration) {
            updateAlpha(particleData);
        }

        blend = particleData.alphaInterpolation.apply(particleData.alphaInterval / particleData.alphaDuration);

        final Float[] alphasArray = alphas.array();

        startAlpha = alphasArray[particleData.alphaIndex];

        if (particleData.alphaIndex == alphas.size() - 1) {
            endAlpha = alphasArray[0];
        } else {
            endAlpha = alphasArray[particleData.alphaIndex + 1];
        }

        particleData.alpha = interpolateLinear(blend, startAlpha, endAlpha);
    }

    private void updateAlpha(@NotNull final ParticleData particleData) {
        particleData.alphaIndex++;

        if (particleData.alphaIndex >= alphas.size()) {
            particleData.alphaIndex = 0;
        }

        particleData.alphaInterpolation = interpolations.get(particleData.alphaIndex);
        particleData.alphaInterval -= particleData.alphaDuration;
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {

        if (!initialized) {
            if (alphas.isEmpty()) {
                addAlpha(1f);
                addAlpha(0f);
            } else if (alphas.size() == 1) {
                setEnabled(false);
            }
            initialized = true;
        }

        if (randomStartAlpha) {
            particleData.alphaIndex = FastMath.nextRandomInt(0, alphas.size() - 1);
        } else {
            particleData.alphaIndex = 0;
        }

        particleData.alphaInterval = 0f;
        particleData.alphaDuration = (cycle) ? fixedDuration : particleData.startlife / ((float) alphas.size() - 1);
        particleData.alpha = alphas.get(particleData.alphaIndex);
        particleData.alphaInterpolation = interpolations.get(particleData.alphaIndex);
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.alpha = 0;
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
        alphas.add(alpha);
        interpolations.add(interpolation);
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
     * Returns an array containing all interpolation step values
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
     * Change an alpha for the index.
     *
     * @param alpha the new alpha.
     * @param index the index.
     */
    public void updateAlpha(@NotNull final Float alpha, final int index) {
        alphas.set(index, alpha);
    }

    /**
     * Remove last an alpha and interpolation.
     */
    public void removeLast() {
        if (alphas.isEmpty()) return;
        interpolations.fastRemove(interpolations.size() - 1);
        alphas.fastRemove(alphas.size() - 1);
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
     * Removes the alpha step value at the given index
     */
    public void removeAlpha(final int index) {
        alphas.slowRemove(index);
        interpolations.slowRemove(index);
    }

    /**
     * Removes all added alpha step values
     */
    public void removeAll() {
        alphas.clear();
        interpolations.clear();
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {

        final int[] interpolationIds = interpolations.stream()
                .mapToInt(InterpolationManager::getId).toArray();

        final double[] alphasToSave = alphas.stream().
                mapToDouble(value -> value).toArray();

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(alphasToSave, "alphas", null);
        capsule.write(interpolationIds, "interpolations", null);
        capsule.write(enabled, "enabled", true);
        capsule.write(randomStartAlpha, "randomStartAlpha", false);
        capsule.write(cycle, "cycle", false);
        capsule.write(fixedDuration, "fixedDuration", 0.125f);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {

        final InputCapsule capsule = importer.getCapsule(this);

        final double[] loadedAlphas = capsule.readDoubleArray("alphas", null);

        ArrayUtils.forEach(loadedAlphas, alphas, (element, toStore) -> toStore.add((float) element));

        final int[] loadedInterpolations = capsule.readIntArray("interpolations", null);

        ArrayUtils.forEach(loadedInterpolations, interpolations,
                (id, toStore) -> toStore.add(InterpolationManager.getInterpolation(id)));

        enabled = capsule.readBoolean("enabled", true);
        randomStartAlpha = capsule.readBoolean("randomStartAlpha", false);
        cycle = capsule.readBoolean("cycle", false);
        fixedDuration = capsule.readFloat("fixedDuration", 0.125f);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            final AlphaInfluencer clone = (AlphaInfluencer) super.clone();
            clone.alphas.addAll(alphas);
            clone.interpolations.addAll(interpolations);
            clone.enabled = enabled;
            clone.randomStartAlpha = randomStartAlpha;
            clone.cycle = cycle;
            clone.fixedDuration = fixedDuration;
            return clone;
        } catch (final CloneNotSupportedException e) {
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
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
