package tonegod.emitter.influencers.impl;

import static com.jme3.math.FastMath.nextRandomInt;
import com.jme3.export.*;
import com.jme3.math.ColorRGBA;
import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;

/**
 * The implementation of the {@link ParticleInfluencer} to change color of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class ColorInfluencer extends AbstractInterpolatedParticleInfluencer<BaseInterpolationData> {

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

    public ColorInfluencer(@NotNull ColorRGBA first, @NotNull ColorRGBA... additional) {
        this();
        addColor(first);
        for (ColorRGBA color : additional) {
            addColor(color);
        }
    }

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
    public @NotNull BaseInterpolationData newDataObject() {
        return new BaseInterpolationData();
    }

    @Override
    protected void updateImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull BaseInterpolationData data,
            float tpf
    ) {

        data.interval += tpf;

        if (data.index >= colors.size()) {
            data.index = 0;
        }

        if (data.interval >= data.duration) {
            updateInterpolation(data, getColors());
        }

        Interpolation interpolation = data.interpolation;
        SafeArrayList<ColorRGBA> colors = getColors();
        ColorRGBA[] array = colors.getArray();

        blend = interpolation.apply(data.interval / data.duration);
        startColor.set(array[data.index]);

        if (data.index == colors.size() - 1) {
            endColor.set(array[0]);
        } else {
            endColor.set(array[data.index + 1]);
        }

        particleData.color.interpolateLocal(startColor, endColor, blend);

        super.updateImpl(emitterNode, particleData, data, tpf);
    }

    @Override
    protected void firstInitializeImpl(@NotNull ParticleData particleData) {

        SafeArrayList<ColorRGBA> colors = getColors();

        if (colors.isEmpty()) {
            addColor(ColorRGBA.Red);
            addColor(ColorRGBA.Yellow);
        } else if (colors.size() == 1) {
            setEnabled(false);
        }

        super.firstInitializeImpl(particleData);
    }

    @Override
    protected void initializeImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull BaseInterpolationData data
    ) {

        SafeArrayList<Interpolation> interpolations = getInterpolations();

        if (isRandomStartColor()) {
            data.index = nextRandomInt(0, colors.size() - 1);
        } else {
            data.index = 0;
        }

        data.interval = 0F;
        data.duration = isCycle() ? getFixedDuration() : particleData.startLife / ((float) interpolations.size() - 1);
        data.interpolation = interpolations.get(data.index);

        particleData.color.set(colors.get(data.index));

        super.initializeImpl(emitterNode, particleData, data);
    }

    @Override
    protected void resetImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull BaseInterpolationData data
    ) {
        particleData.color.set(resetColor);
        super.resetImpl(emitterNode, particleData, data);
    }

    /**
     * Sets true to use random start color.
     *
     * @param randomStartColor true to enable random start color.
     */
    public void setRandomStartColor(boolean randomStartColor) {
        this.randomStartColor = randomStartColor;
    }

    /**
     * Returns true if random start color enabled.
     *
     * @return true if random start color is enabled.
     */
    public boolean isRandomStartColor() {
        return randomStartColor;
    }

    /**
     * Adds the new color with {@link Interpolation#LINEAR} interpolation.
     *
     * @param color the new color.
     */
    public void addColor(@NotNull ColorRGBA color) {
        addColor(color, Interpolation.LINEAR);
    }

    /**
     * Adds the new color with the interpolation.
     *
     * @param color         the color.
     * @param interpolation the interpolation.
     */
    public void addColor(@NotNull ColorRGBA color, @NotNull Interpolation interpolation) {
        addInterpolation(interpolation);
        colors.add(color.clone());
    }

    /**
     * Removes a color and interpolation by the index.
     *
     * @param index the index.
     */
    public void removeColor(int index) {
        removeInterpolation(index);
        colors.remove(index);
    }

    /**
     * Removes all colors with interpolations.
     */
    public void removeAll() {
        clearInterpolations();
        colors.clear();
    }

    /**
     * Gets all colors.
     *
     * @return the list of colors.
     */
    public @NotNull SafeArrayList<ColorRGBA> getColors() {
        return colors;
    }

    /**
     * Gets a color by the index.
     *
     * @param index the index.
     * @return the color by the index.
     */
    public @NotNull ColorRGBA getColor(int index) {
        return colors.get(index);
    }

    /**
     * Changes a color by the index to the color.
     *
     * @param color the new color.
     * @param index the index.
     */
    public void updateColor(@NotNull ColorRGBA color, int index) {
        colors.set(index, color);
    }

    /**
     * Removes a last color and interpolation.
     */
    public void removeLast() {

        SafeArrayList<ColorRGBA> colors = getColors();
        if (colors.isEmpty()) {
            return;
        }

        int index = colors.size() - 1;
        removeInterpolation(index);
        colors.remove(index);
    }

    @Override
    public void write(@NotNull JmeExporter exporter) throws IOException {
        super.write(exporter);

        OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(colors.toArray(new ColorRGBA[colors.size()]), "colors", null);
        capsule.write(randomStartColor, "randomStartColor", false);
    }

    @Override
    public void read(@NotNull JmeImporter importer) throws IOException {
        super.read(importer);

        InputCapsule capsule = importer.getCapsule(this);
        Savable[] readColors = capsule.readSavableArray("colors", null);

        if (readColors != null) {
            for (Savable color : readColors) {
                colors.add((ColorRGBA) color);
            }
        }

        randomStartColor = capsule.readBoolean("randomStartColor", false);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        ColorInfluencer clone = (ColorInfluencer) super.clone();
        clone.colors = new SafeArrayList<>(ColorRGBA.class);
        clone.colors.addAll(colors);
        clone.randomStartColor = randomStartColor;
        return clone;
    }
}
