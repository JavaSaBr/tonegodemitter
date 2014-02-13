package emitter;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.SafeArrayList;
import emitter.EmitterMesh.DirectionType;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import emitter.influencers.ColorInfluencer;
import emitter.influencers.GravityInfluencer;
import emitter.influencers.ImpulseInfluencer;
import emitter.influencers.ParticleInfluencer;
import emitter.influencers.DestinationInfluencer;
import emitter.influencers.PhysicsInfluencer;
import emitter.influencers.RotationInfluencer;
import emitter.influencers.SizeInfluencer;
import emitter.influencers.SpriteInfluencer;
import emitter.influencers.RadialVelocityInfluencer;
import emitter.particle.ParticleData;
import emitter.particle.ParticleDataMesh;
import emitter.particle.ParticleDataPointMesh;
import emitter.particle.ParticleDataTriMesh;
import emitter.shapes.TriangleEmitterShape;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author t0neg0d
 */
public class Emitter implements Control, Cloneable {
	public static enum BillboardMode {
		/**
		 * Facing direction follows the velocity as it changes
		 */
		Velocity,
		/**
		 * Facing direction follows the velocity as it changes, Y of particle always faces Z of velocity
		 */
		Velocity_Z_Up,
		/**
		 * Facing direction follows the velocity as it changes, Y of particle always faces Z of velocity, Up of the particle always faces X
		 */
		Velocity_Z_Up_Y_Left,
		/**
		 * Facing direction remains constant to the face of the particle emitter shape that the particle was emitted from
		 */
		Normal,
		/**
		 * Facing direction remains constant for X, Z axis' to the face of the particle emitter shape that the particle was emitted from. Y axis maps to UNIT_Y
		 */
		Normal_Y_Up,
		/**
		 * ParticleData always faces camera
		 */
		Camera,
		/**
		 * ParticleData always faces X axis
		 */
		UNIT_X,
		/**
		 * ParticleData always faces Y axis
		 */
		UNIT_Y,
		/**
		 * ParticleData always faces Z axis
		 */
		UNIT_Z
	}
	public static enum ForcedStretchAxis {
		X, Y, Z
	}
	public static enum ParticleEmissionPoint {
		Particle_Center,
		Particle_Edge_Top,
		Particle_Edge_Bottom
	}
	
	private Spatial spatial;
	private String name;
	EmitterMesh emitterShape = new EmitterMesh();
	Class particleType = ParticleDataTriMesh.class;
	ParticleDataMesh mesh = null;
	Mesh template = null;
	ParticleData[] particles;
//	Map<String,ParticleInfluencer> influencers = new HashMap();
	SafeArrayList<ParticleInfluencer> influencers = new SafeArrayList(ParticleInfluencer.class);
	Node emitterNode, particleNode, emitterTestNode, particleTestNode;
	
	// ParticleData info
	private int maxParticles;
	private float forceMax = .5f;
	private float forceMin = .15f;
	private float lifeMin = 12f;
	private float lifeMax = 14f;
	protected int activeParticleCount = 0;
	protected Interpolation interpolation = Interpolation.linear;
	
	// Emitter info
	int nextIndex = 0;
	private float targetInterval = .00015f, currentInterval = 0;
	private int emissionsPerSecond, totalParticlesThisEmission, particlesPerEmission;
	private float tpfThreshold = 1f/400f;
	private Matrix3f inverseRotation = Matrix3f.IDENTITY.clone();
	private boolean useStaticParticles = false;
	private boolean useRandomEmissionPoint = false;
	private boolean useSequentialEmissionFace = false;
	private boolean useSequentialSkipPattern = false;
	private boolean useVelocityStretching = false;
	private float velocityStretchFactor = 0.35f;
	private ForcedStretchAxis stretchAxis = ForcedStretchAxis.Y;
	private ParticleEmissionPoint particleEmissionPoint = ParticleEmissionPoint.Particle_Center;
	private DirectionType directionType = DirectionType.Random;
	
	// Material information
	private AssetManager assetManager;
	private Material mat, testMat, userDefinedMat = null;
	private String uniformName = "Texture";
	private Texture tex;
	private String texturePath;
	private float spriteWidth = -1, spriteHeight = -1;
	private int spriteCols = 1, spriteRows = 1;
	
	private BillboardMode billboardMode = BillboardMode.Camera;
	private boolean particlesFollowEmitter = false;
	
	private boolean enabled = false;
	private boolean requiresUpdate = false;
	private boolean postRequiresUpdate = false;
	
	private boolean TEST_EMITTER = false;
	private boolean TEST_PARTICLES = false;
	
	private boolean emitterInitialized = false;
	
	public Emitter() {
		emitterNode = new Node();
		emitterTestNode = new Node();
		particleNode = new Node();
		particleTestNode = new Node();
	}
	
	public <T extends ParticleDataMesh> void setParticleType(Class<T> t) {
		this.particleType = t;
	}
	
	public <T extends ParticleDataMesh> void setParticleType(Class<T> t, Mesh template) {
		this.particleType = t;
		this.template = template;
	}
	
	public ParticleDataMesh getParticleType() {
		return this.mesh;
	}
	
	public Mesh getParticleMeshTemplate() {
		return this.template;
	}
	
	public void setMaxParticles(int maxParticles) {
		this.maxParticles = maxParticles;
		if (emitterInitialized) {
			// rebuild emitter
		}
	}
	
	public void addInfluencers(ParticleInfluencer... influencers) {
		for (ParticleInfluencer pi : influencers) {
			addInfluencer(pi);
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private String generateName() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * Creates a new instance of the Emitter class
	 * @param name The name of the emitter (used as the output Node name containing the ParticleDataMesh)
	 * @param assetManager The application's asset manager
	 * @param type The particle type (point, triangle, etc)
	 * @param maxParticles The maximum number of particles handled by the emitter
	 * @param influencers The list of ParticleInfluencer's to add to the emitter control
	 */
	/*
	public Emitter(String name, AssetManager assetManager, int maxParticles, ParticleInfluencer... influencers) {
		this(name, assetManager, null, maxParticles, influencers);
	}
	
	public Emitter(String name, AssetManager assetManager, Material mat, int maxParticles, ParticleInfluencer... influencers) {
		this.name = name;
		this.emitterNode = new Node(this.name + ":Emitter");
		this.emitterTestNode = new Node(this.name + ":EmitterTest");
		this.particleNode = new Node(this.name);
		this.particleTestNode = new Node(this.name + ":Test");
		this.assetManager = assetManager;
		this.maxParticles = maxParticles;
	
		for (ParticleInfluencer pi : influencers) {
			addInfluencer(pi);
		}
		
		initMaterials();
		if (mat != null)
			this.mat = mat;
	}
	*/
	
	private void initMaterials() {
		mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		
		testMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		testMat.setColor("Color", ColorRGBA.Blue);
		testMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		testMat.getAdditionalRenderState().setWireframe(true);
	}
	
	private <T extends ParticleDataMesh> void initParticles(Class<T> t, Mesh template) {
		try {
			this.mesh = t.newInstance();
			if (template != null) {
				this.mesh.extractTemplateFromMesh(template);
				this.template = template;
			}
			initParticles();
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(Emitter.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	private void initParticles() {
		particles = new ParticleData[maxParticles];
		
		for (int i = 0; i < maxParticles; i++) {
			particles[i] = new ParticleData();
			particles[i].emitter = this;
			particles[i].index = i;
			particles[i].reset();
		}
		
		mesh.initParticleData(this, maxParticles);
	}
	
	// Emitter Shape
	/**
	 * Creates a single triangle emitter shape
	 */
	public void setShapeSimpleEmitter() {
		setShape(new TriangleEmitterShape(1));
		requiresUpdate = true;
	}
	
	/**
	 * Sets the particle emitter shape to the specified mesh
	 * @param mesh The Mesh to use as the particle emitter shape
	 */
	public final void setShape(Mesh mesh) {
		emitterShape.setShape(this, mesh);
		if (!emitterTestNode.getChildren().isEmpty()) {
			emitterTestNode.getChild(0).removeFromParent();
			Geometry testGeom = new Geometry();
			testGeom.setMesh(emitterShape.getMesh());
			emitterTestNode.attachChild(testGeom);
			emitterTestNode.setMaterial(testMat);
		}
		requiresUpdate = true;
	}
	
	/**
	 * Returns the current ParticleData Emitter's EmitterMesh
	 * @return The EmitterMesh containing the specified shape Mesh
	 */
	public EmitterMesh getShape() {
		return emitterShape;
	}
	
	/**
	 * Specifies the number of times the particle emitter will emit particles over the course of one second
	 * @param emissionsPerSecond The number of particle emissions per second
	 */
	public void setEmissionsPerSecond(int emissionsPerSecond) {
		this.emissionsPerSecond = emissionsPerSecond;
		targetInterval = 1f/emissionsPerSecond;
		requiresUpdate = true;
	}
	
	public int getEmissionsPerSecond() { return this.emissionsPerSecond; }
	
	/**
	 * Specifies the number of particles to be emitted per emission.
	 * @param particlesPerEmission The number of particle to emit per emission
	 */
	public void setParticlesPerEmission(int particlesPerEmission) {
		this.particlesPerEmission = particlesPerEmission;
		requiresUpdate = true;
	}
	
	public int getParticlesPerEmission() { return this.particlesPerEmission; }
	
	public void setDirectionType(DirectionType directionType) {
		this.directionType = directionType;
	}
	
	public DirectionType getDirectionType() { return this.directionType; }
	
	public void setTargetFPS(float fps) {
		tpfThreshold = 1f/fps;
	}
	
	/**
	 * Particles are created as staticly placed, with no velocity.  Particles set to static with remain in place and follow the emitter shape's animations.
	 * @param useStaticParticles 
	 */
	public void setUseStaticParticles(boolean useStaticParticles) {
		this.useStaticParticles = useStaticParticles;
		requiresUpdate = true;
	}
	
	/**
	 * Returns if particles are flagged as static
	 * @return Current state of static particle flag
	 */
	public boolean getUseStaticParticles() {
		return this.useStaticParticles;
	}
	
	public void setUseVelocityStretching(boolean useVelocityStretching) {
		this.useVelocityStretching = useVelocityStretching;
		requiresUpdate = true;
	}
	
	public boolean getUseVelocityStretching() { return this.useVelocityStretching; }
	
	public void setVelocityStretchFactor(float velocityStretchFactor) {
		this.velocityStretchFactor = velocityStretchFactor;
		requiresUpdate = true;
	}
	
	public float getVelocityStretchFactor() { return this.velocityStretchFactor; }
	
	public void setForcedStretchAxis(ForcedStretchAxis axis) {
		this.stretchAxis = axis;
		requiresUpdate = true;
	}
	
	public ForcedStretchAxis getForcedStretchAxis() { return this.stretchAxis; }
	
	public void setParticleEmissionPoint(ParticleEmissionPoint particleEmissionPoint) {
		this.particleEmissionPoint = particleEmissionPoint;
		requiresUpdate = true;
	}
	
	public ParticleEmissionPoint getParticleEmissionPoint() {
		return this.particleEmissionPoint;
	}
	
	/**
	 * Particles are effected by updates to the translation of the emitter node.  This option is set to false by default
	 * @param particlesFollowEmitter Particles should/should not update according to the emitter node's translation updates
	 */
	public void setParticlesFollowEmitter(boolean particlesFollowEmitter) {
		this.particlesFollowEmitter = particlesFollowEmitter;
		requiresUpdate = true;
	}
	
	/**
	 * Returns if the particles are set to update according to the emitter node's translation updates
	 * @return Current state of the follows emitter flag
	 */
	public boolean getParticlesFollowEmitter() { return this.particlesFollowEmitter; }
	
	public void setUseRandomEmissionPoint(boolean useRandomEmissionPoint) {
		this.useRandomEmissionPoint = useRandomEmissionPoint;
		requiresUpdate = true;
	}
	
	public boolean getUseRandomEmissionPoint() { return this.useRandomEmissionPoint; }
	
	public void setUseSequentialEmissionFace(boolean useSequentialEmissionFace) {
		this.useSequentialEmissionFace = useSequentialEmissionFace;
		requiresUpdate = true;
	}
	
	public boolean getUseSequentialEmissionFace() { return this.useSequentialEmissionFace; }
	
	public void setUseSequentialSkipPattern(boolean useSequentialSkipPattern) {
		this.useSequentialSkipPattern = useSequentialSkipPattern;
		requiresUpdate = true;
	}
	
	public boolean getUseSequentialSkipPattern() {
		return this.useSequentialSkipPattern;
	}
	
	// Life Cycle
	/**
	 * Sets the inner and outter bounds of the time a particle will remain alive (active)
	 * @param lifeMin The minimum time a particle must remian alive once emitted
	 * @param lifeMax The maximum time a particle can remain alive once emitted
	 */
	public void setLifeMinMax(float lifeMin, float lifeMax) {
		this.lifeMin = lifeMin;
		this.lifeMax = lifeMax;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the inner and outter bounds of the time a particle will remain alive (active) to a fixed duration of time
	 * @param life The fixed duration an emitted particle will remain alive
	 */
	public void setLife(float life) {
		this.lifeMin = life;
		this.lifeMax = life;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the outter bounds of the time a particle will remain alive (active)
	 * @param lifeMax The maximum time a particle can remain alive once emitted
	 */
	public void setLifeMax(float lifeMax) {
		this.lifeMax = lifeMax;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the maximum time a particle can remain alive once emitted.
	 * @return The maximum time a particle can remain alive once emitted
	 */
	public float getLifeMax() { return this.lifeMax; }
	
	/**
	 * Sets the inner bounds of the time a particle will remain alive (active)
	 * @param lifeMin The minimum time a particle must remian alive once emitted
	 */
	public void setLifeMin(float lifeMin) {
		this.lifeMin = lifeMin;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the minimum time a particle must remian alive once emitted
	 * @return The minimum time a particle must remian alive once emitted
	 */
	public float getLifeMin() { return this.lifeMin; }
	
	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
		requiresUpdate = true;
	}
	
	public Interpolation getInterpolation() {
		return this.interpolation;
	}
	
	// Force
	/**
	 * Sets the inner and outter bounds of the initial force with which the particle is emitted.  This directly effects the initial velocity vector of the particle.
	 * @param forceMin The minimum force with which the particle will be emitted
	 * @param forceMax The maximum force with which the particle can be emitted
	 */
	public void setForceMinMax(float forceMin, float forceMax) {
		this.forceMin = forceMin;
		this.forceMax = forceMax;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the inner and outter bounds of the initial force with which the particle is emitted to a fixed ammount.  This directly effects the initial velocity vector of the particle.
	 * @param force The force with which the particle will be emitted
	 */
	public void setForce(float force) {
		this.forceMin = force;
		this.forceMax = force;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the inner bounds of the initial force with which the particle is emitted.  This directly effects the initial velocity vector of the particle.
	 * @param forceMin The minimum force with which the particle will be emitted
	 */
	public void setForceMin(float forceMin) {
		this.forceMin = forceMin;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the outter bounds of the initial force with which the particle is emitted.  This directly effects the initial velocity vector of the particle.
	 * @param forceMax The maximum force with which the particle can be emitted
	 */
	public void setForceMax(float forceMax) {
		this.forceMax = forceMax;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the minimum force with which the particle will be emitted
	 * @return The minimum force with which the particle will be emitted
	 */
	public float getForceMin() { return this.forceMin; }
	
	/**
	 * Returns the maximum force with which the particle can be emitted
	 * @return The maximum force with which the particle can be emitted
	 */
	public float getForceMax() { return this.forceMax; }
	
	public int getMaxParticles() { return this.maxParticles; }
	
	// Influencers
	private void preloadAllInfluencers() {
		addInfluencer(new GravityInfluencer());
		addInfluencer(new RadialVelocityInfluencer());
		addInfluencer(new ColorInfluencer());
		addInfluencer(new SizeInfluencer());
		addInfluencer(new RotationInfluencer());
		addInfluencer(new SpriteInfluencer());
		addInfluencer(new DestinationInfluencer());
		addInfluencer(new PhysicsInfluencer());
		addInfluencer(new ImpulseInfluencer());
	}
	
	// Influencers
	private void preloadCoreInfluencers() {
		addInfluencer(new GravityInfluencer());
		addInfluencer(new ColorInfluencer());
		addInfluencer(new SizeInfluencer());
		addInfluencer(new RotationInfluencer());
		addInfluencer(new ImpulseInfluencer());
	}
	
	/**
	 * Adds a new ParticleData Influencer to the chain of influencers that will effect particles
	 * @param influencer The particle influencer to add to the chain
	 */
	public final void addInfluencer(ParticleInfluencer influencer) {
	//	influencers.put(influencer.getInfluencerClass().getName(), influencer);
		influencers.add(influencer);
		requiresUpdate = true;
	}
	
	/**
	 * Returns the current chain of particle influencers
	 * @return The Collection of particle influencers
	 */
	public ParticleInfluencer[] getInfluencers() {
		return (ParticleInfluencer[])this.influencers.getArray();
	}
	
	/**
	 * 
	 * @param type
	 * @return 
	 */
	/*
	public ParticleInfluencer getInfluencer(String type) {
		T ret = null;
		for (ParticleInfluencer pi : (ParticleInfluencer[])influencers.getArray()) {
			if (pi.getInfluencerClass() == c)
				ret = (T)pi;
		}
		return ret;
	//	return influencers.get(type);
	}
	*/
	/**
	 * Returns the first instance of a specified ParticleData Influencer type
	 * @param <T>
	 * @param c
	 * @return 
	 */
	public <T extends ParticleInfluencer> T getInfluencer(Class<T> c) {
		T ret = null;
		for (ParticleInfluencer pi : (ParticleInfluencer[])influencers.getArray()) {
			if (pi.getInfluencerClass() == c) {
				ret = (T)pi;
				break;
			}
		}
		return ret;
	//	return (T) influencers.get(c.getName());
	}
	
	public void removeInfluencer(Class c) {
		for (ParticleInfluencer pi : (ParticleInfluencer[])influencers.getArray()) {
			if (pi.getInfluencerClass() == c) {
				influencers.remove(pi);
				break;
			}
		}
		requiresUpdate = true;
	}
	
	public void removeAllInfluencers() {
		influencers.clear();
		requiresUpdate = true;
	}
	// Material
	/**
	 * Sets the texture to be used by particles, this can contain multiple images for random image selection or sprite animation of particles.
	 * @param texturePath The path of the texture to use
	 * @param spriteFrameWidth The width in pixels of a single sprite frame
	 * @param spriteFrameHeight The height in pixels of a single sprite frame
	 * @return Texture
	 */
	public void setSpriteBySize(String texturePath, float spriteFrameWidth, float spriteFrameHeight) {
		setSpriteBySize(texturePath, "Texture", spriteFrameWidth, spriteFrameHeight);
	}
	
	public void setSpriteBySize(String texturePath, String uniformName, float spriteFrameWidth, float spriteFrameHeight) {
		this.texturePath = texturePath;
		this.spriteWidth = spriteFrameWidth;
		this.spriteHeight = spriteFrameHeight;
		this.uniformName = uniformName;
		
		if (emitterInitialized) {
			tex = assetManager.loadTexture(texturePath);
			tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			tex.setMagFilter(Texture.MagFilter.Bilinear);
			mat.setTexture(uniformName, tex);

			Image img = tex.getImage();
			int width = img.getWidth();
			int height = img.getHeight();

			spriteCols = (int)(width/spriteFrameWidth);
			spriteRows = (int)(height/spriteFrameHeight);
			
			mesh.setImagesXY(spriteCols,spriteRows);
			requiresUpdate = true;
		}
	}
	
	public void setSprite(String texturePath) {
		setSpriteByCount(texturePath, "Texture", 1, 1);
	}
	
	public void setSprite(String texturePath, int numCols, int numRows) {
		setSpriteByCount(texturePath, "Texture", numCols, numRows);
	}
	
	public void setSprite(String texturePath, String uniformName) {
		setSpriteByCount(texturePath, uniformName, 1, 1);
	}
	
	public void setSprite(String texturePath, String uniformName, int numCols, int numRows) {
		setSpriteByCount(texturePath, uniformName, numCols, numRows);
	}
	
	public void setSpriteByCount(String texturePath, int numCols, int numRows) {
		setSpriteByCount(texturePath, "Texture", numCols, numRows);
	}
	
	public void setSpriteByCount(String texturePath, String uniformName, int numCols, int numRows) {
		this.texturePath = texturePath;
		this.spriteCols = numCols;
		this.spriteRows = numRows;
		this.uniformName = uniformName;
		
		if (emitterInitialized) {
			tex = assetManager.loadTexture(texturePath);
			tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			tex.setMagFilter(Texture.MagFilter.Bilinear);
			mat.setTexture(uniformName, tex);

			Image img = tex.getImage();
			int width = img.getWidth();
			int height = img.getHeight();

			spriteWidth = (int)(width/spriteCols);
			spriteHeight = (int)(height/spriteRows);
			
			mesh.setImagesXY(spriteCols,spriteRows);
			requiresUpdate = true;
		}
	}
	
	public Material getMaterial() { return this.mat; }
	
	@SuppressWarnings("empty-statement")
	public void setMaterial(Material mat) {
		this.userDefinedMat = mat;
		
		if (emitterInitialized) {
			tex = assetManager.loadTexture(texturePath);
			tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			tex.setMagFilter(Texture.MagFilter.Bilinear);
			mat.setTexture(uniformName, tex);
		}
		
		if (particleNode != null) {
			particleNode.setMaterial(mat);
			requiresUpdate = true;
		}
	}
	
	@SuppressWarnings("empty-statement")
	public void setMaterialUnshaded() {
		mat = new Material(assetManager, "emitter/shaders/Particle.j3md");
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		mat.getAdditionalRenderState().setAlphaTest(true);
		
		tex = assetManager.loadTexture(texturePath);
		tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		tex.setMagFilter(Texture.MagFilter.Bilinear);
		mat.setTexture("Texture", tex);
		
		if (particleNode != null) {
			particleNode.setMaterial(mat);
			requiresUpdate = true;
		}
	}
	
	/**
	 * Returns the number of columns of sprite images in the specified texture
	 * @return The number of available sprite columns
	 */
	public int getSpriteColCount() { return this.spriteCols; }
	
	/**
	 * Returns the number of rows of sprite images in the specified texture
	 * @return The number of available sprite rows
	 */
	public int getSpriteRowCount() { return this.spriteRows; }
	
	/**
	 * Sets the billboard mode to be used by emitted particles.  The default mode is Camera
	 * @param billboardMode The billboard mode to use
	 */
	public void setBillboardMode(BillboardMode billboardMode) {
		this.billboardMode = billboardMode;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the current selected BillboardMode used by emitted particles
	 * @return The current selected BillboardMode
	 */
	public BillboardMode getBillboardMode() {
		return billboardMode;
	}
	
	/**
	 * Enables the particle emitter.  The emitter is disabled by default.
	 * @param enabled Activate/deactivate the emitter
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() { return this.enabled; }
	
	public void initialize(AssetManager assetManager) {
		if (!emitterInitialized) {
			this.assetManager = assetManager;
			initMaterials();
			if (userDefinedMat != null) {
				mat = userDefinedMat;
			}
			if (this.name == null)
				name = this.generateName();
			emitterNode.setName(this.name + ":Emitter");
			emitterTestNode.setName(this.name + ":EmitterTest");
			particleNode.setName(this.name);
			particleTestNode.setName(this.name + ":Test");
			
			initParticles(particleType,mesh);
			mesh.setImagesXY(spriteCols,spriteRows);
			
			tex = assetManager.loadTexture(texturePath);
			tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			tex.setMagFilter(Texture.MagFilter.Bilinear);
			mat.setTexture(uniformName, tex);

			Image img = tex.getImage();
			int width = img.getWidth();
			int height = img.getHeight();
			
		//	if (spriteWidth != -1 && spriteHeight != -1) {
		//		spriteCols = (int)(width/spriteWidth);
		//		spriteRows = (int)(height/spriteHeight);
		//	} else {
				spriteWidth = width/spriteCols;
				spriteHeight = height/spriteRows;
		//	}

			if (emitterInitialized) {
				mesh.setImagesXY(spriteCols,spriteRows);
				requiresUpdate = true;
			}
			if (particleNode.getChildren().isEmpty()) {
				Geometry geom = new Geometry();
				geom.setMesh(mesh);
				particleNode.attachChild(geom);
				particleNode.setMaterial(mat);
				particleNode.setQueueBucket(RenderQueue.Bucket.Transparent);
			}
			
			if (emitterTestNode.getChildren().isEmpty()) {
				Geometry testGeom = new Geometry();
				testGeom.setMesh(emitterShape.getMesh());
				emitterTestNode.attachChild(testGeom);
				emitterTestNode.setMaterial(testMat);
			}
			if (particleTestNode.getChildren().isEmpty()) {
				Geometry testPGeom = new Geometry();
				testPGeom.setMesh(mesh);
				particleTestNode.attachChild(testPGeom);
				particleTestNode.setMaterial(testMat);
			}
			
			emitterInitialized = true;
		}
	}
	
	@Override
	public void setSpatial(Spatial spatial) {
		if (spatial != null) {
			((Node)spatial).attachChild(particleNode);
			if (TEST_EMITTER)
				((Node)spatial).attachChild(emitterTestNode);
			if (TEST_PARTICLES)
				((Node)spatial).attachChild(particleTestNode);
		} else {
			particleNode.removeFromParent();
			emitterTestNode.removeFromParent();
			particleTestNode.removeFromParent();
		}
		requiresUpdate = true;
		this.spatial = spatial;
	}
	
	public Spatial getSpatial() { return this.spatial; }
	
	public void setEmitterTestMode(boolean showEmitterShape, boolean showParticleMesh) {
		this.TEST_EMITTER = showEmitterShape;
		this.TEST_PARTICLES = showParticleMesh;
		
		if (spatial != null) {
			if (TEST_EMITTER)
				((Node)spatial).attachChild(emitterTestNode);
			else
				emitterTestNode.removeFromParent();
			
			if (TEST_PARTICLES)
				((Node)spatial).attachChild(particleTestNode);
			else
				particleTestNode.removeFromParent();
		}
		requiresUpdate = true;
	}
	
	public boolean getEmitterTestModeShape() {
		return TEST_EMITTER;
	}
	
	public boolean getEmitterTestModeParticles() {
		return TEST_PARTICLES;
	}
	
	/**
	 * Returns the node containing the particle mesh
	 * @return The node containing the particle mesh
	 */
	public Node getParticleNode() {
		return this.particleNode;
	}
	
	/**
	 * Returns the node containing the emitter transform information
	 * @return The node containing the emitter transform information
	 */
	public Node getEmitterNode() {
		return this.emitterNode;
	}
	
	@Override
	public void update(float tpf) {
		if (enabled && emitterInitialized) {
			for (ParticleData p : particles) {
				if (p.active) p.update(tpf);
			}

			currentInterval += (tpf <= targetInterval) ? tpf : targetInterval;

			if (currentInterval >= targetInterval) {
				totalParticlesThisEmission = this.particlesPerEmission;
				for (int i = 0; i < totalParticlesThisEmission; i++) {
					emitNextParticle();
				}
				currentInterval -= targetInterval;
			}
		//	((Geometry)particleNode.getChild(0)).updateModelBound();
		} else {
			currentInterval = 0;
		}
		if (emitterInitialized && (enabled || postRequiresUpdate)) {
			((Geometry)particleNode.getChild(0)).updateModelBound();
			postRequiresUpdate = false;
		}
	}
	
	private int calcParticlesPerEmission() {
		return (int)(currentInterval/targetInterval*particlesPerEmission);
	}
	
	/**
	 * Emits the next available (non-active) particle
	 */
	public void emitNextParticle() {
		if (nextIndex != -1 && nextIndex < maxParticles) {
			particles[nextIndex].initialize();
			int searchIndex = nextIndex;
			int initIndex = nextIndex;
			int loop = 0;
			while (particles[searchIndex].active) {
				searchIndex++;
				if (searchIndex > particles.length-1) {
					searchIndex = 0;
					loop++;
				}
				if (searchIndex == initIndex && loop == 1) {
					searchIndex = -1;
					break;
				}
			}
			nextIndex = searchIndex;
		}
		/*
		if (nextIndex != -1 && nextIndex < maxParticles) {
			particles[nextIndex].initialize();
			int searchIndex = nextIndex;
			while (particles[searchIndex].active) {
				searchIndex++;
				if (searchIndex > particles.length-1) {
					searchIndex = -1;
					break;
				}
			}
			nextIndex = searchIndex;
		}
		*/
	}
	
	/**
	 * Emits all non-active particles
	 */
	public void emitAllParticles() {
		for (ParticleData p : particles) {
			if (!p.active)
				p.initialize();
		}
		requiresUpdate = true;
	}
	
	public void emitNumParticles(int count) {
		int counter = 0;
		for (ParticleData p : particles) {
			if (!p.active && counter < count) {
				p.initialize();
				counter++;
			}
			if (counter > count)
				break;
		}
		requiresUpdate = true;
	}
	
	public void killAllParticles() {
		for (ParticleData p : particles) {
			p.reset();
		}
		requiresUpdate = true;
	}
	
	/**
	 * Deactivates and resets the specified particle
	 * @param p The particle to reset
	 */
	public void killParticle(ParticleData p) {
		for (ParticleData particle : particles) {
			if (particle == p)
				p.reset();
		}
		requiresUpdate = true;
	}
	
	public int getActiveParticleCount() {
		return activeParticleCount;
	}
	
	public void incActiveParticleCount() {
		activeParticleCount++;
	}
	
	public void decActiveParticleCount() {
		activeParticleCount--;
	}
	
	/**
	 * Deactivates and resets the specified particle
	 * @param index The index of the particle to reset
	 */
	public void killParticle(int index) {
		particles[index].reset();
		requiresUpdate = true;
	}
	
	public void reset() {
		killAllParticles();
		currentInterval = 0;
		requiresUpdate = true;
	}
	
	public void resetInterval() {
		currentInterval = 0;
	}
	
	/**
	 * This method should not be called.  Particles call this method to help track the next available particle index
	 * @param index The index of the particle that was just reset
	 */
	public void setNextIndex(int index) {
		if (index < nextIndex || nextIndex == -1)
			nextIndex = index;
	}
	
	@Override
	public void render(RenderManager rm, ViewPort vp) {
		if (emitterInitialized && (enabled || (!enabled && requiresUpdate))) {
			Camera cam = vp.getCamera();

			if (mesh.getClass() == ParticleDataPointMesh.class) {
				float C = cam.getProjectionMatrix().m00;
				C *= cam.getWidth() * 0.5f;

				// send attenuation params
				mat.setFloat("Quadratic", C);
			}
			mesh.updateParticleData(particles, cam, inverseRotation);
			if (requiresUpdate) {
				requiresUpdate = false;
				postRequiresUpdate = true;
			}
		}
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(name, "name", null);
		oc.writeSavableArrayList(new ArrayList(influencers), "influencers", null);
		oc.write(maxParticles, "maxParticles", 30);
		oc.write(forceMin, "forceMin", .15f);
		oc.write(forceMax, "forceMax", .5f);
		oc.write(lifeMin, "lifeMin", 1.5f);
		oc.write(lifeMax, "lifeMax", 2.5f);
		oc.write(targetInterval, "targetInterval", .00015f);
		oc.write(currentInterval, "currentInterval", 0f);
		oc.write(emissionsPerSecond, "emissionsPerSecond", 20);
		oc.write(particlesPerEmission, "particlesPerEmission", 1);
		oc.write(useStaticParticles, "useStaticParticles", false);
		oc.write(mat, "mat", null);
		oc.write(tex, "tex", null);
		oc.write(texturePath, "texturePath", null);
		oc.write(spriteWidth, "spriteWidth", 50);
		oc.write(spriteHeight, "spriteHeight", 50);
		oc.write(spriteCols, "spriteCols", 1);
		oc.write(spriteRows, "spriteRows", 1);
		oc.write(billboardMode, "billboardMode", BillboardMode.Camera);
		oc.write(particlesFollowEmitter, "particlesFollowEmitter", false);
		
		oc.write(enabled, "enabled", false);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		name = ic.readString("name", null);
		influencers = new SafeArrayList<ParticleInfluencer>(ParticleInfluencer.class, ic.readSavableArrayList("influencers", null));
		maxParticles = ic.readInt("maxParticles", 30);
		forceMin = ic.readFloat("forceMin", .15f);
		forceMax = ic.readFloat("forceMax", .5f);
		lifeMin = ic.readFloat("lifeMin", 1.5f);
		lifeMax = ic.readFloat("lifeMax", 2.5f);
		targetInterval = ic.readFloat("targetInterval", .00015f);
		currentInterval = ic.readFloat("currentInterval", 0f);
		emissionsPerSecond = ic.readInt("emissionsPerSecond", 20);
		particlesPerEmission = ic.readInt("particlesPerEmission", 1);
		useStaticParticles = ic.readBoolean("useStaticParticles", false);
		mat = (Material)ic.readSavable("mat", null);
		tex = (Texture)ic.readSavable("tex", null);
		texturePath = ic.readString("texturePath", null);
		spriteWidth = ic.readFloat("spriteWidth", 50f);
		spriteHeight = ic.readFloat("spriteHeight", 50f);
		spriteCols = ic.readInt("spriteCols", 1);
		spriteRows = ic.readInt("spriteRows", 1);
		billboardMode = ic.readEnum("billboardMode", BillboardMode.class, BillboardMode.Camera);
		particlesFollowEmitter = ic.readBoolean("particlesFollowEmitter", false);
		enabled = ic.readBoolean("enabled", false);
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		Emitter clone = new Emitter();
		clone.setMaxParticles(maxParticles);
		clone.setShape(emitterShape.getMesh());
		clone.setParticleType(particleType, template);
		clone.setInterpolation(getInterpolation());
		clone.setForceMinMax(forceMin, forceMax);
		clone.setLifeMinMax(lifeMin, lifeMax);
		clone.setEmissionsPerSecond(emissionsPerSecond);
		clone.setParticlesPerEmission(particlesPerEmission);
		clone.setUseRandomEmissionPoint(useRandomEmissionPoint);
		clone.setUseSequentialEmissionFace(useSequentialEmissionFace);
		clone.setUseSequentialSkipPattern(useSequentialSkipPattern);
		clone.setParticlesFollowEmitter(particlesFollowEmitter);
		clone.setUseStaticParticles(useStaticParticles);
		clone.setUseVelocityStretching(useVelocityStretching);
		clone.setVelocityStretchFactor(velocityStretchFactor);
		clone.setForcedStretchAxis(stretchAxis);
		clone.setParticleEmissionPoint(particleEmissionPoint);
		clone.setBillboardMode(billboardMode);
		clone.setEmitterTestMode(TEST_EMITTER, TEST_PARTICLES);
		
		for (ParticleInfluencer inf : influencers) {
			clone.addInfluencer(inf.clone());
		}
		
	//	clone.initParticles(mesh.getClass(), template);
		clone.setSprite(texturePath, spriteCols, spriteRows);
		clone.initialize(assetManager);
		clone.setEnabled(enabled);
		
		clone.setSpatial(spatial);
		return clone;
	}
	
	@Override
	public Emitter clone() {
		Emitter clone = new Emitter();
		clone.setMaxParticles(maxParticles);
		clone.setShape(emitterShape.getMesh());
		clone.setParticleType(particleType, template);
		clone.setInterpolation(getInterpolation());
		clone.setForceMinMax(forceMin, forceMax);
		clone.setLifeMinMax(lifeMin, lifeMax);
		clone.setEmissionsPerSecond(emissionsPerSecond);
		clone.setParticlesPerEmission(particlesPerEmission);
		clone.setUseRandomEmissionPoint(useRandomEmissionPoint);
		clone.setUseSequentialEmissionFace(useSequentialEmissionFace);
		clone.setUseSequentialSkipPattern(useSequentialSkipPattern);
		clone.setParticlesFollowEmitter(particlesFollowEmitter);
		clone.setUseStaticParticles(useStaticParticles);
		clone.setUseVelocityStretching(useVelocityStretching);
		clone.setVelocityStretchFactor(velocityStretchFactor);
		clone.setForcedStretchAxis(stretchAxis);
		clone.setParticleEmissionPoint(particleEmissionPoint);
		clone.setBillboardMode(billboardMode);
		clone.setEmitterTestMode(TEST_EMITTER, TEST_PARTICLES);
		
		for (ParticleInfluencer inf : influencers) {
			clone.addInfluencer(inf.clone());
		}
		
	//	clone.initParticles(mesh.getClass(), template);
		clone.setSprite(texturePath, spriteCols, spriteRows);
		clone.initialize(assetManager);
		clone.setEnabled(enabled);
		return clone;
	}
	
	public void setLocalTranslation(Vector3f translation) {
		emitterNode.setLocalTranslation(translation);
		emitterTestNode.setLocalTranslation(translation);
		particleNode.setLocalTranslation(translation);
		particleTestNode.setLocalTranslation(translation);
		requiresUpdate = true;
	}
	
	public void setLocalTranslation(float x, float y, float z) {
		emitterNode.setLocalTranslation(x, y, z);
		emitterTestNode.setLocalTranslation(x, y, z);
		particleNode.setLocalTranslation(x, y, z);
		particleTestNode.setLocalTranslation(x, y, z);
		requiresUpdate = true;
	}
	
	public void setLocalRotation(Quaternion q) {
		emitterNode.setLocalRotation(q);
		emitterTestNode.setLocalRotation(q);
		requiresUpdate = true;
	}
	
	public void setLocalRotation(Matrix3f m) {
		emitterNode.setLocalRotation(m);
		emitterTestNode.setLocalRotation(m);
		requiresUpdate = true;
	}
	
	public void setLocalScale(Vector3f scale) {
		emitterNode.setLocalScale(scale);
		emitterTestNode.setLocalScale(scale);
		requiresUpdate = true;
	}
	
	public void setLocalScale(float scale) {
		emitterNode.setLocalScale(scale);
		emitterTestNode.setLocalScale(scale);
		requiresUpdate = true;
	}
	
	public void setLocalScale(float x, float y, float z) {
		emitterNode.setLocalScale(x, y, z);
		emitterTestNode.setLocalScale(x, y, z);
		requiresUpdate = true;
	}
	
	public Quaternion getLocalRotation() {
		return emitterNode.getLocalRotation();
	}
	
	public Vector3f getLocalTranslation() {
		return emitterNode.getLocalTranslation();
	}
	
	public Vector3f getLocalScale() {
		return emitterNode.getLocalScale();
	}
	
}
