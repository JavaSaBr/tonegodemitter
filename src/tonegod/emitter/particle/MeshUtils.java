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
 * @author t0neg0d, JavaSaBr
 */
public final class MeshUtils {

    public static FloatBuffer getPositionBuffer(@NotNull final Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.Position);
    }

    public static IndexBuffer getIndexBuffer(@NotNull final Mesh mesh) {
        return mesh.getIndexBuffer();
    }

    public static FloatBuffer getTexCoordBuffer(@NotNull final Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.TexCoord);
    }

    public static FloatBuffer getNormalsBuffer(@NotNull final Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.Normal);
    }

    public static FloatBuffer getColorBuffer(@NotNull final Mesh mesh) {
        return mesh.getFloatBuffer(VertexBuffer.Type.Color);
    }
}
