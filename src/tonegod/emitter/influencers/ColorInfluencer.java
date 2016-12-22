package tonegod.emitter.influencers;

import static com.jme3.math.FastMath.nextRandomInt;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.ColorRGBA;

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
 * The implementation of the {@link ParticleInfluencer} for influence to color of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public final class ColorInfluencer implements ParticleInfluencer {

    /**
     * The list of interpolations.
     */
    private final UnsafeArray<Interpolation> interpolations;

    /**
     * The list of colors.
     */
    private final UnsafeArray<ColorRGBA> colors;

    private final transient ColorRGBA resetColor;

    /**
     * The start color.
     */
    private final ColorRGBA startColor;

    /**
     * The end color.
     */
    private final ColorRGBA endColor;

    /**
     * The blend value.
     */
    private float blend;

    /**
     * The fixed duration.
     */
    private float fixedDuration;

    private boolean initialized;
    private boolean enabled;
    private boolean randomStartColor;
    private boolean cycle;

    public ColorInfluencer() {
        this.colors = ArrayFactory.newUnsafeArray(ColorRGBA.class);
        this.interpolations = ArrayFactory.newUnsafeArray(Interpolation.class);
        this.resetColor = new ColorRGBA(0, 0, 0, 0);
        this.startColor = new ColorRGBA(ColorRGBA.Red);
        this.endColor = new ColorRGBA(ColorRGBA.Yellow);
        this.enabled = true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Color influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, float tpf) {
        if (!enabled) return;

        particleData.colorInterval += tpf;

        if (particleData.colorInterval >= particleData.colorDuration) {
            updateColor(particleData);
        }

        final ColorRGBA[] colorsArray = colors.array();

        blend = particleData.colorInterpolation.apply(particleData.colorInterval / particleData.colorDuration);
        startColor.set(colorsArray[particleData.colorIndex]);

        if (particleData.colorIndex == colors.size() - 1) {
            endColor.set(colorsArray[0]);
        } else {
            endColor.set(colorsArray[particleData.colorIndex + 1]);
        }

        particleData.color.interpolateLocal(startColor, endColor, blend);
    }

    private void updateColor(@NotNull final ParticleData particleData) {
        particleData.colorIndex++;

        if (particleData.colorIndex >= colors.size()) {
            particleData.colorIndex = 0;
        }

        particleData.colorInterpolation = interpolations.get(particleData.colorIndex);
        particleData.colorInterval -= particleData.colorDuration;
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {

        if (!initialized) {
            if (colors.isEmpty()) {
                addColor(ColorRGBA.Red);
                addColor(ColorRGBA.Yellow);
            } else if (colors.size() == 1) {
                setEnabled(false);
            }
            initialized = true;
        }

        if (randomStartColor) {
            particleData.colorIndex = nextRandomInt(0, colors.size() - 1);
        } else {
            particleData.colorIndex = 0;
        }

        particleData.colorInterval = 0F;
        particleData.colorDuration = (cycle) ? fixedDuration : particleData.startlife / ((float) colors.size() - 1);
        particleData.color.set(colors.get(particleData.colorIndex));
        particleData.colorInterpolation = interpolations.get(particleData.colorIndex);
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.color.set(resetColor);
        particleData.colorIndex = 0;
        particleData.colorInterval = 0;
    }

    public void setRandomStartColor(final boolean randomStartColor) {
        this.randomStartColor = randomStartColor;
    }

    public boolean isRandomStartColor() {
        return randomStartColor;
    }

    public void addColor(@NotNull final ColorRGBA color) {
        addColor(color, Interpolation.LINEAR);
    }

    public void addColor(@NotNull final ColorRGBA color, final @NotNull Interpolation interpolation) {
        colors.add(color.clone());
        interpolations.add(interpolation);
    }

    public void removeColor(final int index) {
        colors.slowRemove(index);
        interpolations.slowRemove(index);
    }

    public void removeAll() {
        this.colors.clear();
        this.interpolations.clear();
    }

    /**
     * @return the list of colors.
     */
    @NotNull
    public Array<ColorRGBA> getColors() {
        return colors;
    }

    /**
     * @param index the index.
     * @return the color for the index.
     */
    @NotNull
    public ColorRGBA getColor(final int index) {
        return colors.get(index);
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
     * Change a color for the index.
     *
     * @param color the new color.
     * @param index the index.
     */
    public void updateColor(final @NotNull ColorRGBA color, final int index) {
        colors.set(index, color);
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
     * Remove last a color and interpolation.
     */
    public void removeLast() {
        if (colors.isEmpty()) return;
        interpolations.fastRemove(interpolations.size() - 1);
        colors.fastRemove(colors.size() - 1);
    }

    /**
     * @return the list of interpolations.
     */
    @NotNull
    public Array<Interpolation> getInterpolations() {
        return interpolations;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {

        final int[] interpolationIds = interpolations.stream()
                .mapToInt(InterpolationManager::getId).toArray();

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(colors.toArray(new ColorRGBA[colors.size()]), "colors", null);
        capsule.write(interpolationIds, "interpolations", null);
        capsule.write(enabled, "enabled", true);
        capsule.write(randomStartColor, "randomStartColor", false);
        capsule.write(cycle, "cycle", false);
        capsule.write(fixedDuration, "fixedDuration", 0.125f);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {

        final InputCapsule capsule = importer.getCapsule(this);
        final Savable[] loadedColors = capsule.readSavableArray("colors", null);

        ArrayUtils.forEach(loadedColors, colors, (savable, toStore) -> toStore.add((ColorRGBA) savable));

        final int[] loadedInterpolations = capsule.readIntArray("interpolations", null);

        ArrayUtils.forEach(loadedInterpolations, interpolations,
                (id, toStore) -> toStore.add(InterpolationManager.getInterpolation(id)));

        enabled = capsule.readBoolean("enabled", true);
        randomStartColor = capsule.readBoolean("randomStartColor", false);
        cycle = capsule.readBoolean("cycle", false);
        fixedDuration = capsule.readFloat("fixedDuration", 0.125f);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            final ColorInfluencer clone = (ColorInfluencer) super.clone();
            clone.colors.addAll(colors);
            clone.interpolations.addAll(interpolations);
            clone.enabled = enabled;
            clone.randomStartColor = randomStartColor;
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
     * @param fixedDuration duration between step updates
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
     * Returns the current duration used between steps for cycled animation
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
