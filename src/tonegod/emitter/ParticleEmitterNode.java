package tonegod.emitter;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import rlib.util.ClassUtils;
import rlib.util.Util;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.UnsafeArray;
import tonegod.emitter.geometry.EmitterShapeGeometry;
import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.material.ParticlesMaterial;
import tonegod.emitter.node.ParticleNode;
import tonegod.emitter.node.TestParticleEmitterNode;
import tonegod.emitter.particle.ParticleData;
import tonegod.emitter.particle.ParticleDataMesh;
import tonegod.emitter.particle.ParticleDataMeshInfo;
import tonegod.emitter.particle.ParticleDataPointMesh;
import tonegod.emitter.particle.ParticleDataTriMesh;
import tonegod.emitter.shapes.TriangleEmitterShape;

import static java.lang.Class.forName;
import static rlib.util.ClassUtils.unsafeCast;
import static rlib.util.array.ArrayFactory.newArray;

/**
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class ParticleEmitterNode extends Node implements JmeCloneable, Cloneable {

    public static final ArrayList<Object> EMPTY_INFLUENCERS = new ArrayList<>();

    public enum BillboardMode {
        /**
         * Facing direction follows the velocity as it changes
         */
        VELOCITY("Velocity"),
        /**
         * Facing direction follows the velocity as it changes, Y of particle always faces Z of
         * velocity
         */
        VELOCITY_Z_UP("Velocity Z up"),
        /**
         * Facing direction follows the velocity as it changes, Y of particle always faces Z of
         * velocity, Up of the particle always faces X
         */
        VELOCITY_Z_UP_Y_LEFT("Velocity Z up Y left"),
        /**
         * Facing direction remains constant to the face of the particle emitter shape that the
         * particle was emitted from
         */
        NORMAL("Normal"),
        /**
         * Facing direction remains constant for X, Z axis' to the face of the particle emitter
         * shape that the particle was emitted from. Y axis maps to UNIT_Y
         */
        NORMAL_Y_UP("Normal Y up"),
        /**
         * ParticleData always faces camera
         */
        CAMERA("Camera"),
        /**
         * ParticleData always faces X axis
         */
        UNIT_X("Unit X"),
        /**
         * ParticleData always faces Y axis
         */
        UNIT_Y("Unit Y"),
        /**
         * ParticleData always faces Z axis
         */
        UNIT_Z("Unit Z");

        private static final BillboardMode[] VALUES = values();

        public static BillboardMode valueOf(final int index) {
            return VALUES[index];
        }

        private final String uiName;

        BillboardMode(final String uiName) {
            this.uiName = uiName;
        }

        @Override
        public String toString() {
            return uiName;
        }
    }

    public enum ForcedStretchAxis {
        X, Y, Z;

        private static final ForcedStretchAxis[] VALUES = values();

        public static ForcedStretchAxis valueOf(final int index) {
            return VALUES[index];
        }
    }

    public enum ParticleEmissionPoint {
        PARTICLE_CENTER("Center"),
        PARTICLE_EDGE_TOP("Edge top"),
        PARTICLE_EDGE_BOTTOM("Edge bottom");

        private static final ParticleEmissionPoint[] VALUES = values();


        public static ParticleEmissionPoint valueOf(final int index) {
            return VALUES[index];
        }

        private final String uiName;

        ParticleEmissionPoint(final String uiName) {
            this.uiName = uiName;
        }

        @Override
        public String toString() {
            return uiName;
        }
    }

    /** ------------INFLUENCERS------------ **/

    protected Array<ParticleInfluencer> influencers;

    /**
     * THe flag for enabling/disabling emitter.
     */
    protected boolean enabled;
    protected boolean requiresUpdate;
    protected boolean postRequiresUpdate;
    protected boolean emitterInitialized;

    /** ------------EMITTER------------ **/

    /**
     * The next index of particle to emit.
     */
    protected int nextIndex;

    /**
     * The target interval.
     */
    protected float targetInterval;

    /**
     * The current interval.
     */
    protected float currentInterval;

    /**
     * The count of emissions per second.
     */
    protected int emissionsPerSecond;

    protected int totalParticlesThisEmission;

    /**
     * The count of particles per emission.
     */
    protected int particlesPerEmission;

    protected float tpfThreshold;

    /**
     * The inversed rotation.
     */
    protected Matrix3f inverseRotation;

    protected boolean staticParticles;
    protected boolean randomEmissionPoint;
    protected boolean sequentialEmissionFace;
    protected boolean sequentialSkipPattern;
    protected boolean velocityStretching;

    /**
     * The velocity stretch factor.
     */
    protected float velocityStretchFactor;

    /**
     * The stretch axis.
     */
    protected ForcedStretchAxis stretchAxis;

    /**
     * The particle emission point.
     */
    protected ParticleEmissionPoint particleEmissionPoint;

    /**
     * The direction type.
     */
    protected EmitterMesh.DirectionType directionType;

    /**
     * The emitter shape.
     */
    protected EmitterMesh emitterShape;

    /**
     * The test emitter node.
     */
    protected TestParticleEmitterNode emitterTestNode;

    /**
     * Emitter shape test geometry.
     */
    protected EmitterShapeGeometry emitterShapeTestGeometry;

    /** -----------PARTICLES------------- **/

    /**
     * The particle node.
     */
    protected ParticleNode particleNode;

    /**
     * The particles test node.
     */
    protected TestParticleEmitterNode particleTestNode;

    /**
     * Particles geometry.
     */
    protected ParticleGeometry particleGeometry;

    /**
     * Particles test geometry.
     */
    protected ParticleGeometry particleTestGeometry;

    /**
     * The billboard mode.
     */
    protected BillboardMode billboardMode;

    /**
     * The flag for following sprites for emitter.
     */
    protected boolean particlesFollowEmitter;

    /** ------------PARTICLES MESH DATA------------ **/

    /**
     * The array of particles.
     */
    protected ParticleData[] particles;

    /**
     * The class type of the using {@link ParticleDataMesh}.
     */
    protected Class<? extends ParticleDataMesh> particleDataMeshType;

    /**
     * The data mesh of particles.
     */
    protected ParticleDataMesh particleDataMesh;

    /**
     * The template of mesh of particles.
     */
    protected Mesh particleMeshTemplate;

    /**
     * The active count of particles.
     */
    protected int activeParticleCount;

    /**
     * The maximum count of particles.
     */
    protected int maxParticles;

    /**
     * The maximum force of particles.
     */
    protected float forceMax;

    /**
     * The minimum force of particles.
     */
    protected float forceMin;

    /**
     * The minimum life of particles.
     */
    protected float lifeMin;

    /**
     * The maximum life of particles.
     */
    protected float lifeMax;

    /**
     * The interpolation.
     */
    protected Interpolation interpolation;

    /** ------------PARTICLES MATERIAL------------ **/

    /**
     * The asset manager.
     */
    protected AssetManager assetManager;

    /**
     * The material of particles.
     */
    protected Material material;

    /**
     * The material of test particles geometry.
     */
    protected Material testMat;

    protected boolean applyLightingTransform;

    /**
     * The name of texture parameter of particles material.
     */
    protected String textureParamName;

    /**
     * The sprite width.
     */
    protected float spriteWidth;

    /**
     * The sprite height.
     */
    protected float spriteHeight;

    /**
     * The count of sprite columns.
     */
    protected int spriteCols;

    /**
     * The count of sprite rows.
     */
    protected int spriteRows;

    // Emitter animation
    protected Node emitterAnimNode;

    protected boolean emitterNodeExists;

    protected AnimControl emitterAnimControl;
    protected AnimChannel emitterAnimChannel;

    protected String emitterAnimName = "";

    protected float emitterAnimSpeed;
    protected float emitterAnimBlendTime;

    protected LoopMode emitterAnimLoopMode;

    // Particle animation
    protected Node particlesAnimNode;

    protected boolean particlesNodeExists;

    protected AnimControl particlesAnimControl;
    protected AnimChannel particlesAnimChannel;

    protected String particlesAnimName = "";

    protected float particlesAnimSpeed;
    protected float particlesAnimBlendTime;

    protected LoopMode particlesAnimLoopMode;

    public boolean testEmitter;
    public boolean testParticles;

    /**
     * Creates a new instance of the Emitter
     */
    public ParticleEmitterNode(@NotNull final AssetManager assetManager) {
        this();
        changeEmitterShapeMesh(new TriangleEmitterShape(1));
        changeParticleMeshType(ParticleDataTriMesh.class, null);
        initialize(assetManager, true, true);
    }

    /**
     * Creates a new instance of the Emitter
     */
    public ParticleEmitterNode() {
        setName("Emitter Node");
        this.textureParamName = "Texture";
        this.inverseRotation = Matrix3f.IDENTITY.clone();
        this.tpfThreshold = 1f / 400f;
        this.targetInterval = 0.00015f;
        this.currentInterval = 0;
        this.velocityStretchFactor = 0.35f;
        this.stretchAxis = ForcedStretchAxis.Y;
        this.particleEmissionPoint = ParticleEmissionPoint.PARTICLE_CENTER;
        this.directionType = EmitterMesh.DirectionType.RANDOM;
        this.interpolation = Interpolation.linear;
        this.influencers = newArray(ParticleInfluencer.class, 0);
        this.particleDataMeshType = ParticleDataTriMesh.class;
        this.emitterShape = new EmitterMesh();
        this.emitterShapeTestGeometry = new EmitterShapeGeometry("Emitter Shape Test Geometry");
        this.emitterTestNode = new TestParticleEmitterNode("EmitterTestNode");
        this.emitterTestNode.attachChild(emitterShapeTestGeometry);
        this.particleTestGeometry = new ParticleGeometry("Particle Test Geometry");
        this.particleTestNode = new TestParticleEmitterNode("Particle Test Node");
        this.particleTestNode.attachChild(particleTestGeometry);
        //this.particleTestNode.setQueueBucket(RenderQueue.Bucket.Transparent);
        this.particleGeometry = new ParticleGeometry("Particle Geometry");
        this.particleNode = new ParticleNode("Particle Node");
        this.particleNode.attachChild(particleGeometry);
        this.particleNode.setQueueBucket(RenderQueue.Bucket.Transparent);
        this.forceMax = 0.5f;
        this.forceMin = 0.15f;
        this.lifeMin = 0.999f;
        this.lifeMax = 0.999f;
        this.particlesPerEmission = 1;
        this.maxParticles = 100;
        this.billboardMode = BillboardMode.CAMERA;
        this.spriteWidth = -1;
        this.spriteCols = 1;
        this.spriteRows = 1;
        this.spriteHeight = -1;
        this.emitterNodeExists = true;
        this.emitterAnimSpeed = 1;
        this.emitterAnimBlendTime = 1;
        this.emitterAnimLoopMode = LoopMode.Loop;
        this.particlesAnimSpeed = 1;
        this.particlesAnimBlendTime = 1;
        this.particlesAnimLoopMode = LoopMode.Loop;
        attachChild(particleNode);
        setEmissionsPerSecond(100);
    }

    /**
     * @return the particle node.
     */
    public ParticleNode getParticleNode() {
        return particleNode;
    }

    /**
     * Sets the mesh class used to create the particle mesh. For example:
     * ParticleDataTemplateMesh.class - Uses a supplied mesh as a particleMeshTemplate for particles
     * NOTE: This method is supplied for use with animated particles.
     *
     * @param type     The Mesh class used to create the particle Mesh
     * @param template The Node to extract the particleMeshTemplate mesh used to define a single
     *                 particle
     */
    @Deprecated
    public <T extends ParticleDataMesh> void setParticleType(@NotNull final Class<T> type, @NotNull final Node template) {
        if (particlesAnimNode != null) particlesAnimNode.removeFromParent();

        this.particleDataMeshType = type;
        this.particlesAnimNode = template;
        this.particleMeshTemplate = ((Geometry) particlesAnimNode.getChild(0)).getMesh();

        particlesAnimNode.setLocalScale(0);
        particlesAnimControl = particlesAnimNode.getControl(AnimControl.class);

        if (particlesAnimControl != null) {
            particlesAnimChannel = particlesAnimControl.createChannel();
        }

        if (emitterInitialized) {
            attachChild(particlesAnimNode);
        }
    }

    /**
     * Returns the Class defined for the particle type. (ex. ParticleDataTriMesh.class - a quad-base
     * particle)
     */
    @NotNull
    public Class<? extends ParticleDataMesh> getParticleDataMeshType() {
        return particleDataMeshType;
    }

    /**
     * Returns the Mesh defined as a particleMeshTemplate for a single particle
     *
     * @return The Mesh to use as a particle particleMeshTemplate
     */
    @Nullable
    public Mesh getParticleMeshTemplate() {
        return particleMeshTemplate;
    }

    @Override
    protected void setParent(@Nullable final Node parent) {
        super.setParent(parent);
        if (parent == null && isEnabled()) setEnabled(false);
    }

    @Deprecated
    public void setParticleAnimation(@NotNull final String particlesAnimName, final float particleAnimSpeed, final float particlesAnimBlendTime, @NotNull final LoopMode particlesAnimLoopMode) {
        this.particlesAnimName = particlesAnimName;
        this.particlesAnimSpeed = particleAnimSpeed;
        this.particlesAnimBlendTime = particlesAnimBlendTime;
        this.particlesAnimLoopMode = particlesAnimLoopMode;

        if (emitterInitialized && particlesAnimControl != null) {
            particlesAnimChannel.setAnim(particlesAnimName, particlesAnimBlendTime);
            particlesAnimChannel.setSpeed(particleAnimSpeed);
            particlesAnimChannel.setLoopMode(particlesAnimLoopMode);
        }
    }

    /**
     * Sets the maximum number of particles the emitter will manage
     */
    public void setMaxParticles(final int maxParticles) {
        if (maxParticles < 0) throw new IllegalArgumentException("maxParticles can't be negative.");
        this.maxParticles = maxParticles;
        if (!emitterInitialized) return;
        killAllParticles();
        initParticles();
    }

    private void initMaterials() {
        if (material != null && testMat != null) return;

        final Texture texture = assetManager.loadTexture("textures/default.png");
        texture.setMinFilter(MinFilter.BilinearNearestMipMap);
        texture.setMagFilter(MagFilter.Bilinear);

        material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        material.getAdditionalRenderState().setBlendMode(BlendMode.AlphaAdditive);
        material.getAdditionalRenderState().setDepthTest(false);
        material.setTexture("Texture", texture);

        testMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        testMat.setColor("Color", ColorRGBA.Blue);
        testMat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        testMat.getAdditionalRenderState().setWireframe(true);
    }

    public ParticleDataMeshInfo getParticleMeshType() {
        return new ParticleDataMeshInfo(particleDataMeshType, particleMeshTemplate);
    }

    /**
     * Change the particles data mesh in this emitter.
     *
     * @param info information about new settings.
     */
    public <T extends ParticleDataMesh> void changeParticleMeshType(@NotNull final ParticleDataMeshInfo info) {
        changeParticleMeshType(info.getMeshType(), info.getTemplate());
    }

    /**
     * Change the particles data mesh in this emitter.
     *
     * @param type     the type of the particles data mesh.
     * @param template the particleMeshTemplate of the mesh of the particles, can be null.
     */
    public <T extends ParticleDataMesh> void changeParticleMeshType(@NotNull final Class<T> type, @Nullable final Mesh template) {
        this.particleDataMeshType = type;
        this.particleMeshTemplate = template;
        this.particleDataMesh = ClassUtils.newInstance(type);

        if (template != null) {
            this.particleDataMesh.extractTemplateFromMesh(template);
        }

        particleGeometry.setMesh(particleDataMesh);
        particleTestGeometry.setMesh(particleDataMesh);

        if (!emitterInitialized) return;
        if (enabled) killAllParticles();

        initParticles();

        if (enabled) emitAllParticles();
    }

    private <T extends ParticleDataMesh> void initParticles(@NotNull final Class<T> type, @Nullable final Mesh template) {
        this.particleDataMesh = ClassUtils.newInstance(type);

        if (template != null) {
            this.particleDataMesh.extractTemplateFromMesh(template);
            this.particleMeshTemplate = template;
        }
    }

    protected void initParticles() {
        particles = new ParticleData[maxParticles];

        for (int i = 0; i < maxParticles; i++) {
            particles[i] = new ParticleData();
            particles[i].emitterNode = this;
            particles[i].index = i;
            particles[i].reset();
        }

        particleDataMesh.initParticleData(this, maxParticles);
    }

    /**
     * Sets the particle emitter shape to the specified mesh
     *
     * @param mesh The Mesh to use as the particle emitter shape
     */
    public final void changeEmitterShapeMesh(@NotNull final Mesh mesh) {
        emitterShape.setShape(this, mesh);
        emitterShapeTestGeometry.setMesh(mesh);
        requiresUpdate = true;
    }

    /**
     * Sets the particle emitter shape to the specified mesh NOTE: This method is supplied for use
     * with animated emitter shapes.
     *
     * @param node                             The node containing the Mesh used as the particle
     *                                         emitter shape
     * @param sceneAlreadyContainsEmitterShape Tells the emitter if shape is an asset that is
     *                                         already contained within the scene.  This allows you
     *                                         to manage animations via the asset in place of
     *                                         calling setEmitterAnimation
     */
    @Deprecated
    public final void setEmitterShapeMesh(@NotNull final Node node, final boolean sceneAlreadyContainsEmitterShape) {
        if (emitterAnimNode != null) emitterAnimNode.removeFromParent();

        emitterAnimNode = node;
        emitterNodeExists = sceneAlreadyContainsEmitterShape;
        if (!emitterNodeExists) emitterAnimNode.setLocalScale(0);

        final Mesh shape = ((Geometry) node.getChild(0)).getMesh();
        changeEmitterShapeMesh(shape);

        emitterAnimControl = emitterAnimNode.getControl(AnimControl.class);

        if (emitterAnimControl != null) {
            emitterAnimChannel = emitterAnimControl.createChannel();
        }

        if (emitterInitialized) {
            attachChild(emitterAnimNode);
        }
    }

    /**
     * Returns the current ParticleData Emitter's EmitterMesh
     *
     * @return The EmitterMesh containing the specified shape Mesh
     */
    @NotNull
    public EmitterMesh getEmitterShape() {
        return emitterShape;
    }

    /**
     * Returns if the current emitter shape has an associated animation Control
     */
    @Deprecated
    public boolean getIsShapeAnimated() {
        return !(emitterAnimControl == null);
    }

    /**
     * Called to set the emitter shape's animation IF the emitter shape is not pointing to a scene
     * asset
     *
     * @param emitterAnimName      The String name of the animation
     * @param emitterAnimSpeed     The speed at which the animation should run
     * @param emitterAnimBlendTime The blend time to use when switching animations
     */
    @Deprecated
    public void setEmitterAnimation(@NotNull final String emitterAnimName, final float emitterAnimSpeed, final float emitterAnimBlendTime, @NotNull final LoopMode emitterAnimLoopMode) {
        this.emitterAnimName = emitterAnimName;
        this.emitterAnimSpeed = emitterAnimSpeed;
        this.emitterAnimBlendTime = emitterAnimBlendTime;
        this.emitterAnimLoopMode = emitterAnimLoopMode;

        if (emitterInitialized && emitterAnimControl != null) {
            emitterAnimChannel.setAnim(emitterAnimName, emitterAnimBlendTime);
            emitterAnimChannel.setSpeed(emitterAnimSpeed);
            emitterAnimChannel.setLoopMode(emitterAnimLoopMode);
        }
    }

    /**
     * Specifies the number of times the particle emitter will emit particles over the course of one
     * second
     *
     * @param emissionsPerSecond The number of particle emissions per second
     */
    public void setEmissionsPerSecond(final int emissionsPerSecond) {
        this.emissionsPerSecond = emissionsPerSecond;
        targetInterval = 1f / emissionsPerSecond;
        requiresUpdate = true;
    }

    /**
     * Return the number of times the particle emitter will emit particles over the course of one
     * second
     */
    public int getEmissionsPerSecond() {
        return emissionsPerSecond;
    }

    /**
     * Specifies the number of particles to be emitted per emission.
     *
     * @param particlesPerEmission The number of particle to emit per emission
     */
    public void setParticlesPerEmission(final int particlesPerEmission) {
        this.particlesPerEmission = particlesPerEmission;
        requiresUpdate = true;
    }

    /**
     * Returns the number of particles to be emitted per emission.
     */
    public int getParticlesPerEmission() {
        return particlesPerEmission;
    }

    /**
     * Defines how particles are emitted from the face of the emitter shape. For example: NORMAL
     * will emit in the direction of the face's normal NORMAL_NEGATE will emit the the opposite
     * direction of the face's normal RANDOM_TANGENT will select a random tagent to the face's
     * normal.
     */
    public void setDirectionType(@NotNull final EmitterMesh.DirectionType directionType) {
        this.directionType = directionType;
    }

    /**
     * Returns the direction in which the particles will be emitted relative to the emitter shape's
     * selected face.
     */
    @NotNull
    public EmitterMesh.DirectionType getDirectionType() {
        return directionType;
    }

    public void setTargetFPS(final float fps) {
        tpfThreshold = 1f / fps;
    }

    /**
     * Particles are created as staticly placed, with no velocity.  Particles set to static with
     * remain in place and follow the emitter shape's animations.
     */
    public void setStaticParticles(final boolean useStaticParticles) {
        this.staticParticles = useStaticParticles;
        requiresUpdate = true;
    }

    /**
     * Returns if particles are flagged as static
     *
     * @return Current state of static particle flag
     */
    public boolean isStaticParticles() {
        return staticParticles;
    }

    /**
     * Enable or disable to use of particle stretching
     */
    public void setVelocityStretching(final boolean useVelocityStretching) {
        this.velocityStretching = useVelocityStretching;
        requiresUpdate = true;
    }

    /**
     * Returns if the emitter will use particle stretching
     */
    public boolean isVelocityStretching() {
        return velocityStretching;
    }

    /**
     * Sets the magnitude of the particle stretch
     */
    public void setVelocityStretchFactor(final float velocityStretchFactor) {
        this.velocityStretchFactor = velocityStretchFactor;
        requiresUpdate = true;
    }

    /**
     * Gets the magnitude of the particle stretching
     */
    public float getVelocityStretchFactor() {
        return velocityStretchFactor;
    }

    /**
     * Forces the stretch to occure along the specified axis relative to the particle's velocity
     *
     * @param axis The axis to stretch against.  Default is Y
     */
    public void setForcedStretchAxis(@NotNull final ForcedStretchAxis axis) {
        this.stretchAxis = axis;
        requiresUpdate = true;
    }

    /**
     * Returns the axis to stretch particles against.  Axis is relative to the particles velocity.
     */
    @NotNull
    public ForcedStretchAxis getForcedStretchAxis() {
        return stretchAxis;
    }

    /**
     * Determine how the particle is placed when first emitted.  The default is the particles 0,0,0
     * point
     */
    public void setParticleEmissionPoint(@NotNull final ParticleEmissionPoint particleEmissionPoint) {
        this.particleEmissionPoint = particleEmissionPoint;
        requiresUpdate = true;
    }

    /**
     * Returns how the particle is placed when first emitted.
     */
    @NotNull
    public ParticleEmissionPoint getParticleEmissionPoint() {
        return particleEmissionPoint;
    }

    /**
     * Particles are effected by updates to the translation of the emitter node.  This option is set
     * to false by default
     *
     * @param particlesFollowEmitter Particles should/should not update according to the emitter
     *                               node's translation updates
     */
    public void setParticlesFollowEmitter(final boolean particlesFollowEmitter) {
        this.particlesFollowEmitter = particlesFollowEmitter;
        requiresUpdate = true;
    }

    /**
     * Returns if the particles are set to update according to the emitter node's translation
     * updates
     *
     * @return Current state of the follows emitter flag
     */
    public boolean isParticlesFollowEmitter() {
        return particlesFollowEmitter;
    }

    /**
     * By default, emission happens from the direct center of the selected emitter shape face.  This
     * flag enables selecting a random point of emission within the selected face.
     */
    public void setRandomEmissionPoint(final boolean useRandomEmissionPoint) {
        this.randomEmissionPoint = useRandomEmissionPoint;
        requiresUpdate = true;
    }

    /**
     * Returns if particle emission uses a randomly selected point on the emitter shape's selected
     * face or it's absolute center.  Center emission is default.
     */
    public boolean isRandomEmissionPoint() {
        return randomEmissionPoint;
    }

    /**
     * For use with emitter shapes that contain more than one face. By default, the face selected
     * for emission is random.  Use this to enforce emission in the sequential order the faces are
     * created in the emitter shape mesh.
     */
    public void setSequentialEmissionFace(final boolean useSequentialEmissionFace) {
        this.sequentialEmissionFace = useSequentialEmissionFace;
        requiresUpdate = true;
    }

    /**
     * Returns if emission happens in the sequential order the faces of the emitter shape mesh are
     * defined.
     */
    public boolean isSequentialEmissionFace() {
        return sequentialEmissionFace;
    }

    /**
     * Enabling skip pattern will use every other face in the emitter shape.  This stops the
     * clustering of two particles per quad that makes up the the emitter shape.
     */
    public void setSequentialSkipPattern(final boolean useSequentialSkipPattern) {
        this.sequentialSkipPattern = useSequentialSkipPattern;
        requiresUpdate = true;
    }

    /**
     * Returns if the emitter will skip every other face in the sequential order the emitter shape
     * faces are defined.
     */
    public boolean isSequentialSkipPattern() {
        return sequentialSkipPattern;
    }

    /**
     * Sets the default interpolation for the emitter will use
     */
    public void setInterpolation(@NotNull final Interpolation interpolation) {
        this.interpolation = interpolation;
        requiresUpdate = true;
    }

    /**
     * Returns the default interpolation used by the emitter
     */
    @NotNull
    public Interpolation getInterpolation() {
        return interpolation;
    }

    /**
     * Sets the inner and outter bounds of the time a particle will remain alive (active)
     *
     * @param lifeMin The minimum time a particle must remian alive once emitted
     * @param lifeMax The maximum time a particle can remain alive once emitted
     */
    public void setLifeMinMax(final float lifeMin, final float lifeMax) {
        this.lifeMin = lifeMin;
        this.lifeMax = lifeMax;
        requiresUpdate = true;
    }

    /**
     * Sets the inner and outter bounds of the time a particle will remain alive (active).
     *
     * @param life the minimum and maximum time a particle must remian alive once emitted.
     */
    public void setLifeMinMax(final Vector2f life) {
        this.lifeMin = life.getX();
        this.lifeMax = life.getY();
        requiresUpdate = true;
    }

    /**
     * Sets the inner and outter bounds of the time a particle will remain alive (active) to a fixed
     * duration of time
     *
     * @param life The fixed duration an emitted particle will remain alive
     */
    public void setLife(final float life) {
        this.lifeMin = life;
        this.lifeMax = life;
        requiresUpdate = true;
    }

    /**
     * Sets the outter bounds of the time a particle will remain alive (active)
     *
     * @param lifeMax The maximum time a particle can remain alive once emitted
     */
    public void setLifeMax(final float lifeMax) {
        this.lifeMax = lifeMax;
        requiresUpdate = true;
    }

    /**
     * Returns the maximum time a particle can remain alive once emitted.
     *
     * @return The maximum time a particle can remain alive once emitted
     */
    public float getLifeMax() {
        return lifeMax;
    }

    /**
     * Returns the minimum and maximum time a particle can remain alive once emitted.
     *
     * @return the minimum and maximum time a particle can remain alive once emitted.
     */
    @NotNull
    public Vector2f getLifeMinMax() {
        return new Vector2f(lifeMin, lifeMax);
    }

    /**
     * Sets the inner bounds of the time a particle will remain alive (active)
     *
     * @param lifeMin The minimum time a particle must remian alive once emitted
     */
    public void setLifeMin(final float lifeMin) {
        this.lifeMin = lifeMin;
        requiresUpdate = true;
    }

    /**
     * Returns the minimum time a particle must remian alive once emitted
     *
     * @return The minimum time a particle must remian alive once emitted
     */
    public float getLifeMin() {
        return lifeMin;
    }

    /**
     * Sets the inner and outter bounds of the initial force with which the particle is emitted.
     * This directly effects the initial velocity vector of the particle.
     *
     * @param forceMin The minimum force with which the particle will be emitted
     * @param forceMax The maximum force with which the particle can be emitted
     */
    public void setForceMinMax(final float forceMin, final float forceMax) {
        this.forceMin = forceMin;
        this.forceMax = forceMax;
        requiresUpdate = true;
    }

    /**
     * Sets the inner and outter bounds of the initial force with which the particle is emitted.
     * This directly effects the initial velocity vector of the particle.
     *
     * @param force The minimum and maximum force with which the particle will be emitted.
     */
    public void setForceMinMax(final Vector2f force) {
        this.forceMin = force.getX();
        this.forceMax = force.getY();
        requiresUpdate = true;
    }

    /**
     * Sets the inner and outter bounds of the initial force with which the particle is emitted to a
     * fixed ammount.  This directly effects the initial velocity vector of the particle.
     *
     * @param force The force with which the particle will be emitted
     */
    public void setForce(final float force) {
        this.forceMin = force;
        this.forceMax = force;
        requiresUpdate = true;
    }

    /**
     * Sets the inner bounds of the initial force with which the particle is emitted.  This directly
     * effects the initial velocity vector of the particle.
     *
     * @param forceMin The minimum force with which the particle will be emitted
     */
    public void setForceMin(final float forceMin) {
        this.forceMin = forceMin;
        requiresUpdate = true;
    }

    /**
     * Sets the outter bounds of the initial force with which the particle is emitted.  This
     * directly effects the initial velocity vector of the particle.
     *
     * @param forceMax The maximum force with which the particle can be emitted
     */
    public void setForceMax(final float forceMax) {
        this.forceMax = forceMax;
        requiresUpdate = true;
    }

    /**
     * Returns the minimum force with which the particle will be emitted
     *
     * @return The minimum force with which the particle will be emitted
     */
    public float getForceMin() {
        return forceMin;
    }

    /**
     * Returns the maximum force with which the particle can be emitted
     *
     * @return The maximum force with which the particle can be emitted
     */
    public float getForceMax() {
        return forceMax;
    }

    /**
     * Returns the minimum and maximum force with which the particle can be emitted.
     *
     * @return the minimum and maximum force with which the particle can be emitted.
     */
    @NotNull
    public Vector2f getForceMinMax() {
        return new Vector2f(forceMin, forceMax);
    }

    /**
     * Returns the maximum number of particles managed by the emitter
     */
    public int getMaxParticles() {
        return maxParticles;
    }

    /**
     * Adds a series of influencers
     *
     * @param influencers The list of influencers
     */
    public void addInfluencers(@NotNull final ParticleInfluencer... influencers) {
        final UnsafeArray<ParticleInfluencer> unsafe = this.influencers.asUnsafe();
        unsafe.addAll(influencers);
        unsafe.trimToSize();
    }

    /**
     * Adds a new ParticleData Influencer to the chain of influencers that will effect particles
     *
     * @param influencer The particle influencer to add to the chain
     */
    public void addInfluencer(@NotNull final ParticleInfluencer influencer) {
        final UnsafeArray<ParticleInfluencer> unsafe = influencers.asUnsafe();
        unsafe.add(influencer);
        unsafe.trimToSize();
        requiresUpdate = true;
    }

    /**
     * Adds a new {@link ParticleInfluencer} to the chain of influencers that will effect particles
     *
     * @param influencer the particle influencer to add to the chain.
     * @param index      the index of the position of this influencer.
     */
    public void addInfluencer(@NotNull final ParticleInfluencer influencer, final int index) {

        final Array<ParticleInfluencer> temp = ArrayFactory.newArray(ParticleInfluencer.class, influencers.size() + 1);

        for (int i = 0; i < index; i++) {
            temp.add(influencers.get(i));
        }

        temp.add(influencer);

        for (int i = index, length = influencers.size(); i < length; i++) {
            temp.add(influencers.get(i));
        }

        final UnsafeArray<ParticleInfluencer> unsafe = influencers.asUnsafe();
        unsafe.clear();
        unsafe.addAll(temp);
        unsafe.trimToSize();

        requiresUpdate = true;
    }

    /**
     * Get the index of the influencer in the list of the influencers of this emitter.
     *
     * @param influencer the influencer for getting its position index.
     * @return the index of position for the influencer.
     */
    public int indexOfInfluencer(@NotNull final ParticleInfluencer influencer) {
        return influencers.indexOf(influencer);
    }

    /**
     * Removes the influencer rom this emitter.
     *
     * @param influencer the influencer to remove.
     */
    public void removeInfluencer(@NotNull ParticleInfluencer influencer) {
        final UnsafeArray<ParticleInfluencer> unsafe = influencers.asUnsafe();
        unsafe.slowRemove(influencer);
        unsafe.trimToSize();
        requiresUpdate = true;
    }

    /**
     * Returns the current chain of particle influencers
     *
     * @return The Collection of particle influencers
     */
    @NotNull
    public ParticleInfluencer[] getInfluencers() {
        return influencers.array();
    }

    /**
     * Returns the first instance of a specified ParticleData Influencer type
     */
    @Nullable
    public <T extends ParticleInfluencer> T getInfluencer(@NotNull final Class<T> type) {
        for (final ParticleInfluencer pi : influencers.array()) {
            if (pi.getClass() == type) return unsafeCast(pi);
        }
        return null;
    }

    /**
     * Removes the specified influencer by class
     *
     * @param type The class of the influencer to remove
     */
    public void removeInfluencer(@NotNull final Class<?> type) {
        for (final ParticleInfluencer pi : influencers.array()) {
            if (pi.getClass() == type) {
                influencers.fastRemove(pi);
                break;
            }
        }
        requiresUpdate = true;
    }

    /**
     * Removes all influencers
     */
    public void removeAllInfluencers() {
        influencers.clear();
        requiresUpdate = true;
    }

    /**
     * Change the current texture to the new texture.
     *
     * @param texturePath the path to texture.
     */
    public void changeTexture(final String texturePath) {

        final Texture texture = assetManager.loadTexture(texturePath);
        texture.setMinFilter(MinFilter.BilinearNearestMipMap);
        texture.setMagFilter(MagFilter.Bilinear);

        material.setTexture(textureParamName, texture);

        setSpriteCount(spriteCols, spriteRows);
    }

    /**
     * Change the current texture to the new texture.
     *
     * @param texture the new texture.
     */
    public void changeTexture(final Texture texture) {

        texture.setMinFilter(MinFilter.BilinearNearestMipMap);
        texture.setMagFilter(MagFilter.Bilinear);

        material.setTexture(textureParamName, texture);

        setSpriteCount(spriteCols, spriteRows);
    }

    /**
     * Sets the count of columns and rows in the current texture for splitting for sprites.
     *
     * @param spriteCount The number of rows and columns containing sprite images.
     */
    public void setSpriteCount(final Vector2f spriteCount) {
        setSpriteCount((int) spriteCount.getX(), (int) spriteCount.getY());
    }

    /**
     * Sets the count of columns and rows in the current texture for splitting for sprites.
     *
     * @param spriteCols The number of columns containing sprite images.
     * @param spriteRows The number of rows containing sprite images.
     */
    public void setSpriteCount(final int spriteCols, final int spriteRows) {

        if (spriteCols < 1 || spriteRows < 1) {
            throw new IllegalArgumentException("the values " + spriteCols + "-" + spriteRows + " can't be less than 1.");
        }

        this.spriteCols = spriteCols;
        this.spriteRows = spriteRows;
        if (!emitterInitialized) return;

        final MatParamTexture textureParam = material.getTextureParam(textureParamName);
        final Texture texture = textureParam.getTextureValue();

        final Image textureImage = texture.getImage();
        final int width = textureImage.getWidth();
        final int height = textureImage.getHeight();

        spriteWidth = width / spriteCols;
        spriteHeight = height / spriteRows;

        particleDataMesh.setImagesXY(spriteCols, spriteRows);

        requiresUpdate = true;
    }

    public Vector2f getSpriteCount() {
        return new Vector2f(spriteCols, spriteRows);
    }

    /**
     * Returns the current material used by the emitter.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Set new material for these particles.
     *
     * @param material the new material.
     */
    public void setParticlesMaterial(@NotNull final ParticlesMaterial material) {
        setMaterial(material.getMaterial(), material.getTextureParam(), material.isApplyLightingTransform());
    }

    /**
     * @return the current material of these particles.
     */
    @NotNull
    public ParticlesMaterial getParticlesMaterial() {
        return new ParticlesMaterial(material, textureParamName, applyLightingTransform);
    }

    /**
     * Can be used to override the default Particle material.
     *
     * @param material               The material
     * @param textureParamName       The material uniform name used for applying a color map (ex:
     *                               Texture, ColorMap, DiffuseMap)
     * @param applyLightingTransform Forces update of normals and should only be used if the emitter
     *                               material uses a lighting shader
     */
    public void setMaterial(final Material material, final String textureParamName, final boolean applyLightingTransform) {
        this.material = material;
        this.applyLightingTransform = applyLightingTransform;
        this.textureParamName = textureParamName;

        if (emitterInitialized) {
            final MatParamTexture textureParam = material.getTextureParam(textureParamName);
            final Texture texture = textureParam.getTextureValue();
            texture.setMinFilter(MinFilter.BilinearNearestMipMap);
            texture.setMagFilter(MagFilter.Bilinear);
        }

        particleNode.setMaterial(material);
        requiresUpdate = true;
    }

    /**
     * Returns the number of columns of sprite images in the specified texture
     *
     * @return The number of available sprite columns
     */
    public int getSpriteColCount() {
        return spriteCols;
    }

    /**
     * Returns the number of rows of sprite images in the specified texture
     *
     * @return The number of available sprite rows
     */
    public int getSpriteRowCount() {
        return spriteRows;
    }

    /**
     * Returns if the emitter will update normals for lighting materials
     */
    public boolean getApplyLightingTransform() {
        return applyLightingTransform;
    }

    /**
     * Sets the billboard mode to be used by emitted particles.  The default mode is CAMERA
     *
     * @param billboardMode The billboard mode to use
     */
    public void setBillboardMode(@NotNull final BillboardMode billboardMode) {
        this.billboardMode = billboardMode;
        requiresUpdate = true;
    }

    /**
     * Returns the current selected BillboardMode used by emitted particles
     *
     * @return The current selected BillboardMode
     */
    @NotNull
    public BillboardMode getBillboardMode() {
        return billboardMode;
    }

    /**
     * Enables the particle emitter.  The emitter is disabled by default. Enabling the emitter will
     * actively call the update loop each frame. The emitter should remain disabled if you are using
     * the emitter to produce static meshes.
     *
     * @param enabled Activate/deactivate the emitter
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns if the emitter is actively calling update.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Initializes the emitter, materials & particle mesh Must be called prior to adding the control
     * to your scene.
     */
    protected void initialize(@NotNull final AssetManager assetManager, boolean needInitMaterials, boolean needInitMesh) {
        if (emitterInitialized) return;

        this.assetManager = assetManager;

        if (needInitMaterials) initMaterials();
        if (needInitMesh) initParticles(particleDataMeshType, particleMeshTemplate);

        initParticles();

        particleDataMesh.setImagesXY(spriteCols, spriteRows);

        final MatParamTexture textureParam = material.getTextureParam(textureParamName);
        final Texture texture = textureParam.getTextureValue();

        final Image img = texture.getImage();
        final int width = img.getWidth();
        final int height = img.getHeight();

        spriteWidth = width / spriteCols;
        spriteHeight = height / spriteRows;

        if (emitterAnimControl != null) {
            if (!emitterAnimName.equals("")) {
                emitterAnimChannel.setAnim(emitterAnimName, emitterAnimBlendTime);
                emitterAnimChannel.setSpeed(emitterAnimSpeed);
            }
        }

        if (particlesAnimControl != null) {
            if (!particlesAnimName.equals("")) {
                particlesAnimChannel.setAnim(particlesAnimName, particlesAnimBlendTime);
                particlesAnimChannel.setSpeed(particlesAnimSpeed);
            }
        }

        particleGeometry.setMesh(particleDataMesh);
        particleTestGeometry.setMesh(particleDataMesh);
        emitterShapeTestGeometry.setMesh(emitterShape.getMesh());

        particleNode.setMaterial(material);
        particleTestNode.setMaterial(testMat);
        emitterTestNode.setMaterial(testMat);

        emitterInitialized = true;
    }

    /**
     * @return true if the test emitter is enabled.
     */
    public boolean isEnabledTestEmitter() {
        return testEmitter;
    }

    /**
     * @return true if the test particles is enabled.
     */
    public boolean isEnabledTestParticles() {
        return testParticles;
    }

    /**
     * @param testEmitter the flag of enabling test emitter.
     */
    public void setEnabledTestEmitter(final boolean testEmitter) {
        if (isEnabledTestEmitter() == testEmitter) return;
        this.testEmitter = testEmitter;
        if (testEmitter) attachChild(emitterTestNode);
        else emitterTestNode.removeFromParent();
        requiresUpdate = true;
    }

    /**
     * @param testParticles the flag of enabling test particles.
     */
    public void setEnabledTestParticles(final boolean testParticles) {
        if (isEnabledTestParticles() == testParticles) return;
        this.testParticles = testParticles;
        if (testParticles) particleNode.attachChild(particleTestNode);
        else particleTestNode.removeFromParent();
        requiresUpdate = true;
    }

    @NotNull
    public TestParticleEmitterNode getParticleTestNode() {
        return particleTestNode;
    }

    @NotNull
    public TestParticleEmitterNode getEmitterTestNode() {
        return emitterTestNode;
    }

    @Override
    public void updateGeometricState() {

        if (emitterInitialized && (enabled || postRequiresUpdate)) {
            particleGeometry.updateModelBound();
            if (testParticles) {
                particleTestGeometry.updateModelBound();
            }
            postRequiresUpdate = false;
        }

        super.updateGeometricState();
    }

    @Override
    public void updateLogicalState(final float tpf) {
        super.updateLogicalState(tpf);

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

        } else {
            currentInterval = 0;
        }
    }

    private int calcParticlesPerEmission() {
        return (int) (currentInterval / targetInterval * particlesPerEmission);
    }

    /**
     * Emits the next available (non-active) particle
     */
    public void emitNextParticle() {
        if (nextIndex == -1 || nextIndex >= maxParticles) return;

        particles[nextIndex].initialize();

        int searchIndex = nextIndex;
        int initIndex = nextIndex;
        int loop = 0;

        while (particles[searchIndex].active) {
            searchIndex++;
            if (searchIndex > particles.length - 1) {
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

    /**
     * Emits all non-active particles
     */
    public void emitAllParticles() {
        for (final ParticleData data : particles) {
            if (!data.active) data.initialize();
        }
        requiresUpdate = true;
    }

    /**
     * Emits the specified number of particles
     *
     * @param count The number of particles to emit.
     */
    public void emitNumParticles(int count) {

        int counter = 0;

        for (final ParticleData data : particles) {
            if (!data.active && counter < count) {
                data.initialize();
                counter++;
            }
            if (counter > count) break;
        }

        requiresUpdate = true;
    }

    /**
     * Clears all current particles, setting them to inactive
     */
    public void killAllParticles() {
        for (final ParticleData data : particles) {
            data.reset();
        }
        requiresUpdate = true;
    }

    /**
     * Deactivates and resets the specified particle
     *
     * @param toKill The particle to reset
     */
    public void killParticle(@NotNull final ParticleData toKill) {
        for (final ParticleData data : particles) {
            if (data == toKill) toKill.reset();
        }
        requiresUpdate = true;
    }

    /**
     * Returns the number of active particles
     */
    public int getActiveParticleCount() {
        return activeParticleCount;
    }

    /**
     * DO NOT CALL - For internal use.
     */
    public void incActiveParticleCount() {
        activeParticleCount++;
    }

    /**
     * DO NOT CALL - For internal use.
     */
    public void decActiveParticleCount() {
        activeParticleCount--;
    }

    /**
     * Deactivates and resets the specified particle
     *
     * @param index The index of the particle to reset
     */
    public void killParticle(int index) {
        particles[index].reset();
        requiresUpdate = true;
    }

    /**
     * Resets all particle data and the current emission interval
     */
    public void reset() {
        killAllParticles();
        currentInterval = 0;
        requiresUpdate = true;
    }

    /**
     * Resets the current emission interval
     */
    public void resetInterval() {
        currentInterval = 0;
    }

    /**
     * This method should not be called.  Particles call this method to help track the next
     * available particle index
     *
     * @param index The index of the particle that was just reset
     */
    public void setNextIndex(int index) {
        if (index >= nextIndex && nextIndex != -1) return;
        nextIndex = index;
    }

    @Override
    public void runControlRender(final RenderManager rm, final ViewPort vp) {
        super.runControlRender(rm, vp);
        if (!emitterInitialized || (!enabled && !requiresUpdate)) return;

        Camera cam = vp.getCamera();

        if (particleDataMesh.getClass() == ParticleDataPointMesh.class) {
            float C = cam.getProjectionMatrix().m00;
            C *= cam.getWidth() * 0.5f;

            // send attenuation params
            material.setFloat("Quadratic", C);
        }
        particleDataMesh.updateParticleData(particles, cam, inverseRotation);
        if (requiresUpdate) {
            requiresUpdate = false;
            postRequiresUpdate = true;
        }
    }

    @Override
    public void write(@NotNull final JmeExporter ex) throws IOException {

        final int childIndex = getChildIndex(particleNode);
        detachChild(particleNode);

        super.write(ex);

        attachChildAt(particleNode, childIndex);

        final OutputCapsule capsule = ex.getCapsule(this);
        final ArrayList<ParticleInfluencer> writeInfluencers = new ArrayList<>(influencers.size());

        Collections.addAll(writeInfluencers, influencers.array());

        capsule.writeSavableArrayList(writeInfluencers, "influencers", EMPTY_INFLUENCERS);
        capsule.write(enabled, "enabled", true);

        // EMITTER
        capsule.write(emitterShape, "emitterShape", null);
        capsule.write(emissionsPerSecond, "emissionsPerSecond", 0);
        capsule.write(particlesPerEmission, "particlesPerEmission", 0);
        capsule.write(staticParticles, "staticParticles", false);
        capsule.write(randomEmissionPoint, "randomEmissionPoint", false);
        capsule.write(sequentialEmissionFace, "sequentialEmissionFace", false);
        capsule.write(sequentialSkipPattern, "sequentialSkipPattern", false);
        capsule.write(velocityStretching, "velocityStretching", false);
        capsule.write(velocityStretchFactor, "velocityStretchFactor", 0);
        capsule.write(stretchAxis.ordinal(), "stretchAxis", 0);
        capsule.write(particleEmissionPoint.ordinal(), "particleEmissionPoint", 0);
        capsule.write(directionType.ordinal(), "directionType", 0);

        // PARTICLES
        capsule.write(billboardMode.ordinal(), "billboardMode", 0);
        capsule.write(particlesFollowEmitter, "particlesFollowEmitter", false);

        // PARTICLES MESH DATA
        capsule.write(particleDataMeshType.getName(), "particleDataMeshType", ParticleDataTriMesh.class.getName());
        capsule.write(particleMeshTemplate, "particleMeshTemplate", null);
        capsule.write(maxParticles, "maxParticles", 0);
        capsule.write(forceMin, "forceMin", 0);
        capsule.write(forceMax, "forceMax", 0);
        capsule.write(lifeMin, "lifeMin", 0);
        capsule.write(lifeMax, "lifeMax", 0);
        capsule.write(interpolation, "interpolation", Interpolation.linear);

        // MATERIALS
        capsule.write(textureParamName, "textureParamName", null);
        capsule.write(material, "material", null);
        capsule.write(applyLightingTransform, "applyLightingTransform", false);
        capsule.write(spriteCols, "spriteCols", 0);
        capsule.write(spriteRows, "spriteRows", 0);
    }

    @Override
    public void read(@NotNull final JmeImporter im) throws IOException {

        final int particleIndex = getChildIndex(particleNode);
        final int testIndex = getChildIndex(emitterTestNode);
        detachChild(particleNode);

        if (testIndex != -1) detachChild(emitterTestNode);

        super.read(im);

        attachChildAt(particleNode, particleIndex);
        if (testIndex != -1) attachChildAt(emitterTestNode, testIndex);

        final InputCapsule capsule = im.getCapsule(this);
        final ArrayList<ParticleInfluencer> readInfluencers = capsule.readSavableArrayList("influencers", EMPTY_INFLUENCERS);

        this.enabled = capsule.readBoolean("enabled", true);

        // EMITTER
        emitterShape = (EmitterMesh) capsule.readSavable("emitterShape", null);
        emitterShape.setEmitterNode(this);

        setEmissionsPerSecond(capsule.readInt("emissionsPerSecond", 0));
        setParticlesPerEmission(capsule.readInt("particlesPerEmission", 0));
        setStaticParticles(capsule.readBoolean("staticParticles", false));
        setRandomEmissionPoint(capsule.readBoolean("randomEmissionPoint", false));
        setSequentialEmissionFace(capsule.readBoolean("sequentialEmissionFace", false));
        setSequentialSkipPattern(capsule.readBoolean("sequentialSkipPattern", false));
        setVelocityStretching(capsule.readBoolean("velocityStretching", false));
        setVelocityStretchFactor(capsule.readFloat("velocityStretchFactor", 0F));
        setForcedStretchAxis(ForcedStretchAxis.valueOf(capsule.readInt("stretchAxis", 0)));
        setParticleEmissionPoint(ParticleEmissionPoint.valueOf(capsule.readInt("particleEmissionPoint", 0)));
        setDirectionType(EmitterMesh.DirectionType.valueOf(capsule.readInt("directionType", 0)));

        // PARTICLES
        setBillboardMode(BillboardMode.valueOf(capsule.readInt("billboardMode", 0)));
        setParticlesFollowEmitter(capsule.readBoolean("particlesFollowEmitter", false));

        // PARTICLES MESH DATA
        final Class<? extends ParticleDataMesh> meshType = Util.safeGet(capsule, first ->
                unsafeCast(forName(first.readString("particleDataMeshType", ParticleDataTriMesh.class.getName()))));
        final Mesh template = (Mesh) capsule.readSavable("particleMeshTemplate", null);

        changeParticleMeshType(meshType, template);
        setMaxParticles(capsule.readInt("maxParticles", 0));
        setForceMinMax(capsule.readFloat("forceMin", 0F), capsule.readFloat("forceMax", 0F));
        setLifeMinMax(capsule.readFloat("lifeMin", 0F), capsule.readFloat("lifeMax", 0F));
        setInterpolation((Interpolation) capsule.readSavable("interpolation", Interpolation.linear));

        // MATERIALS
        final String textureParamName = capsule.readString("textureParamName", null);
        final Material material = (Material) capsule.readSavable("material", null);
        final boolean applyLightingTransform = capsule.readBoolean("applyLightingTransform", false);

        setMaterial(material, textureParamName, applyLightingTransform);
        setSpriteCount(capsule.readInt("spriteCols", 0), capsule.readInt("spriteRows", 0));

        addInfluencers(readInfluencers.toArray(new ParticleInfluencer[readInfluencers.size()]));
        initialize(im.getAssetManager(), true, true);
    }

    @Override
    public ParticleEmitterNode jmeClone() {
        return (ParticleEmitterNode) super.jmeClone();
    }

    @Override
    public void cloneFields(final Cloner cloner, final Object original) {
        super.cloneFields(cloner, original);

        particles = cloner.clone(particles);
    }

    @Override
    public void setLocalTranslation(final Vector3f translation) {
        super.setLocalTranslation(translation);
        requiresUpdate = true;
    }

    @Override
    public void setLocalTranslation(float x, float y, float z) {
        super.setLocalTranslation(x, y, z);
        requiresUpdate = true;
    }

    @Override
    public void setLocalRotation(Quaternion q) {
        super.setLocalRotation(q);
        requiresUpdate = true;
    }

    @Override
    public void setLocalRotation(Matrix3f m) {
        super.setLocalRotation(m);
        requiresUpdate = true;
    }

    @Override
    public void setLocalScale(Vector3f scale) {
        super.setLocalScale(scale);
        requiresUpdate = true;
    }

    @Override
    public void setLocalScale(float scale) {
        super.setLocalScale(scale);
        requiresUpdate = true;
    }

    @Override
    public void setLocalScale(float x, float y, float z) {
        super.setLocalScale(x, y, z);
        requiresUpdate = true;
    }
}
