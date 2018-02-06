package tonegod.emitter.influencers.impl;

import static com.jme3.math.FastMath.nextRandomInt;
import com.jme3.export.*;
import com.jme3.math.ColorRGBA;
import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.Messages;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;
import tonegod.emitter.particle.ParticleData.DataFactory;

import java.io.IOException;

/**
 * The implementation of the {@link ParticleInfluencer} to change color of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class ColorInfluencer extends AbstractInterpolatedParticleInfluencer {

    @NotNull
    private static final String DATA_KEY = "Default.ColorInfluencer";

    @NotNull
    private static final DataFactory<ColorInfluencerData> DATA_FACTORY = new DataFactory<ColorInfluencerData>() {

        @Override
        public @NotNull ColorInfluencer.ColorInfluencerData create(@NotNull final String name) {
            return new ColorInfluencerData();
        }
    };

    private static class ColorInfluencerData {

        /**
         * The color index.
         */
        int index;

        /**
         * The color interval.
         */
        float interval;

        /**
         * The duration.
         */
        float duration;

        /**
         * The color interpolation.
         */
        @NotNull
        Interpolation interpolation;

        private ColorInfluencerData() {
            this.duration = 1f;
            this.interpolation = Interpolation.LINEAR;
        }
    }

    /**
     * The list of colors.
     */
    @NotNull
    private SafeArrayList<ColorRGBA> colors;

    /**
     * The reset color.
     */
    @NotNull
    private transient final ColorRGBA resetColor;

    /**
     * The start color.
     */
    @NotNull
    private final ColorRGBA startColor;

    /**
     * The end color.
     */
    @NotNull
    private final ColorRGBA endColor;

    /**
     * The flag of using random start color.
     */
    private boolean randomStartColor;

    public ColorInfluencer() {
        this.colors = new SafeArrayList<>(ColorRGBA.class);
        this.resetColor = new ColorRGBA(0, 0, 0, 0);
        this.startColor = new ColorRGBA(ColorRGBA.Red);
        this.endColor = new ColorRGBA(ColorRGBA.Yellow);
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_COLOR;
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        final ColorInfluencerData data = particleData.getData(DATA_KEY, DATA_FACTORY);
        data.interval += tpf;

        if (data.index >= colors.size()) {
            data.index = 0;
        }

        if (data.interval >= data.duration) {
            updateColor(data);
        }

        final Interpolation interpolation = data.interpolation;
        final SafeArrayList<ColorRGBA> colors = getColors();
        final ColorRGBA[] array = colors.getArray();

        blend = interpolation.apply(data.interval / data.duration);
        startColor.set(array[data.index]);

        if (data.index == colors.size() - 1) {
            endColor.set(array[0]);
        } else {
            endColor.set(array[data.index + 1]);
        }

        particleData.color.interpolateLocal(startColor, endColor, blend);

        super.updateImpl(particleData, tpf);
    }

    /**
     * Update the color.
     *
     * @param data the particle's data.
     */
    private void updateColor(@NotNull final ColorInfluencerData data) {
        data.index++;

        if (data.index >= colors.size()) {
            data.index = 0;
        }

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
        data.interpolation = interpolations.get(data.index);
        data.interval -= data.duration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull final ParticleData particleData) {

        final SafeArrayList<ColorRGBA> colors = getColors();

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

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
        final ColorInfluencerData data = particleData.getData(DATA_KEY, DATA_FACTORY);

        if (isRandomStartColor()) {
            data.index = nextRandomInt(0, colors.size() - 1);
        } else {
            data.index = 0;
        }

        data.interval = 0F;
        data.duration = isCycle() ? getFixedDuration() : particleData.startlife / ((float) interpolations.size() - 1);
        data.interpolation = interpolations.get(data.index);

        particleData.color.set(colors.get(data.index));

        super.initializeImpl(particleData);
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.color.set(resetColor);

        final ColorInfluencerData data = particleData.getData(DATA_KEY, DATA_FACTORY);
        data.index = 0;
        data.interval = 0;

        super.reset(particleData);
    }

    /**
     * Set true to use random start color.
     *
     * @param randomStartColor true to enable random start color.
     */
    public void setRandomStartColor(final boolean randomStartColor) {
        this.randomStartColor = randomStartColor;
    }

    /**
     * Return true if random start color enabled.
     *
     * @return true if random start color is enabled.
     */
    public boolean isRandomStartColor() {
        return randomStartColor;
    }

    /**
     * Add the new color with {@link Interpolation#LINEAR} interpolation.
     *
     * @param color the new color.
     */
    public void addColor(@NotNull final ColorRGBA color) {
        addColor(color, Interpolation.LINEAR);
    }

    /**
     * Add the new color with the interpolation.
     *
     * @param color         the color.
     * @param interpolation the interpolation.
     */
    public void addColor(@NotNull final ColorRGBA color, final @NotNull Interpolation interpolation) {
        addInterpolation(interpolation);
        colors.add(color.clone());
    }

    /**
     * Remove a color and interpolation by the index.
     *
     * @param index the index.
     */
    public void removeColor(final int index) {
        removeInterpolation(index);
        colors.remove(index);
    }

    /**
     * Remove all colors with interpolations.
     */
    public void removeAll() {
        clearInterpolations();
        colors.clear();
    }

    /**
     * Get all colors.
     *
     * @return the list of colors.
     */
    public @NotNull SafeArrayList<ColorRGBA> getColors() {
        return colors;
    }

    /**
     * Get a color by the index.
     *
     * @param index the index.
     * @return the color by the index.
     */
    public @NotNull ColorRGBA getColor(final int index) {
        return colors.get(index);
    }

    /**
     * Change a color by the index to the color.
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

        final SafeArrayList<ColorRGBA> colors = getColors();
        if (colors.isEmpty()) {
            return;
        }

        final int index = colors.size() - 1;
        removeInterpolation(index);
        colors.remove(index);
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
        final Savable[] readColors = capsule.readSavableArray("colors", null);

        if (readColors != null) {
            for (final Savable color : readColors) {
                colors.add((ColorRGBA) color);
            }
        }

        randomStartColor = capsule.readBoolean("randomStartColor", false);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        final ColorInfluencer clone = (ColorInfluencer) super.clone();
        clone.colors = new SafeArrayList<>(ColorRGBA.class);
        clone.colors.addAll(colors);
        clone.randomStartColor = randomStartColor;
        return clone;
    }
}
