package tonegod.emitter.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link Geometry} for using in the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class ParticleGeometry extends Geometry {

    public ParticleGeometry() {
    }

    public ParticleGeometry(final String name) {
        super(name);
    }

    public ParticleGeometry(final String name, final Mesh mesh) {
        super(name, mesh);
    }
}
