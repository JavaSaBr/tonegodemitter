package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;

/**
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class SpriteInfluencer extends AbstractParticleInfluencer {

    private transient float targetInterval;

    /**
     * The frame sequence.
     */
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
     * The flag of unsing animation..
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

    @NotNull
    @Override
    public String getName() {
        return "Sprite influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!isAnimate()) return;
        super.update(particleData, tpf);
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        particleData.spriteInterval += tpf;

        targetInterval = (cycle) ? fixedDuration : particleData.spriteDuration;

        if (particleData.spriteInterval >= targetInterval) {
            updateFrame(particleData);
        }

        super.updateImpl(particleData, tpf);
    }

    /**
     * Update a frame for the particle data.
     *
     * @param particleData the particle data.
     */
    private void updateFrame(@NotNull final ParticleData particleData) {
        if (frameSequence == null) {

            particleData.spriteCol++;

            if (particleData.spriteCol == particleData.emitterNode.getSpriteColCount()) {
                particleData.spriteCol = 0;
                particleData.spriteRow++;

                if (particleData.spriteRow == particleData.emitterNode.getSpriteRowCount()) {
                    particleData.spriteRow = 0;
                }
            }

        } else {

            particleData.spriteIndex++;

            if (particleData.spriteIndex == frameSequence.length) {
                particleData.spriteIndex = 0;
            }

            particleData.spriteRow = (int) FastMath.floor(frameSequence[particleData.spriteIndex] / particleData.emitterNode.getSpriteRowCount()) - 2;
            particleData.spriteCol = (int) frameSequence[particleData.spriteIndex] % particleData.emitterNode.getSpriteColCount();
        }

        particleData.spriteInterval -= targetInterval;
    }

    @Override
    protected void initializeImpl(@NotNull final ParticleData particleData) {

        if (totalFrames == -1) {
            totalFrames = particleData.emitterNode.getSpriteColCount() * particleData.emitterNode.getSpriteRowCount();
            if (totalFrames == 1) setAnimate(false);
        }

        if (isRandomStartImage()) {
            if (frameSequence == null) {
                particleData.spriteIndex = FastMath.nextRandomInt(0, totalFrames - 1);
                particleData.spriteRow = (int) FastMath.floor(particleData.spriteIndex / particleData.emitterNode.getSpriteRowCount()) - 1;
                particleData.spriteCol = (int) particleData.spriteIndex % particleData.emitterNode.getSpriteColCount();
            } else {
                particleData.spriteIndex = FastMath.nextRandomInt(0, frameSequence.length - 1);
                particleData.spriteRow = (int) FastMath.floor(frameSequence[particleData.spriteIndex] / particleData.emitterNode.getSpriteRowCount()) - 1;
                particleData.spriteCol = (int) frameSequence[particleData.spriteIndex] % particleData.emitterNode.getSpriteColCount();
            }
        } else {
            if (frameSequence != null) {
                particleData.spriteIndex = frameSequence[0];
                particleData.spriteRow = (int) FastMath.floor(frameSequence[particleData.spriteIndex] / particleData.emitterNode.getSpriteRowCount()) - 2;
                particleData.spriteCol = (int) frameSequence[particleData.spriteIndex] % particleData.emitterNode.getSpriteColCount();
            } else {
                particleData.spriteIndex = 0;
                particleData.spriteRow = 0;
                particleData.spriteCol = 0;
            }
        }

        if (!isAnimate()) return;

        particleData.spriteInterval = 0;

        if (isCycle()) return;

        if (frameSequence == null) {
            particleData.spriteDuration = particleData.startlife / (float) totalFrames;
        } else {
            particleData.spriteDuration = particleData.startlife / (float) frameSequence.length;
        }

        super.initializeImpl(particleData);
    }

    /**
     * @return true changing is cycled.
     */
    public boolean isCycle() {
        return cycle;
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.spriteIndex = 0;
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
     */
    public boolean isRandomStartImage() {
        return randomStatImage;
    }

    /**
     * @param frame the frame sequence.
     */
    public void setFrameSequence(final int... frame) {
        frameSequence = frame;
    }

    /**
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
     * Animated texture should cycle and use the provided duration between frames (0 diables
     * cycling)
     *
     * @param fixedDuration duration between frame updates
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

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final SpriteInfluencer clone = (SpriteInfluencer) super.clone();
        clone.setAnimate(animate);
        clone.setFixedDuration(fixedDuration);
        clone.setRandomStartImage(randomStatImage);
        clone.setFrameSequence(frameSequence);
        return clone;
    }
}
