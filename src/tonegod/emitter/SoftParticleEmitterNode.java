package tonegod.emitter;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.node.ParticleNode;

/**
 * The implementation of soft particle emitter node.
 *
 * @author JavaSaBr
 */
public class SoftParticleEmitterNode extends ParticleEmitterNode {

    public SoftParticleEmitterNode(@NotNull final AssetManager assetManager) {
        super(assetManager);
    }

    public SoftParticleEmitterNode() {
    }

    @Override
    protected void initParticleNode(@NotNull final ParticleNode particleNode) {
        super.initParticleNode(particleNode);
        particleNode.setQueueBucket(RenderQueue.Bucket.Translucent);
    }

    @Override
    protected void initParticleMaterial(@NotNull final Material material) {
        super.initParticleMaterial(material);

        material.setBoolean("SoftParticles", true);

        final RenderState renderState = material.getAdditionalRenderState();
        renderState.setDepthTest(true);
    }
}
