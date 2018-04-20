package tonegod.emitter;

import static tonegod.emitter.material.ParticlesMaterial.PROP_SOFT_PARTICLES;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.node.ParticleNode;

/**
 * The implementation of soft particle emitter node.
 *
 * @author JavaSaBr
 */
public class SoftParticleEmitterNode extends ParticleEmitterNode {

    public SoftParticleEmitterNode() {
    }

    public SoftParticleEmitterNode(@NotNull AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected void initParticleNode(@NotNull ParticleNode particleNode) {
        super.initParticleNode(particleNode);
        particleNode.setQueueBucket(Bucket.Translucent);
    }

    @Override
    protected void initParticleMaterial(@NotNull Material material) {
        super.initParticleMaterial(material);

        material.setBoolean(PROP_SOFT_PARTICLES, true);

        RenderState renderState = material.getAdditionalRenderState();
        renderState.setDepthTest(true);
    }
}
