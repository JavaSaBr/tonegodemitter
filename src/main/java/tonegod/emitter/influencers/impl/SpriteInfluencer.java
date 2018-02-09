package tonegod.emitter.influencers.impl;

import static tonegod.emitter.influencers.impl.AbstractInterpolatedParticleInfluencer.DATA_FACTORY;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.impl.AbstractInterpolatedParticleInfluencer.BaseInterpolationData;
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;

/**
 * The implementation of the {@link ParticleInfluencer} to animate sprites of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public class SpriteInfluencer extends AbstractParticleInfluencer {

    private static final int DATA_ID = ParticleData.reserveObjectDataId();

    private transient float targetInterval;

    /**
     * The frame sequence.
     */
    @Nullable
    private int[] frameSequence;

    /**
     * The fixed duration.
     */
    private float fixedDuration;

    /**
     * The total frames.
     */
    private int totalFrames;

    /**
     * The flag of using random images.
     */
    private boolean randomStatImage;

    /**
     * The flag of using animation.
     */
    private boolean animate;

    /**
     * The flag of using cycle image changing.
     */
    private boolean cycle;

    public SpriteInfluencer() {
        this.fixedDuration = 0f;
        this.totalFrames = -1;
        this.animate = true;
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_SPRITE;
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {

        if (!isAnimate()) {
            return;
        }

        super.update(particleData, tpf);
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        final BaseInterpolationData data = particleData.getObjectData(DATA_ID);
        data.interval += tpf;

        targetInterval = isCycle() ? (fixedDuration / 100F) : data.duration;

        if (data.interval >= targetInterval) {
            updateFrame(data, particleData);
        }

        super.updateImpl(particleData, tpf);
    }

    /**
     * Update a frame for the particle.
     *
     * @param data         influencer's data.
     * @param particleData the particle's data.
     */
    private void updateFrame(@NotNull final BaseInterpolationData data, @NotNull final ParticleData particleData) {

        final ParticleEmitterNode emitterNode = particleData.getEmitterNode();

        if (frameSequence == null) {

            particleData.spriteCol++;

            if (particleData.spriteCol == emitterNode.getSpriteColCount()) {
                particleData.spriteCol = 0;
                particleData.spriteRow++;

                if (particleData.spriteRow == emitterNode.getSpriteRowCount()) {
                    particleData.spriteRow = 0;
                }
            }

        } else {

            data.index++;

            if (data.index == frameSequence.length) {
                data.index = 0;
            }

            final int frame = frameSequence[data.index];

            particleData.spriteRow = (int) FastMath.floor(frame / emitterNode.getSpriteRowCount()) - 2;
            particleData.spriteCol = frame % emitterNode.getSpriteColCount();
        }

        data.interval -= targetInterval;
    }

    @Override
    protected void initializeImpl(@NotNull final ParticleData particleData) {
        particleData.initializeObjectData(DATA_ID, DATA_FACTORY);

        final ParticleEmitterNode emitterNode = particleData.getEmitterNode();
        final BaseInterpolationData data = particleData.getObjectData(DATA_ID);

        final int spriteRowCount = emitterNode.getSpriteRowCount();
        final int spriteColCount = emitterNode.getSpriteColCount();

        if (totalFrames == -1) {
            totalFrames = spriteColCount * spriteRowCount;
            if (totalFrames == 1) {
                setAnimate(false);
            }
        }

        if (isRandomStartImage()) {
            if (frameSequence == null) {
                data.index = FastMath.nextRandomInt(0, totalFrames - 1);
                particleData.spriteRow = (int) FastMath.floor(data.index / spriteRowCount) - 1;
                particleData.spriteCol = data.index % spriteColCount;
            } else {
                data.index = FastMath.nextRandomInt(0, frameSequence.length - 1);
                particleData.spriteRow = (int) FastMath.floor(frameSequence[data.index] / spriteRowCount) - 1;
                particleData.spriteCol = frameSequence[data.index] % spriteColCount;
            }
        } else {
            if (frameSequence != null) {
                data.index = frameSequence[0];
                particleData.spriteRow = (int) FastMath.floor(frameSequence[data.index] / spriteRowCount) - 2;
                particleData.spriteCol = frameSequence[data.index] % spriteColCount;
            } else {
                data.index = 0;
                particleData.spriteRow = 0;
                particleData.spriteCol = 0;
            }
        }

        if (!isAnimate()) {
            return;
        }

        data.interval = 0;

        if (isCycle()) {
            return;
        }

        if (frameSequence == null) {
            data.duration = particleData.startLife / (float) totalFrames;
        } else {
            data.duration = particleData.startLife / (float) frameSequence.length;
        }

        super.initializeImpl(particleData);
    }

    /**
     * Is cycle boolean.
     *
     * @return true changing is cycled.
     */
    public boolean isCycle() {
        return cycle;
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {

        final BaseInterpolationData data = particleData.getObjectData(DATA_ID);
        data.index = 0;

        particleData.spriteCol = 0;
        particleData.spriteRow = 0;

        super.reset(particleData);
    }

    /**
     * Particles will/will not use sprite animations
     *
     * @param animate boolean
     */
    public void setAnimate(final boolean animate) {
        this.animate = animate;
    }

    /**
     * Current animation state of particle
     *
     * @return Returns if particles use sprite animation
     */
    public boolean isAnimate() {
        return animate;
    }

    /**
     * Sets if particles should select a random start image from the provided sprite texture
     *
     * @param randomStartImage boolean
     */
    public void setRandomStartImage(final boolean randomStartImage) {
        this.randomStatImage = randomStartImage;
    }

    /**
     * Returns if particles currently select a random start image from the provided sprite texture
     *
     * @return the boolean
     */
    public boolean isRandomStartImage() {
        return randomStatImage;
    }

    /**
     * Sets frame sequence.
     *
     * @param frame the frame sequence.
     */
    public void setFrameSequence(final int... frame) {
        frameSequence = frame;
    }

    /**
     * Get frame sequence int [ ].
     *
     * @return the frame sequence.
     */
    public int[] getFrameSequence() {
        return frameSequence;
    }

    /**
     * Clear the frame sequence.
     */
    public void clearFrameSequence() {
        frameSequence = null;
    }

    /**
     * Animated texture should cycle and use the provided duration between frames (0 diables cycling)
     *
     * @param fixedDuration duration between frame updates.
     */
    public void setFixedDuration(float fixedDuration) {
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
     *
     * @return the fixed duration.
     */
    public float getFixedDuration() {
        return fixedDuration;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(randomStatImage, "randomStatImage", false);
        capsule.write(animate, "animate", true);
        capsule.write(fixedDuration, "fixedDuration", 0f);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        randomStatImage = capsule.readBoolean("randomStatImage", false);
        animate = capsule.readBoolean("animate", true);
        fixedDuration = capsule.readFloat("fixedDuration", 0f);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        final SpriteInfluencer clone = (SpriteInfluencer) super.clone();
        clone.setAnimate(animate);
        clone.setFixedDuration(fixedDuration);
        clone.setRandomStartImage(randomStatImage);
        clone.setFrameSequence(frameSequence);
        return clone;
    }
}
