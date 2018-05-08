package tonegod.emitter.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.clone.CloneFunction;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.IdentityCloneFunction;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link Geometry} for using in the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class ParticleGeometry extends Geometry {

    public ParticleGeometry() {
    }

    public ParticleGeometry(@NotNull String name) {
        super(name);
    }

    public ParticleGeometry(@NotNull String name, @NotNull Mesh mesh) {
        super(name, mesh);
    }

    @Override
    public void cloneFields(@NotNull Cloner cloner, @NotNull Object original) {

        CloneFunction<Mesh> meshFunction = cloner.getCloneFunction(Mesh.class);
        try {

            cloner.setCloneFunction(Mesh.class, null);

            super.cloneFields(cloner, original);

            boolean shallowClone = (meshFunction instanceof IdentityCloneFunction);

            // See if we clone the mesh using the special animation
            // semi-deep cloning
            if (shallowClone && mesh != null && mesh.getBuffer(VertexBuffer.Type.BindPosePosition) != null) {
                // Then we need to clone the mesh a little deeper
                this.mesh = mesh.cloneForAnim();
            } else {
                // Do whatever the cloner wants to do about it
                this.mesh = cloner.clone(mesh);
            }

        } finally {
            cloner.setCloneFunction(Mesh.class, meshFunction);
        }
    }
}
