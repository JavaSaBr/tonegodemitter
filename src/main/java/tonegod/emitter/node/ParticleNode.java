package tonegod.emitter.node;

import com.jme3.scene.Node;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link Node} for using in the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class ParticleNode extends Node {

    /**
     * Instantiates a new Particle node.
     */
    public ParticleNode() {
    }

    /**
     * Instantiates a new Particle node.
     *
     * @param name the name
     */
    public ParticleNode(@NotNull final String name) {
        super(name);
    }
}
