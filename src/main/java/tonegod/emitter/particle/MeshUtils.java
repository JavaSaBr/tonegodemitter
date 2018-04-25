/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.emitter.particle;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.FloatBuffer;

/**
 * The type Mesh utils.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class MeshUtils {

    /**
     * Gets a position buffer.
     *
     * @param mesh the mesh.
     * @return the position buffer.
     */
    public static @Nullable FloatBuffer getPositionBuffer(@NotNull Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.Position);
    }

    /**
     * Gets a index buffer.
     *
     * @param mesh the mesh.
     * @return the index buffer.
     */
    public static @Nullable IndexBuffer getIndexBuffer(@NotNull Mesh mesh) {
        return mesh.getIndexBuffer();
    }

    /**
     * Gets a tex coord buffer.
     *
     * @param mesh the mesh.
     * @return the tex coord buffer.
     */
    public static @Nullable FloatBuffer getTexCoordBuffer(@NotNull Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.TexCoord);
    }

    /**
     * Gets a normals buffer.
     *
     * @param mesh the mesh.
     * @return the normals buffer.
     */
    public static @Nullable FloatBuffer getNormalsBuffer(@NotNull Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.Normal);
    }

    /**
     * Gets a color buffer.
     *
     * @param mesh the mesh.
     * @return the color buffer.
     */
    public static @Nullable FloatBuffer getColorBuffer(@NotNull Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.Color);
    }
}
