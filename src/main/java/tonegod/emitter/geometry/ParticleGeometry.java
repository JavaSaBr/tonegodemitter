package tonegod.emitter.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link Geometry} for using in the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class ParticleGeometry extends Geometry {

    /**
     * Instantiates a new Particle geometry.
     */
    public ParticleGeometry() {
    }

    /**
     * Instantiates a new Particle geometry.
     *
     * @param name the name
     */
    public ParticleGeometry(@NotNull final String name) {
        super(name);
    }

    /**
     * Instantiates a new Particle geometry.
     *
     * @param name the name
     * @param mesh the mesh
     */
    public ParticleGeometry(@NotNull final String name, @NotNull final Mesh mesh) {
        super(name, mesh);
    }
}
