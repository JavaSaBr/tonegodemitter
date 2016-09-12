package tonegod.emitter;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
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
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import rlib.util.ClassUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.UnsafeArray;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;
import tonegod.emitter.particle.ParticleDataMesh;
import tonegod.emitter.particle.ParticleDataPointMesh;
import tonegod.emitter.particle.ParticleDataTriMesh;
import tonegod.emitter.shapes.TriangleEmitterShape;

import static rlib.util.ClassUtils.unsafeCast;
import static rlib.util.array.ArrayFactory.newArray;

/**
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class ParticleEmitterNode extends Node implements JmeCloneable, Cloneable {

    public enum BillboardMode {
        /**
         * Facing direction follows the velocity as it changes
         */
        VELOCITY,
        /**
         * Facing direction follows the velocity as it changes, Y of particle always faces Z of
         * velocity
         */
        VELOCITY_Z_UP,
        /**
         * Facing direction follows the velocity as it changes, Y of particle always faces Z of
         * velocity, Up of the particle always faces X
         */
        VELOCITY_Z_UP_Y_LEFT,
        /**
         * Facing direction remains constant to the face of the particle emitter shape that the
         * particle was emitted from
         */
        NORMAL,
        /**
         * Facing direction remains constant for X, Z axis' to the face of the particle emitter
         * shape that the particle was emitted from. Y axis maps to UNIT_Y
         */
        NORMAL_Y_UP,
        /**
         * ParticleData always faces camera
         */
        CAMERA,
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
        UNIT_Z;

        private static final BillboardMode[] VALUES = values();

        public static BillboardMode valueOf(final int index) {
            return VALUES[index];
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
        PARTICLE_CENTER,
        PARTICLE_EDGE_TOP,
        PARTICLE_EDGE_BOTTOM;

        private static final ParticleEmissionPoint[] VALUES = values();

        public static ParticleEmissionPoint valueOf(final int index) {
            return VALUES[index];
        }
    }

    protected Array<ParticleInfluencer> influencers;

    protected Mesh template;

    // Emitter info
    protected int nextIndex;

    protected float targetInterval;
    protected float currentInterval;

    protected int emissionsPerSecond;
    protected int totalParticlesThisEmission;
    protected int particlesPerEmission;

    protected float tpfThreshold;

    protected Matrix3f inverseRotation;

    protected boolean useStaticParticles;
    protected boolean useRandomEmissionPoint;
    protected boolean useSequentialEmissionFace;
    protected boolean useSequentialSkipPattern;
    protected boolean useVelocityStretching;

    protected float velocityStretchFactor;

    protected ForcedStretchAxis stretchAxis;
    protected ParticleEmissionPoint particleEmissionPoint;
    protected EmitterMesh.DirectionType directionType;

    protected EmitterMesh emitterShape;

    protected Node emitterTestNode, particleTestNode;

    /**
     * The particle node.
     */
    protected Node particleNode;

    // ParticleData info
    protected ParticleData[] particles;

    protected Class particleType;

    protected ParticleDataMesh mesh;

    protected int activeParticleCount;
    protected int maxParticles;

    protected float forceMax;
    protected float forceMin;

    protected float lifeMin;
    protected float lifeMax;

    protected Interpolation interpolation;

    // Material information
    protected AssetManager assetManager;

    protected Material material;
    protected Material userDefinedMaterial;
    protected Material testMat;

    protected boolean applyLightingTransform;

    protected String uniformName = "Texture";
    protected String texturePath;

    protected Texture texture;

    protected float spriteWidth;
    protected float spriteHeight;

    protected int spriteCols;
    protected int spriteRows;

    protected BillboardMode billboardMode;

    protected boolean particlesFollowEmitter;
    protected boolean enabled;
    protected boolean requiresUpdate;
    protected boolean postRequiresUpdate;

    protected boolean emitterInitialized;

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
    public ParticleEmitterNode(final AssetManager assetManager) {
        this();
        this.assetManager = assetManager;
    }

    /**
     * Creates a new instance of the Emitter
     */
    public ParticleEmitterNode() {
        setName("Emitter Node");
        this.inverseRotation = Matrix3f.IDENTITY.clone();
        this.tpfThreshold = 1f / 400f;
        this.targetInterval = 0.00015f;
        this.currentInterval = 0;
        this.velocityStretchFactor = 0.35f;
        this.stretchAxis = ForcedStretchAxis.Y;
        this.particleEmissionPoint = ParticleEmissionPoint.PARTICLE_CENTER;
        this.directionType = EmitterMesh.DirectionType.Random;
        this.interpolation = Interpolation.linear;
        this.influencers = newArray(ParticleInfluencer.class, 1);
        this.particleType = ParticleDataTriMesh.class;
        this.emitterShape = new EmitterMesh();
        this.emitterTestNode = new Node("EmitterTestNode");
        this.particleTestNode = new Node("ParticleTestNode");
        this.particleNode = new Node("Particle Node");
        this.forceMax = 0.5f;
        this.forceMin = 0.15f;
        this.lifeMin = 0.999f;
        this.lifeMax = 0.999f;
        this.activeParticleCount = 0;
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
    }

    /**
     * @return the particle node.
     */
    public Node getParticleNode() {
        return particleNode;
    }

    /**
     * Sets the mesh class used to create the particle mesh. For example: ParticleDataTriMesh.class
     * - A quad-based particle mesh ParticleDataImpostorMesh.class - A star-shaped impostor mesh
     *
     * @param type The Mesh class used to create the particle Mesh
     */
    public <T extends ParticleDataMesh> void setParticleType(@NotNull final Class<T> type) {
        this.particleType = type;
    }

    /**
     * Sets the mesh class used to create the particle mesh. For example:
     * ParticleDataTemplateMesh.class - Uses a supplied mesh as a template for particles
     *
     * @param type     The Mesh class used to create the particle Mesh
     * @param template The template mesh used to define a single particle
     */
    public <T extends ParticleDataMesh> void setParticleType(@NotNull final Class<T> type, @Nullable final Mesh template) {
        this.particleType = type;
        this.template = template;
    }

    /**
     * Sets the mesh class used to create the particle mesh. For example:
     * ParticleDataTemplateMesh.class - Uses a supplied mesh as a template for particles NOTE: This
     * method is supplied for use with animated particles.
     *
     * @param type     The Mesh class used to create the particle Mesh
     * @param template The Node to extract the template mesh used to define a single particle
     */
    public <T extends ParticleDataMesh> void setParticleType(@NotNull final Class<T> type, @NotNull final Node template) {
        if (particlesAnimNode != null) particlesAnimNode.removeFromParent();

        this.particleType = type;
        this.particlesAnimNode = template;
        this.template = ((Geometry) particlesAnimNode.getChild(0)).getMesh();

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
    public Class getParticleType() {
        return particleType;
    }

    /**
     * Returns the Mesh defined as a template for a single particle
     *
     * @return The Mesh to use as a particle template
     */
    @NotNull
    public Mesh getParticleMeshTemplate() {
        return template;
    }

    @Override
    protected void setParent(@Nullable final Node parent) {
        super.setParent(parent);
        if (parent == null && isEnabled()) setEnabled(false);
    }

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

        material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        testMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        testMat.setColor("Color", ColorRGBA.Blue);
        testMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        testMat.getAdditionalRenderState().setWireframe(true);
    }

    private <T extends ParticleDataMesh> void initParticles(@NotNull final Class<T> type, @Nullable final Mesh template) {
        this.mesh = ClassUtils.newInstance(type);

        if (template != null) {
            this.mesh.extractTemplateFromMesh(template);
            this.template = template;
        }

        initParticles();
    }

    protected void initParticles() {
        particles = new ParticleData[maxParticles];

        for (int i = 0; i < maxParticles; i++) {
            particles[i] = new ParticleData();
            particles[i].emitterNode = this;
            particles[i].index = i;
            particles[i].reset();
        }

        mesh.initParticleData(this, maxParticles);
    }

    /**
     * Creates a single triangle emitter shape
     */
    public void setShapeSimpleEmitter() {

        final TriangleEmitterShape shape = new TriangleEmitterShape();
        shape.init(1F);

        setShape(shape);

        requiresUpdate = true;
    }

    /**
     * Sets the particle emitter shape to the specified mesh
     *
     * @param mesh The Mesh to use as the particle emitter shape
     */
    public final void setShape(@NotNull final Mesh mesh) {
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
    public final void setShape(@NotNull final Node node, final boolean sceneAlreadyContainsEmitterShape) {
        if (emitterAnimNode != null) emitterAnimNode.removeFromParent();

        emitterAnimNode = node;
        emitterNodeExists = sceneAlreadyContainsEmitterShape;
        if (!emitterNodeExists) emitterAnimNode.setLocalScale(0);

        final Mesh shape = ((Geometry) node.getChild(0)).getMesh();
        setShape(shape);

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
    public EmitterMesh getShape() {
        return emitterShape;
    }

    /**
     * Returns if the current emitter shape has an associated animation Control
     */
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
     * will emit in the direction of the face's normal NormalNegate will emit the the opposite
     * direction of the face's normal RandomTangent will select a random tagent to the face's
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
    public void setUseStaticParticles(final boolean useStaticParticles) {
        this.useStaticParticles = useStaticParticles;
        requiresUpdate = true;
    }

    /**
     * Returns if particles are flagged as static
     *
     * @return Current state of static particle flag
     */
    public boolean getUseStaticParticles() {
        return useStaticParticles;
    }

    /**
     * Enable or disable to use of particle stretching
     */
    public void setUseVelocityStretching(final boolean useVelocityStretching) {
        this.useVelocityStretching = useVelocityStretching;
        requiresUpdate = true;
    }

    /**
     * Returns if the emitter will use particle stretching
     */
    public boolean getUseVelocityStretching() {
        return useVelocityStretching;
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
        return this.stretchAxis;
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
    public boolean getParticlesFollowEmitter() {
        return particlesFollowEmitter;
    }

    /**
     * By default, emission happens from the direct center of the selected emitter shape face.  This
     * flag enables selecting a random point of emission within the selected face.
     */
    public void setUseRandomEmissionPoint(final boolean useRandomEmissionPoint) {
        this.useRandomEmissionPoint = useRandomEmissionPoint;
        requiresUpdate = true;
    }

    /**
     * Returns if particle emission uses a randomly selected point on the emitter shape's selected
     * face or it's absolute center.  Center emission is default.
     */
    public boolean isUseRandomEmissionPoint() {
        return useRandomEmissionPoint;
    }

    /**
     * For use with emitter shapes that contain more than one face. By default, the face selected
     * for emission is random.  Use this to enforce emission in the sequential order the faces are
     * created in the emitter shape mesh.
     */
    public void setUseSequentialEmissionFace(final boolean useSequentialEmissionFace) {
        this.useSequentialEmissionFace = useSequentialEmissionFace;
        requiresUpdate = true;
    }

    /**
     * Returns if emission happens in the sequential order the faces of the emitter shape mesh are
     * defined.
     */
    public boolean isUseSequentialEmissionFace() {
        return useSequentialEmissionFace;
    }

    /**
     * Enabling skip pattern will use every other face in the emitter shape.  This stops the
     * clustering of two particles per quad that makes up the the emitter shape.
     */
    public void setUseSequentialSkipPattern(final boolean useSequentialSkipPattern) {
        this.useSequentialSkipPattern = useSequentialSkipPattern;
        requiresUpdate = true;
    }

    /**
     * Returns if the emitter will skip every other face in the sequential order the emitter shape
     * faces are defined.
     */
    public boolean isUseSequentialSkipPattern() {
        return useSequentialSkipPattern;
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

    //<editor-fold desc="Emission Force & Particle Lifespan">

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
    public final void addInfluencer(@NotNull final ParticleInfluencer influencer) {
        final UnsafeArray<ParticleInfluencer> unsafe = influencers.asUnsafe();
        unsafe.add(influencer);
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
            if (pi.getInfluencerClass() == type) return unsafeCast(pi);
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
            if (pi.getInfluencerClass() == type) {
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
    //</editor-fold>

    //<editor-fold desc="Material & Particle Texture">

    /**
     * Sets the texture to be used by particles, when calling this method, it is assumed that the
     * image does not contain multiple sprite images
     *
     * @param texturePath The path of the texture to use
     */
    public void setSprite(final String texturePath) {
        setSpriteByCount(texturePath, uniformName, 1, 1);
    }

    /**
     * Sets the texture to be used by particles, this can contain multiple images for random image
     * selection or sprite animation of particles.
     *
     * @param texturePath The path of the texture to use
     * @param numCols     The number of sprite images per row
     * @param numRows     The number of rows containing sprite images
     */
    public void setSprite(String texturePath, int numCols, int numRows) {
        setSpriteByCount(texturePath, uniformName, numCols, numRows);
    }

    /**
     * Sets the texture to be used by particles, when calling this method, it is assumed that the
     * image
     *
     * @param texturePath The path of the texture to use
     * @param uniformName The uniform name used when setting the particle texture
     */
    public void setSprite(String texturePath, String uniformName) {
        setSpriteByCount(texturePath, uniformName, 1, 1);
    }

    /**
     * Sets the texture to be used by particles, this can contain multiple images for random image
     * selection or sprite animation of particles.
     *
     * @param texturePath The path of the texture to use
     * @param uniformName The uniform name used when setting the particle texture
     * @param numCols     The number of sprite images per row
     * @param numRows     The number of rows containing sprite images
     */
    public void setSprite(String texturePath, String uniformName, int numCols, int numRows) {
        setSpriteByCount(texturePath, uniformName, numCols, numRows);
    }

    /**
     * Sets the texture to be used by particles, this can contain multiple images for random image
     * selection or sprite animation of particles.
     *
     * @param texturePath The path of the texture to use
     * @param numCols     The number of sprite images per row
     * @param numRows     The number of rows containing sprite images
     */
    public void setSpriteByCount(String texturePath, int numCols, int numRows) {
        setSpriteByCount(texturePath, uniformName, numCols, numRows);
    }

    /**
     * Sets the texture to be used by particles, this can contain multiple images for random image
     * selection or sprite animation of particles.
     *
     * @param texturePath The path of the texture to use
     * @param uniformName The uniform name used when setting the particle texture
     * @param numCols     The number of sprite images per row
     * @param numRows     The number of rows containing sprite images
     */
    public void setSpriteByCount(final String texturePath, final String uniformName, final int numCols, final int numRows) {
        this.texturePath = texturePath;
        this.spriteCols = numCols;
        this.spriteRows = numRows;
        this.uniformName = uniformName;

        if (emitterInitialized) {

            texture = assetManager.loadTexture(texturePath);
            texture.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
            texture.setMagFilter(Texture.MagFilter.Bilinear);

            material.setTexture(uniformName, texture);

            Image img = texture.getImage();
            int width = img.getWidth();
            int height = img.getHeight();

            spriteWidth = width / spriteCols;
            spriteHeight = height / spriteRows;

            mesh.setImagesXY(spriteCols, spriteRows);
            requiresUpdate = true;
        }
    }

    /**
     * Returns the current material used by the emitter.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Can be used to override the default Particle material. NOTE: If the color/diffuse uniform
     * name differs from "Texture", the new uniform name must be set when calling one of the
     * setSprite methods.
     *
     * @param material The material
     */
    @Override
    public void setMaterial(final Material material) {
        setMaterial(material, false);
    }

    /**
     * Can be used to override the default Particle material. NOTE: If the color/diffuse uniform
     * name differs from "Texture", the new uniform name must be set when calling one of the
     * setSprite methods.
     *
     * @param material               The material
     * @param applyLightingTransform Forces update of normals and should only be used if the emitter
     *                               material uses a lighting shader
     */
    public void setMaterial(final Material material, final boolean applyLightingTransform) {
        setMaterial(material, uniformName, applyLightingTransform);
    }

    /**
     * Can be used to override the default Particle material.
     *
     * @param material    The material
     * @param uniformName The material uniform name used for applying a color map (ex: Texture,
     *                    ColorMap, DiffuseMap)
     */
    public void setMaterial(final Material material, final String uniformName) {
        setMaterial(material, uniformName, false);
    }

    /**
     * Can be used to override the default Particle material.
     *
     * @param material               The material
     * @param uniformName            The material uniform name used for applying a color map (ex:
     *                               Texture, ColorMap, DiffuseMap)
     * @param applyLightingTransform Forces update of normals and should only be used if the emitter
     *                               material uses a lighting shader
     */
    public void setMaterial(final Material material, final String uniformName, final boolean applyLightingTransform) {
        this.userDefinedMaterial = material;
        this.applyLightingTransform = applyLightingTransform;
        this.uniformName = uniformName;

        if (emitterInitialized) {
            texture = assetManager.loadTexture(texturePath);
            texture.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
            texture.setMagFilter(Texture.MagFilter.Bilinear);
            material.setTexture(uniformName, texture);
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
    public void initialize(@NotNull final AssetManager assetManager) {
        if (emitterInitialized) return;

        this.assetManager = assetManager;

        initMaterials();

        if (userDefinedMaterial != null) {
            material = userDefinedMaterial;
        }

        initParticles(particleType, template);

        mesh.setImagesXY(spriteCols, spriteRows);

        texture = assetManager.loadTexture(texturePath);
        texture.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
        texture.setMagFilter(Texture.MagFilter.Bilinear);

        material.setTexture(uniformName, texture);

        Image img = texture.getImage();
        int width = img.getWidth();
        int height = img.getHeight();

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

        checkAndInitParticleGeometry();

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

    protected void checkAndInitParticleGeometry() {
        if (!particleNode.getChildren().isEmpty()) return;

        final Geometry geom = new Geometry();
        geom.setMesh(mesh);
        geom.setName("Particle Geometry");

        particleNode.attachChild(geom);
        particleNode.setMaterial(material);
        particleNode.setQueueBucket(RenderQueue.Bucket.Transparent);
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
        if (testParticles) attachChild(particleTestNode);
        else particleTestNode.removeFromParent();
        requiresUpdate = true;
    }

    /**
     * Returns if the emitter is set to show the emitter shape as a wireframe.
     */
    public boolean getEmitterTestModeShape() {
        return testEmitter;
    }

    /**
     * Returns if the emitter is set to show the particle mesh as a wireframe.
     */
    public boolean getEmitterTestModeParticles() {
        return testParticles;
    }

    @NotNull
    public Node getParticleTestNode() {
        return particleTestNode;
    }

    @NotNull
    public Node getEmitterTestNode() {
        return emitterTestNode;
    }

    @Override
    public void updateLogicalState(final float tpf) {
        super.updateLogicalState(tpf);
        if (enabled && !emitterInitialized) initialize(assetManager);

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

        if (emitterInitialized && (enabled || postRequiresUpdate)) {
            getChild(0).updateModelBound();
            if (testParticles)
                particleTestNode.getChild(0).updateModelBound();
            postRequiresUpdate = false;
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
        if (index < nextIndex || nextIndex == -1)
            nextIndex = index;
    }

    @Override
    public void runControlRender(final RenderManager rm, final ViewPort vp) {
        super.runControlRender(rm, vp);
        if (!emitterInitialized || (!enabled && !requiresUpdate)) return;

        Camera cam = vp.getCamera();

        if (mesh.getClass() == ParticleDataPointMesh.class) {
            float C = cam.getProjectionMatrix().m00;
            C *= cam.getWidth() * 0.5f;

            // send attenuation params
            material.setFloat("Quadratic", C);
        }
        mesh.updateParticleData(particles, cam, inverseRotation);
        if (requiresUpdate) {
            requiresUpdate = false;
            postRequiresUpdate = true;
        }
    }

    @Override
    public void write(@NotNull final JmeExporter ex) throws IOException {
        super.write(ex);

        final OutputCapsule capsule = ex.getCapsule(this);

        final ArrayList<ParticleInfluencer> toExport = new ArrayList<>(influencers.size());

        Collections.addAll(toExport, influencers.array());

        capsule.writeSavableArrayList(toExport, "influencers", null);
        capsule.write(template, "template", null);

        // write emitter info
        capsule.write(targetInterval, "targetInterval", 0);
        capsule.write(currentInterval, "currentInterval", 0);

        capsule.write(emissionsPerSecond, "emissionsPerSecond", 0);
        capsule.write(totalParticlesThisEmission, "totalParticlesThisEmission", 0);
        capsule.write(particlesPerEmission, "particlesPerEmission", 0);

        capsule.write(tpfThreshold, "tpfThreshold", 0);

        capsule.write(inverseRotation, "inverseRotation", null);

        capsule.write(useStaticParticles, "useStaticParticles", false);
        capsule.write(useRandomEmissionPoint, "useRandomEmissionPoint", false);
        capsule.write(useSequentialEmissionFace, "useSequentialEmissionFace", false);
        capsule.write(useSequentialSkipPattern, "useSequentialSkipPattern", false);
        capsule.write(useVelocityStretching, "useVelocityStretching", false);

        capsule.write(velocityStretchFactor, "velocityStretchFactor", 0);

        capsule.write(stretchAxis.ordinal(), "stretchAxis", ForcedStretchAxis.Y.ordinal());
        capsule.write(particleEmissionPoint.ordinal(), "particleEmissionPoint", ParticleEmissionPoint.PARTICLE_CENTER.ordinal());
        capsule.write(directionType.ordinal(), "directionType", EmitterMesh.DirectionType.Random.ordinal());

        capsule.write(emitterShape, "emitterShape", null);

        // write particleData info
        capsule.write(particleType.getName(), "particleType", ParticleDataTriMesh.class.getName());
        capsule.write(mesh, "mesh", null);

        capsule.write(activeParticleCount, "activeParticleCount", 0);
        capsule.write(maxParticles, "maxParticles", 0);

        capsule.write(forceMax, "forceMax", 0);
        capsule.write(forceMin, "forceMin", 0);

        capsule.write(lifeMin, "lifeMin", 0);
        capsule.write(lifeMax, "lifeMax", 0);

        capsule.write(interpolation, "interpolation", null);

        // write material information
        capsule.write(material, "material", null);
        capsule.write(userDefinedMaterial, "userDefinedMaterial", null);
        capsule.write(testMat, "testMat", null);

        capsule.write(applyLightingTransform, "applyLightingTransform", false);

        capsule.write(uniformName, "uniformName", null);
        capsule.write(texturePath, "texturePath", null);

        capsule.write(texture, "texture", null);

        capsule.write(spriteWidth, "spriteWidth", 0);
        capsule.write(spriteHeight, "spriteHeight", 0);

        capsule.write(spriteCols, "spriteCols", 0);
        capsule.write(spriteRows, "spriteRows", 0);

        capsule.write(billboardMode.ordinal(), "billboardMode", BillboardMode.CAMERA.ordinal());

        capsule.write(particlesFollowEmitter, "particlesFollowEmitter", false);
        capsule.write(enabled, "enabled", false);
        capsule.write(requiresUpdate, "requiresUpdate", false);

        // write emitter animation
        //FIXME

        // write particle animation
        //FIXME

        capsule.write(testEmitter, "testEmitter", false);
        capsule.write(testParticles, "testParticles", false);

        capsule.write(name, "name", null);
    }

    @Override
    public void read(@NotNull final JmeImporter im) throws IOException {
        super.read(im);

        this.assetManager = im.getAssetManager();

        final InputCapsule capsule = im.getCapsule(this);
        final ArrayList<ParticleInfluencer> imported = capsule.readSavableArrayList("influencers", null);

        influencers = newArray(ParticleInfluencer.class, imported == null ? 1 : imported.size());

        if (imported != null) {
            influencers.addAll(imported);
        }

        influencers.asUnsafe().trimToSize();
        template = (Mesh) capsule.readSavable("template", null);

        // read emitter info
        targetInterval = capsule.readFloat("targetInterval", 0);
        currentInterval = capsule.readFloat("currentInterval", 0);

        emissionsPerSecond = capsule.readInt("emissionsPerSecond", 0);
        totalParticlesThisEmission = capsule.readInt("totalParticlesThisEmission", 0);
        particlesPerEmission = capsule.readInt("particlesPerEmission", 0);

        tpfThreshold = capsule.readFloat("tpfThreshold", 0);

        inverseRotation = (Matrix3f) capsule.readSavable("inverseRotation", null);

        useStaticParticles = capsule.readBoolean("useStaticParticles", false);
        useRandomEmissionPoint = capsule.readBoolean("useRandomEmissionPoint", false);
        useSequentialEmissionFace = capsule.readBoolean("useSequentialEmissionFace", false);
        useSequentialSkipPattern = capsule.readBoolean("useSequentialSkipPattern", false);
        useVelocityStretching = capsule.readBoolean("useVelocityStretching", false);

        velocityStretchFactor = capsule.readFloat("velocityStretchFactor", 0);

        stretchAxis = ForcedStretchAxis.valueOf(capsule.readInt("stretchAxis", ForcedStretchAxis.Y.ordinal()));
        particleEmissionPoint = ParticleEmissionPoint.valueOf(capsule.readInt("particleEmissionPoint", ParticleEmissionPoint.PARTICLE_CENTER.ordinal()));
        directionType = EmitterMesh.DirectionType.valueOf(capsule.readInt("directionType", EmitterMesh.DirectionType.Random.ordinal()));

        emitterShape = (EmitterMesh) capsule.readSavable("emitterShape", null);
        emitterShape.setEmitterNode(this);

        // read particleData info

        // Reconstruct particle mesh
        try {
            particleType = Class.forName(capsule.readString("particleType", ParticleDataTriMesh.class.getName()));
        } catch (IOException | ClassNotFoundException ex) {
            particleType = ParticleDataTriMesh.class;
        }

        mesh = (ParticleDataMesh) capsule.readSavable("mesh", null);

        activeParticleCount = capsule.readInt("activeParticleCount", 0);
        maxParticles = capsule.readInt("maxParticles", 0);

        forceMax = capsule.readFloat("forceMax", 0);
        forceMin = capsule.readFloat("forceMin", 0);

        lifeMin = capsule.readFloat("lifeMin", 0);
        lifeMax = capsule.readFloat("lifeMax", 0);

        interpolation = (Interpolation) capsule.readSavable("interpolation", null);

        // write material information
        material = (Material) capsule.readSavable("material", null);
        userDefinedMaterial = (Material) capsule.readSavable("userDefinedMaterial", null);
        testMat = (Material) capsule.readSavable("testMat", null);

        applyLightingTransform = capsule.readBoolean("applyLightingTransform", false);

        uniformName = capsule.readString("uniformName", null);
        texturePath = capsule.readString("texturePath", null);

        texture = (Texture) capsule.readSavable("texture", null);

        spriteWidth = capsule.readFloat("spriteWidth", 0);
        spriteHeight = capsule.readFloat("spriteHeight", 0);

        spriteCols = capsule.readInt("spriteCols", 0);
        spriteRows = capsule.readInt("spriteRows", 0);

        billboardMode = BillboardMode.valueOf(capsule.readInt("billboardMode", BillboardMode.CAMERA.ordinal()));

        particlesFollowEmitter = capsule.readBoolean("particlesFollowEmitter", false);
        enabled = capsule.readBoolean("enabled", false);
        postRequiresUpdate = capsule.readBoolean("postRequiresUpdate", false);

        name = capsule.readString("name", "Restored name");

        Objects.requireNonNull(emitterShape);
        Objects.requireNonNull(interpolation);
        Objects.requireNonNull(mesh);
        Objects.requireNonNull(uniformName);
    }

    @Override
    public ParticleEmitterNode jmeClone() {
        return (ParticleEmitterNode) super.jmeClone();
    }

    @Override
    public void cloneFields(final Cloner cloner, final Object original) {

        final ParticleEmitterNode result = this;
        result.emitterAnimNode = cloner.clone(emitterAnimNode);
        result.emitterShape = cloner.clone(emitterShape);
        result.particleNode = cloner.clone(particleNode);
        result.particlesAnimNode = cloner.clone(particlesAnimNode);
        result.particleTestNode = cloner.clone(particleTestNode);
        result.emitterTestNode = cloner.clone(emitterTestNode);

        if (emitterAnimNode != null) {
            result.setShape(emitterAnimNode, emitterNodeExists);
            result.setEmitterAnimation(emitterAnimName, emitterAnimSpeed, emitterAnimBlendTime, emitterAnimLoopMode);
        } else result.setShape(emitterShape.getMesh());

        if (particlesAnimNode != null) {
            result.setParticleType(particleType, particlesAnimNode);
            result.setParticleAnimation(particlesAnimName, particlesAnimSpeed, particlesAnimBlendTime, particlesAnimLoopMode);
        } else result.setParticleType(particleType, template);

        final Array<ParticleInfluencer> originalInfluencers = influencers;
        influencers = ArrayFactory.newArray(ParticleInfluencer.class, influencers.size());

        for (final ParticleInfluencer influencer : originalInfluencers) {
            result.addInfluencer(influencer.clone());
        }
    }

    @Override
    public void setLocalTranslation(final Vector3f translation) {
        super.setLocalTranslation(translation);
        emitterTestNode.setLocalTranslation(translation);
        particleTestNode.setLocalTranslation(translation);
        requiresUpdate = true;
    }

    @Override
    public void setLocalTranslation(float x, float y, float z) {
        super.setLocalTranslation(x, y, z);
        emitterTestNode.setLocalTranslation(x, y, z);
        particleTestNode.setLocalTranslation(x, y, z);
        requiresUpdate = true;
    }

    @Override
    public void setLocalRotation(Quaternion q) {
        super.setLocalRotation(q);
        emitterTestNode.setLocalRotation(q);
        requiresUpdate = true;
    }

    @Override
    public void setLocalRotation(Matrix3f m) {
        super.setLocalRotation(m);
        emitterTestNode.setLocalRotation(m);
        requiresUpdate = true;
    }

    @Override
    public void setLocalScale(Vector3f scale) {
        super.setLocalScale(scale);
        emitterTestNode.setLocalScale(scale);
        requiresUpdate = true;
    }

    @Override
    public void setLocalScale(float scale) {
        super.setLocalScale(scale);
        emitterTestNode.setLocalScale(scale);
        requiresUpdate = true;
    }

    @Override
    public void setLocalScale(float x, float y, float z) {
        super.setLocalScale(x, y, z);
        emitterTestNode.setLocalScale(x, y, z);
        requiresUpdate = true;
    }
}
