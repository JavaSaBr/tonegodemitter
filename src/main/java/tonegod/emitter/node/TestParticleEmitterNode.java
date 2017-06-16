package tonegod.emitter.node;

import com.jme3.scene.Node;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link Node} for using in the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class TestParticleEmitterNode extends Node {

    /**
     * Instantiates a new Test particle emitter node.
     */
    public TestParticleEmitterNode() {
    }

    /**
     * Instantiates a new Test particle emitter node.
     *
     * @param name the name
     */
    public TestParticleEmitterNode(@NotNull final String name) {
        super(name);
    }
}
