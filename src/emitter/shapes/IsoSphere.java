/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emitter.shapes;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author t0neg0d
 */
public class IsoSphere extends Mesh {
	private FloatBuffer verts = BufferUtils.createFloatBuffer(36);
	private ShortBuffer indexes = BufferUtils.createShortBuffer(60);
	private FloatBuffer normals = BufferUtils.createFloatBuffer(9);
	private Vector3f tempV3 = new Vector3f();
	
	public IsoSphere(float diameter, int subdivisions) {
		float t = (1f + FastMath.sqrt(diameter)) / 2f;
		float radius = diameter/2f;
		
		verts.put(-radius);
		verts.put(t);
		verts.put(0);
		verts.put(radius);
		verts.put(t);
		verts.put(0);
		verts.put(-radius);
		verts.put(-t);
		verts.put(0);
		verts.put(radius);
		verts.put(-t);
		verts.put(0);
		
		verts.put(0);
		verts.put(-radius);
		verts.put(t);
		verts.put(0);
		verts.put(radius);
		verts.put(t);
		verts.put(0);
		verts.put(-radius);
		verts.put(-t);
		verts.put(0);
		verts.put(radius);
		verts.put(-t);
		
		verts.put(t);
		verts.put(0);
		verts.put(-radius);
		verts.put(t);
		verts.put(0);
		verts.put(radius);
		verts.put(-t);
		verts.put(0);
		verts.put(-radius);
		verts.put(-t);
		verts.put(0);
		verts.put(radius);
		
		
		indexes.put((short)0);
		indexes.put((short)11);
		indexes.put((short)5);
		
		indexes.put((short)0);
		indexes.put((short)5);
		indexes.put((short)7);
		
		indexes.put((short)0);
		indexes.put((short)7);
		indexes.put((short)10);
		
		indexes.put((short)0);
		indexes.put((short)10);
		indexes.put((short)2);
		
		indexes.put((short)0);
		indexes.put((short)2);
		indexes.put((short)11);
		
		indexes.put((short)11);
		indexes.put((short)2);
		indexes.put((short)4);
		
		indexes.put((short)11);
		indexes.put((short)4);
		indexes.put((short)9);
		
		indexes.put((short)11);
		indexes.put((short)9);
		indexes.put((short)5);
		
		indexes.put((short)5);
		indexes.put((short)9);
		indexes.put((short)1);
		
		indexes.put((short)5);
		indexes.put((short)1);
		indexes.put((short)7);
		
		indexes.put((short)9);
		indexes.put((short)4);
		indexes.put((short)3);
		
		indexes.put((short)9);
		indexes.put((short)3);
		indexes.put((short)1);
		
		indexes.put((short)6);
		indexes.put((short)2);
		indexes.put((short)4);
		
		indexes.put((short)6);
		indexes.put((short)4);
		indexes.put((short)3);
		
		indexes.put((short)6);
		indexes.put((short)3);
		indexes.put((short)8);
		
		indexes.put((short)6);
		indexes.put((short)2);
		indexes.put((short)10);
		
		indexes.put((short)8);
		indexes.put((short)3);
		indexes.put((short)1);
		
		indexes.put((short)8);
		indexes.put((short)1);
		indexes.put((short)7);
		
		indexes.put((short)8);
		indexes.put((short)7);
		indexes.put((short)10);
		
		indexes.put((short)8);
		indexes.put((short)10);
		indexes.put((short)6);

		this.clearBuffer(VertexBuffer.Type.Position);
		this.setBuffer(VertexBuffer.Type.Position, 3, verts);
		this.clearBuffer(VertexBuffer.Type.Index);
		this.setBuffer(VertexBuffer.Type.Index, 3, indexes);
	//	this.clearBuffer(VertexBuffer.Type.Normal);
	//	this.setBuffer(VertexBuffer.Type.Normal, 3, normals);
		
		updateBound();
		
	}
	/*
//	List<Vector3f> vertices;
//	List<Vector3f> normals;

//	List<Integer> indices;
//	Map<Vector3f,Integer> newVectices;
	
	public Vector3f GetNewVertex(Vector3f i1, Vector3f i2) {
		// We have to test both directions since the edge
		// could be reversed in another triangle
        
		int t1 = (i1 << 16) | i2;
        int t2 = (i2 << 16) | i1;
        if (newVectices.containsKey(t2))
            return newVectices[t2];
        if (newVectices.ContainsKey(t1))
            return newVectices[t1];
        // generate vertex:
        int newIndex = vertices.Count;
        newVectices.Add(t1,newIndex);
 
        // calculate new vertex
        vertices.Add((vertices[i1] + vertices[i2]) * 0.5f);
        normals.Add((normals[i1] + normals[i2]).normalized);
        // [... all other vertex data arrays]
 
        return newIndex;
    }
 
    public void Subdivide() {
		for (int i = 0; i < getTriangleCount(); i++) {
			Triangle tri = new Triangle();
			getTriangle(i, tri);
            Vector3f i1 = tri.get1();
            Vector3f i2 = tri.get2();
            Vector3f i3 = tri.get3();
			
            Vector3f a = GetNewVertex(i1, i2);
            Vector3f b = GetNewVertex(i2, i3);
            Vector3f c = GetNewVertex(i3, i1);
            indices.Add(i1);   indices.Add(a);   indices.Add(c);
            indices.Add(i2);   indices.Add(b);   indices.Add(a);
            indices.Add(i3);   indices.Add(c);   indices.Add(b);
            indices.Add(a );   indices.Add(b);   indices.Add(c); // center triangle
        }
        mesh.vertices = vertices.ToArray();
        mesh.normals = normals.ToArray();
        // [... all other vertex data arrays]
        mesh.triangles = indices.ToArray();
 
        // since this is a static function and it uses static variables
        // we should erase the arrays to free them:
        newVectices = null;
        vertices = null;
        normals = null;
        // [... all other vertex data arrays]
 
        indices = null;
    }
	*/
}
