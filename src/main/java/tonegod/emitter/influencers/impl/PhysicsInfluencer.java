package tonegod.emitter.influencers.impl;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.OpaqueComparator;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.InfluencerData;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * The implementation of the {@link ParticleInfluencer} to give physics reactions of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public class PhysicsInfluencer extends AbstractParticleInfluencer<PhysicsInfluencer.PhysicsInfluencerData> {

    @NotNull
    protected static final Callable<PhysicsInfluencerData> DATA_FACTORY = new Callable<PhysicsInfluencerData>() {
        @Override
        public PhysicsInfluencerData call() throws Exception {
            return new PhysicsInfluencerData();
        }
    };

    protected static class PhysicsInfluencerData implements InfluencerData<PhysicsInfluencerData> {

        /**
         * The flag.
         */
        public boolean collision;

        /**
         * The interval.
         */
        public float interval;

        private PhysicsInfluencerData() {
        }

        @Override
        public PhysicsInfluencerData create() {
            return new PhysicsInfluencerData();
        }
    }

    /**
     * The list of reactions on collisions.
     */
    public enum CollisionReaction {
        /**
         * Bounce collision reaction.
         */
        BOUNCE(Messages.PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_BOUNCE),
        /**
         * Stick collision reaction.
         */
        STICK(Messages.PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_STICK),
        /**
         * Destroy collision reaction.
         */
        DESTROY(Messages.PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_DESTROY);

        private static final CollisionReaction[] VALUES = values();

        /**
         * Get a collision reaction by the index.
         *
         * @param index the index.
         * @return the collision reaction.
         */
        public static @NotNull CollisionReaction valueOf(final int index) {
            return VALUES[index];
        }

        @NotNull
        private final String name;

        CollisionReaction(@NotNull final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String toString() {
            return name;
        }
    }

    /**
     * The list of collidable geometries.
     */
    @NotNull
    private final GeometryList geometries;

    /**
     * The temp list of collidable geometries.
     */
    @NotNull
    private final GeometryList tempGeometries;

    /**
     * The quad.
     */
    @NotNull
    private final Quad quad;

    /**
     * The geometry.
     */
    @NotNull
    private final Geometry geom;

    /**
     * The triangle of contacted surface.
     */
    @NotNull
    private final Triangle contactSurface;

    /**
     * The rotation of particle.
     */
    @NotNull
    private final Quaternion quaternion;

    /**
     * The collision results.
     */
    @NotNull
    private final CollisionResults results;

    /**
     * The reflect.
     */
    @NotNull
    private final Vector3f reflect;

    /**
     * The two.
     */
    @NotNull
    private final Vector3f two;

    /**
     * The normal.
     */
    @NotNull
    private final Vector3f normal;

    /**
     * The temp vector.
     */
    @NotNull
    private final Vector3f tempVec;

    /**
     * The temp vector #2.
     */
    @NotNull
    private final Vector3f tempVec2;

    /**
     * The collision result.
     */
    @Nullable
    private CollisionResult result;

    /**
     * The collision reaction.
     */
    @NotNull
    private CollisionReaction collisionReaction;

    /**
     * The two dot value.
     */
    private float twoDot;

    /**
     * The length value.
     */
    private float length;

    /**
     * The collision threshold value.
     */
    private float collisionThreshold;

    /**
     * The restitution value.
     */
    private float restitution;

    public PhysicsInfluencer() {
        this.geometries = new GeometryList(new OpaqueComparator());
        this.tempGeometries = new GeometryList(new OpaqueComparator());
        this.quad = new Quad(1, 1);
        this.geom = new Geometry();
        this.quaternion = new Quaternion();
        this.results = new CollisionResults();
        this.reflect = new Vector3f();
        this.two = new Vector3f();
        this.normal = new Vector3f();
        this.tempVec = new Vector3f();
        this.tempVec2 = new Vector3f();
        this.collisionReaction = CollisionReaction.BOUNCE;
        this.collisionThreshold = 0.1f;
        this.restitution = 0.5f;
        this.contactSurface = new Triangle();
        geom.setMesh(quad);
        quad.updateBound();
        geom.updateModelBound();
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_PHYSICS;
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final PhysicsInfluencerData data, final float tpf) {

        if (!data.collision) {
            findCollisions(particleData, data, tpf);
        } else {
            data.interval += tpf;
            if (data.interval >= collisionThreshold) {
                data.collision = false;
                data.interval = 0;
            }
        }

        super.updateImpl(particleData, data, tpf);
    }

    /**
     * Get the collision results.
     *
     * @return the collision results.
     */
    private @NotNull CollisionResults getResults() {
        return results;
    }

    /**
     * Find collisions.
     *
     * @param particleData the particle data.
     * @param tpf          the tpf.
     */
    private void findCollisions(final @NotNull ParticleData particleData, final PhysicsInfluencerData data, final float tpf) {

        final CollisionReaction collisionReaction = getCollisionReaction();
        final ParticleEmitterNode emitterNode = particleData.getEmitterNode();
        final GeometryList geometries = getGeometries();
        final CollisionResults results = getResults();

        for (int i = 0; i < geometries.size(); i++) {
            final Geometry geometry = geometries.get(i);
            try {

                if (results.size() != 0) {
                    results.clear();
                }

                updateCollisionShape(particleData, tpf);

                geometry.collideWith(geom.getWorldBound(), results);

                if (results.size() <= 0) {
                    continue;
                }

                final Vector3f velocity = particleData.velocity;

                result = results.getClosestCollision();

                switch (collisionReaction) {
                    case BOUNCE: {

                        result.getTriangle(contactSurface);
                        normal.set(contactSurface.getNormal());
                        twoDot = 2.0f * velocity.dot(normal);
                        two.set(twoDot, twoDot, twoDot);

                        reflect.set(two.mult(normal, tempVec)
                                .subtract(velocity, tempVec2))
                                .negateLocal().normalizeLocal();

                        length = velocity.length() * (restitution - 0.1f) + (FastMath.nextRandomFloat() * 0.2f);

                        velocity.set(reflect).multLocal(length);
                        data.collision = true;
                        break;
                    }
                    case STICK: {
                        velocity.set(0, 0, 0);
                        break;
                    }
                    case DESTROY: {
                        emitterNode.killParticle(particleData);
                        break;
                    }
                }

            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void initializeImpl(@NotNull final ParticleData particleData, final PhysicsInfluencerData data) {
        data.collision = false;
        data.interval = 0;

        super.initializeImpl(particleData, data);
    }

    /**
     * Update collision shape.
     *
     * @param particleData the particle data.
     * @param tpf          the tpf.
     */
    private void updateCollisionShape(@NotNull final ParticleData particleData, final float tpf) {

        final ParticleEmitterNode emitterNode = particleData.getEmitterNode();
        final Vector3f translation = tempVec.set(particleData.position)
                .addLocal(emitterNode.getLocalTranslation());

        final Vector3f angles = particleData.angles;
        quaternion.fromAngles(angles.x, angles.y, angles.z);
        geom.setLocalTranslation(translation);
        geom.setLocalRotation(quaternion);
        geom.setLocalScale(particleData.size);
        geom.updateLogicalState(tpf);
        geom.updateGeometricState();
        geom.updateModelBound();
    }

    /**
     * Add a geometry to this influencer.
     *
     * @param geometry the geometry.
     */
    public void addCollidable(@NotNull final Geometry geometry) {

        for (int i = 0; i < geometries.size(); i++) {
            if (geometries.get(i) == geometry) {
                throw new RuntimeException("The geometry " + geometry + " is already exists.");
            }
        }

        geometries.add(geometry);
    }

    /**
     * Remove a last geometry.
     */
    public void removeLast() {

        final int size = geometries.size();
        if (size < 1) {
            return;
        }

        removeCollidable(geometries.get(size - 1));
    }

    /**
     * Remove a geometry from this influencer.
     *
     * @param geometry the geometry.
     */
    public void removeCollidable(@NotNull final Geometry geometry) {
        boolean wasEnabled = isEnabled();
        setEnabled(false);
        try {

            tempGeometries.clear();

            for (int i = 0; i < geometries.size(); i++) {
                if (geometries.get(i) != geometry) {
                    tempGeometries.add(geometries.get(i));
                }
            }

            geometries.clear();

            for (int i = 0; i < tempGeometries.size(); i++) {
                geometries.add(tempGeometries.get(i));
            }

        } finally {
            setEnabled(wasEnabled);
        }
    }

    /**
     * Get a geometry list.
     *
     * @return the list of geometries.
     */
    public @NotNull GeometryList getGeometries() {
        return geometries;
    }

    /**
     * How "bouncy" the particle is (a value between 0.0f and 1.0f).  The default value is 0.5f.
     *
     * @param restitution The bounciness of the particle
     */
    public void setRestitution(final float restitution) {
        this.restitution = restitution;
    }

    /**
     * Gets restitution.
     *
     * @return the bounciness of the particle
     */
    public float getRestitution() {
        return restitution;
    }

    /**
     * Defines the response when a particle collides with a geometry in the collidables list.
     *
     * @param collisionReaction the collision reaction.
     */
    public void setCollisionReaction(@NotNull final CollisionReaction collisionReaction) {
        this.collisionReaction = collisionReaction;
    }

    /**
     * Get the collision reaction.
     *
     * @return the collision reaction.
     */
    public @NotNull CollisionReaction getCollisionReaction() {
        return collisionReaction;
    }

    /**
     * Gets the collision threshold.
     *
     * @return the collision threshold value.
     */
    public float getCollisionThreshold() {
        return collisionThreshold;
    }

    /**
     * Sets collision threshold.
     *
     * @param collisionThreshold the collision threshold value.
     */
    public void setCollisionThreshold(final float collisionThreshold) {
        this.collisionThreshold = collisionThreshold;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(collisionThreshold, "collisionThreshold", 0.1f);
        capsule.write(restitution, "restitution", 0.5f);
        capsule.write(collisionReaction.ordinal(), "collisionReaction", CollisionReaction.BOUNCE.ordinal());
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        collisionThreshold = capsule.readFloat("collisionThreshold", 0.1f);
        restitution = capsule.readFloat("restitution", 0.5f);
        collisionReaction = CollisionReaction.valueOf(capsule.readInt("collisionReaction", CollisionReaction.BOUNCE.ordinal()));
    }

    /**
     * This method clones the influencer instance.
     *
     * ** Please note the geometry list is specific to each instance of the physics influencer and must be maintained by
     * the user.  This list is NOT cloned from the original influencer.
     */
    @Override
    public @NotNull ParticleInfluencer clone() {
        final PhysicsInfluencer clone = (PhysicsInfluencer) super.clone();
        clone.setCollisionReaction(collisionReaction);
        clone.setRestitution(restitution);
        clone.setCollisionThreshold(collisionThreshold);
        return clone;
    }
}
