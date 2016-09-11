package tonegod.emitter.node;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.scene.Node;

import java.io.IOException;

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
    private Emitter emitter;

    public EmitterNode() {
    }

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

    @Override
    public void read(final JmeImporter importer) throws IOException {
        super.read(importer);
        final InputCapsule capsule = importer.getCapsule(this);
        emitter = (Emitter) capsule.readSavable("emitter", null);
    }

    @Override
    public void write(final JmeExporter exporter) throws IOException {
        super.write(exporter);
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(emitter, "emitter", null);
    }
}
