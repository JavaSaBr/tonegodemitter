/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.emitter.particle;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;

/**
 * The type Mesh utils.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class MeshUtils {

    /**
     * Gets position buffer.
     *
     * @param mesh the mesh
     * @return the position buffer
     */
    public static FloatBuffer getPositionBuffer(@NotNull final Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.Position);
    }

    /**
     * Gets index buffer.
     *
     * @param mesh the mesh
     * @return the index buffer
     */
    public static IndexBuffer getIndexBuffer(@NotNull final Mesh mesh) {
        return mesh.getIndexBuffer();
    }

    /**
     * Gets tex coord buffer.
     *
     * @param mesh the mesh
     * @return the tex coord buffer
     */
    public static FloatBuffer getTexCoordBuffer(@NotNull final Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.TexCoord);
    }

    /**
     * Gets normals buffer.
     *
     * @param mesh the mesh
     * @return the normals buffer
     */
    public static FloatBuffer getNormalsBuffer(@NotNull final Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.Normal);
    }

    /**
     * Gets color buffer.
     *
     * @param mesh the mesh
     * @return the color buffer
     */
    public static FloatBuffer getColorBuffer(@NotNull final Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.Color);
    }
}
