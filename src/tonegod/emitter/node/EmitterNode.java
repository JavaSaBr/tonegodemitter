package tonegod.emitter.node;

import com.jme3.scene.Node;

import tonegod.emitter.Emitter;

/**
 * The implementation of the {@link Node} for using with emitter.
 *
 * @author JavaSaBr.
 */
public class EmitterNode extends Node {

    /**
     * The emitter which use this node.
     */
    private final Emitter emitter;

    public EmitterNode(final Emitter emitter) {
        this.emitter = emitter;
    }

    public EmitterNode(final String name, final Emitter emitter) {
        super(name);
        this.emitter = emitter;
    }

    /**
     * @return the emitter which use this node.
     */
    public Emitter getEmitter() {
        return emitter;
    }
}
