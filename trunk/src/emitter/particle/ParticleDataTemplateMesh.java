package emitter.particle;

import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import emitter.Emitter;

/**
 *
 * @author t0neg0d
 */
public class ParticleDataTemplateMesh extends ParticleDataMesh {

    private int imagesX = 1;
    private int imagesY = 1;
    private boolean uniqueTexCoords = false;
    private Emitter emitter;
	private Vector3f left = new Vector3f(), tempLeft = new Vector3f();
	private Vector3f up = new Vector3f(), tempUp = new Vector3f();
	private Vector3f dir = new Vector3f();
	private Vector3f tempV3 = new Vector3f();
	private Quaternion rotStore = new Quaternion();
	private Quaternion tempQ = new Quaternion();
	private Node tempN = new Node();
	private int imgX, imgY;
	private float startX, startY, endX, endY;
	private Mesh template;
	private FloatBuffer	templateVerts;
	private FloatBuffer	templateCoords;
	private IndexBuffer	templateIndexes;
	private FloatBuffer	templateNormals;
	
	private FloatBuffer	finVerts;
	private FloatBuffer	finCoords;
	private ShortBuffer	finIndexes;
	private FloatBuffer	finNormals;
	
	Matrix3f mat3 = new Matrix3f();
	Matrix4f mat4 = new Matrix4f();
	
	@Override
	public void extractTemplateFromMesh(Mesh mesh) {
		template = mesh;
		templateVerts = MeshUtils.getPositionBuffer(mesh);
		templateCoords = MeshUtils.getTexCoordBuffer(mesh);
		templateIndexes = MeshUtils.getIndexBuffer(mesh);
		templateNormals = MeshUtils.getNormalsBuffer(mesh);
	}
	
	public Mesh getTemplateMesh() { return this.template; }
	
    @Override
    public void initParticleData(Emitter emitter, int numParticles) {
        setMode(Mode.Triangles);
		
        this.emitter = emitter;
		
		this.finVerts = BufferUtils.createFloatBuffer(templateVerts.capacity()*numParticles);
		try { this.finCoords = BufferUtils.createFloatBuffer(templateCoords.capacity()*numParticles); } catch (Exception e) {  }
		this.finIndexes = BufferUtils.createShortBuffer(templateIndexes.size()*numParticles);
		this.finNormals = BufferUtils.createFloatBuffer(templateNormals.capacity()*numParticles);
		
		int index = 0, index2 = 0, index3 = 0, index4 = 0;
		int indexOffset = 0;
		
		for (int i = 0; i < numParticles; i++) {
			templateVerts.rewind();
			for (int v = 0; v < templateVerts.capacity(); v += 3) {
				tempV3.set(templateVerts.get(v), templateVerts.get(v+1), templateVerts.get(v+2));
				finVerts.put(index, tempV3.getX());
				index++;
				finVerts.put(index, tempV3.getY());
				index++;
				finVerts.put(index, tempV3.getZ());
				index++;
			}
			try {
				templateCoords.rewind();
				for (int v = 0; v < templateCoords.capacity(); v++) {
					finCoords.put(index2, templateCoords.get(v));
					index2++;
				}
			} catch (Exception e) {  }
			for (int v = 0; v < templateIndexes.size(); v++) {
				finIndexes.put(index3, (short)(templateIndexes.get(v)+indexOffset));
				index3++;
			}
			indexOffset += templateVerts.capacity()/3;
			
			templateNormals.rewind();
			for (int v = 0; v < templateNormals.capacity(); v++) {
				finNormals.put(index4, templateNormals.get(v));
				index4++;
			}
		}
		
		// Help GC
	//	tempV3 = null;
	//	templateVerts = null;
	//	templateCoords = null;
	//	templateIndexes = null;
	//	templateNormals = null;
		
		// Clear & ssign buffers
		this.clearBuffer(VertexBuffer.Type.Position);
		this.setBuffer(VertexBuffer.Type.Position,	3, finVerts);
		this.clearBuffer(VertexBuffer.Type.TexCoord);
		try { this.setBuffer(VertexBuffer.Type.TexCoord,	2, finCoords); } catch (Exception e) {  }
		this.clearBuffer(VertexBuffer.Type.Index);
		this.setBuffer(VertexBuffer.Type.Index,		3, finIndexes);
		this.clearBuffer(VertexBuffer.Type.Normal);
		this.setBuffer(VertexBuffer.Type.Normal,		3, finNormals);
		this.updateBound();
    }
    
    @Override
    public void setImagesXY(int imagesX, int imagesY) {
        this.imagesX = imagesX;
        this.imagesY = imagesY;
        if (imagesX != 1 || imagesY != 1){
            uniqueTexCoords = true;
            getBuffer(VertexBuffer.Type.TexCoord).setUsage(Usage.Stream);
        }
    }
	
	public int getSpriteCols() { return this.imagesX; }
	public int getSpriteRows() { return this.imagesY; }
	
    @Override
    public void updateParticleData(ParticleData[] particles, Camera cam, Matrix3f inverseRotation) {
    //    VertexBuffer pvb = getBuffer(VertexBuffer.Type.Position);
     //   FloatBuffer positions = (FloatBuffer) pvb.getData();

     //   VertexBuffer cvb = getBuffer(VertexBuffer.Type.Color);
     //   ByteBuffer colors = (ByteBuffer) cvb.getData();

     //   VertexBuffer tvb = getBuffer(VertexBuffer.Type.TexCoord);
     //   FloatBuffer texcoords = (FloatBuffer) tvb.getData();

        // update data in vertex buffers
     //   positions.clear();
	//	positions.rewind();
     //   colors.clear();
     //   texcoords.clear();
        
        for (int i = 0; i < particles.length; i++){
            ParticleData p = particles[i];
			int offset = templateVerts.capacity()*i;
			if (p.life == 0) {
				for (int x = 0; x < templateVerts.capacity(); x++) {
					finVerts.put(offset+x,0);
				}
				continue;
			}
			for (int x = 0; x < templateVerts.capacity(); x += 3) {
				tempV3.set(templateVerts.get(x),templateVerts.get(x+1),templateVerts.get(x+2));
				
				tempV3.multLocal(p.size);
				
				rotStore = tempQ.fromAngles(p.angles.x, p.angles.y, p.angles.z);
				tempV3 = rotStore.mult(tempV3);
				
				tempV3.addLocal(p.position);
				
				finVerts.put(offset+x,tempV3.getX());
				finVerts.put(offset+x+1,tempV3.getY());
				finVerts.put(offset+x+2,tempV3.getZ());
			}
			if (p.emitter.getApplyLightingTransform()) {
				for (int v = 0; v < templateNormals.capacity(); v += 3) {
					tempV3.set(templateNormals.get(v),templateNormals.get(v+1),templateNormals.get(v+2));

					rotStore.fromAngles(p.angles.x, p.angles.y, p.angles.z);
					mat3.set(rotStore.toRotationMatrix());
					float vx = tempV3.x, vy = tempV3.y, vz = tempV3.z;
					tempV3.x = mat3.get(0,0) * vx + mat3.get(0,1) * vy + mat3.get(0,2) * vz;
					tempV3.y = mat3.get(1,0) * vx + mat3.get(1,1) * vy + mat3.get(1,2) * vz;
					tempV3.z = mat3.get(2,0) * vx + mat3.get(2,1) * vy + mat3.get(2,2) * vz;

					finNormals.put(offset+v, tempV3.getX());
					finNormals.put(offset+v+1, tempV3.getY());
					finNormals.put(offset+v+2, tempV3.getZ());
				}
			}
        }
		
		this.clearBuffer(VertexBuffer.Type.Position);
		this.setBuffer(VertexBuffer.Type.Position, 3, finVerts);
		this.setBuffer(VertexBuffer.Type.Normal, 3, finNormals);
		
		updateBound();
    }
}