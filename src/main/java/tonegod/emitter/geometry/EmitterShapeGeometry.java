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
public class EmitterShapeGeometry extends Geometry {

    /**
     * Instantiates a new Emitter shape geometry.
     */
    public EmitterShapeGeometry() {
    }

    /**
     * Instantiates a new Emitter shape geometry.
     *
     * @param name the name
     */
    public EmitterShapeGeometry(@NotNull final String name) {
        super(name);
    }

    /**
     * Instantiates a new Emitter shape geometry.
     *
     * @param name the name
     * @param mesh the mesh
     */
    public EmitterShapeGeometry(@NotNull final String name, @NotNull final Mesh mesh) {
        super(name, mesh);
    }
}
