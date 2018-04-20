package tonegod.emitter.influencers.impl;

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
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;

/**
 * The implementation of the {@link ParticleInfluencer} to animate sprites of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public class SpriteInfluencer extends AbstractInterpolatedParticleInfluencer<BaseInterpolationData> {

    /**
     * The frame sequence.
     */
    @Nullable
    private int[] frameSequence;

    /**
     * The total frames.
     */
    private int totalFrames;

    /**
     * The flag of using random images.
     */
    private boolean randomStatImage;

    /**
     * The flag of using sprite animation.
     */
    private boolean animate;

    public SpriteInfluencer() {
        this.totalFrames = -1;
        this.animate = true;
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_SPRITE;
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

        if (!isAnimate()) {
            super.updateImpl(emitterNode, particleData, data, tpf);
            return;
        }

        data.interval += tpf;

        float targetInterval = isCycle() ? (getFixedDuration() / 100F) : data.duration;

        if (data.interval >= targetInterval) {
            updateFrame(emitterNode, data, particleData, targetInterval);
        }

        super.updateImpl(emitterNode, particleData, data, tpf);
    }

    /**
     * Updates a frame for the particle.
     *
     * @param data           influencer's data.
     * @param particleData   the particle's data.
     * @param targetInterval the target interval.
     */
    private void updateFrame(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull BaseInterpolationData data,
            @NotNull ParticleData particleData,
            float targetInterval
    ) {

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

            int frame = frameSequence[data.index];

            particleData.spriteRow = (int) FastMath.floor(frame / emitterNode.getSpriteRowCount()) - 2;
            particleData.spriteCol = frame % emitterNode.getSpriteColCount();
        }

        data.interval -= targetInterval;
    }

    @Override
    protected void initializeImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull BaseInterpolationData data
    ) {

        int spriteRowCount = emitterNode.getSpriteRowCount();
        int spriteColCount = emitterNode.getSpriteColCount();

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

        super.initializeImpl(emitterNode, particleData, data);
    }

    @Override
    protected void resetImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull BaseInterpolationData data
    ) {

        particleData.spriteCol = 0;
        particleData.spriteRow = 0;

        super.resetImpl(emitterNode, particleData, data);
    }

    /**
     * Sets true if need to use sprite animation.
     *
     * @param animate true if need to use sprite animation.
     */
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    /**
     * Returns true if sprite animation is used.
     *
     * @return true if sprite animation is used.
     */
    public boolean isAnimate() {
        return animate;
    }

    /**
     * Sets if particles should select a random start image from the provided sprite texture.
     *
     * @param randomStartImage true if need to use random start image.
     */
    public void setRandomStartImage(boolean randomStartImage) {
        this.randomStatImage = randomStartImage;
    }

    /**
     * Returns true if particles uses random start images.
     *
     * @return true if particles uses random start images.
     */
    public boolean isRandomStartImage() {
        return randomStatImage;
    }

    /**
     * Sets the frame sequence.
     *
     * @param frames the frame sequence.
     */
    public void setFrameSequence(int[] frames) {
        this.frameSequence = frames;
    }

    /**
     * Sets frame sequence.
     *
     * @param frame      the first frame.
     * @param additional the additional frames.
     */
    public void setFrameSequence(int frame, int... additional) {

        frameSequence = new int[1 + additional.length];
        frameSequence[0] = frame;
        for (int i = 0, length = additional.length; i < length; i++) {
            frameSequence[i + 1] = additional[i];
        }
    }

    /**
     * Gets the frame sequence.
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

    @Override
    public void write(@NotNull JmeExporter exporter) throws IOException {
        super.write(exporter);

        OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(randomStatImage, "randomStatImage", false);
        capsule.write(animate, "animate", true);
    }

    @Override
    public void read(@NotNull JmeImporter importer) throws IOException {
        super.read(importer);

        InputCapsule capsule = importer.getCapsule(this);
        randomStatImage = capsule.readBoolean("randomStatImage", false);
        animate = capsule.readBoolean("animate", true);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        SpriteInfluencer clone = (SpriteInfluencer) super.clone();
        clone.setAnimate(animate);
        clone.setRandomStartImage(randomStatImage);
        clone.setFrameSequence(frameSequence);
        return clone;
    }
}
