package tonegod.emitter.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link Geometry} for using in the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterGeometry extends Geometry {

    public ParticleEmitterGeometry() {
    }

    public ParticleEmitterGeometry(final String name) {
        super(name);
    }

    public ParticleEmitterGeometry(final String name, final Mesh mesh) {
        super(name, mesh);
    }
}
