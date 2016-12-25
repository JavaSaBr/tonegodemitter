package tonegod.emitter.influencers.impl;

import static com.jme3.math.FastMath.nextRandomInt;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;

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
 * The implementation of the {@link ParticleInfluencer} for influence to color of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public final class ColorInfluencer extends AbstractInterpolatedParticleInfluencer {

    /**
     * The list of colors.
     */
    private final UnsafeArray<ColorRGBA> colors;

    /**
     * The reset color.
     */
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
     * The flag of using random start color.
     */
    private boolean randomStartColor;

    public ColorInfluencer() {
        this.colors = ArrayFactory.newUnsafeArray(ColorRGBA.class);
        this.resetColor = new ColorRGBA(0, 0, 0, 0);
        this.startColor = new ColorRGBA(ColorRGBA.Red);
        this.endColor = new ColorRGBA(ColorRGBA.Yellow);
    }

    @NotNull
    @Override
    public String getName() {
        return "Color influencer";
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {
        particleData.colorInterval += tpf;

        if (particleData.colorInterval >= particleData.colorDuration) {
            updateColor(particleData);
        }

        final Array<ColorRGBA> colors = getColors();
        final ColorRGBA[] array = colors.array();

        blend = particleData.colorInterpolation.apply(particleData.colorInterval / particleData.colorDuration);
        startColor.set(array[particleData.colorIndex]);

        if (particleData.colorIndex == colors.size() - 1) {
            endColor.set(array[0]);
        } else {
            endColor.set(array[particleData.colorIndex + 1]);
        }

        particleData.color.interpolateLocal(startColor, endColor, blend);

        super.updateImpl(particleData, tpf);
    }

    /**
     * Update a color for the particle data.
     *
     * @param particleData the particle data.
     */
    private void updateColor(@NotNull final ParticleData particleData) {
        particleData.colorIndex++;

        if (particleData.colorIndex >= colors.size()) {
            particleData.colorIndex = 0;
        }

        final Array<Interpolation> interpolations = getInterpolations();
        particleData.colorInterpolation = interpolations.get(particleData.colorIndex);
        particleData.colorInterval -= particleData.colorDuration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull final ParticleData particleData) {

        final Array<ColorRGBA> colors = getColors();

        if (colors.isEmpty()) {
            addColor(ColorRGBA.Red);
            addColor(ColorRGBA.Yellow);
        } else if (colors.size() == 1) {
            setEnabled(false);
        }

        super.firstInitializeImpl(particleData);
    }

    @Override
    protected void initializeImpl(@NotNull final ParticleData particleData) {

        final Array<Interpolation> interpolations = getInterpolations();

        if (isRandomStartColor()) {
            particleData.colorIndex = nextRandomInt(0, colors.size() - 1);
        } else {
            particleData.colorIndex = 0;
        }

        particleData.colorInterval = 0F;
        particleData.colorDuration = isCycle() ? getFixedDuration() : particleData.startlife / ((float) interpolations.size() - 1);
        particleData.color.set(colors.get(particleData.colorIndex));
        particleData.colorInterpolation = interpolations.get(particleData.colorIndex);

        super.initializeImpl(particleData);
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.color.set(resetColor);
        particleData.colorIndex = 0;
        particleData.colorInterval = 0;
        super.reset(particleData);
    }

    /**
     * @param randomStartColor true for enabling random color.
     */
    public void setRandomStartColor(final boolean randomStartColor) {
        this.randomStartColor = randomStartColor;
    }

    /**
     * @return true if random start color is enabled.
     */
    public boolean isRandomStartColor() {
        return randomStartColor;
    }

    /**
     * Add a new color with Linear {@link Interpolation}.
     *
     * @param color the new color.
     */
    public void addColor(@NotNull final ColorRGBA color) {
        addColor(color, Interpolation.LINEAR);
    }

    /**
     * Add new color.
     *
     * @param color         the color.
     * @param interpolation the interpolation.
     */
    public void addColor(@NotNull final ColorRGBA color, final @NotNull Interpolation interpolation) {
        addInterpolation(interpolation);
        colors.add(color.clone());
    }

    /**
     * Remove a color and interpolation for the index.
     *
     * @param index the index.
     */
    public void removeColor(final int index) {
        removeInterpolation(index);
        colors.slowRemove(index);
    }

    /**
     * Remove all colors with interpolations.
     */
    public void removeAll() {
        clearInterpolations();
        colors.clear();
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
     * Change a color for the index.
     *
     * @param color the new color.
     * @param index the index.
     */
    public void updateColor(final @NotNull ColorRGBA color, final int index) {
        colors.set(index, color);
    }

    /**
     * Remove a last color and interpolation.
     */
    public void removeLast() {

        final Array<ColorRGBA> colors = getColors();
        if (this.colors.isEmpty()) return;

        final int index = colors.size() - 1;
        removeInterpolation(index);
        colors.fastRemove(index);
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(colors.toArray(new ColorRGBA[colors.size()]), "colors", null);
        capsule.write(randomStartColor, "randomStartColor", false);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);

        ArrayUtils.forEach(capsule.readSavableArray("colors", null), colors,
                (savable, toStore) -> toStore.add((ColorRGBA) savable));

        randomStartColor = capsule.readBoolean("randomStartColor", false);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final ColorInfluencer clone = (ColorInfluencer) super.clone();
        clone.colors.addAll(colors);
        clone.randomStartColor = randomStartColor;
        return clone;
    }
}
