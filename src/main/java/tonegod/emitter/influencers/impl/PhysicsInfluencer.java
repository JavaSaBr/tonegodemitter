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
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;

/**
 * The implementation of the {@link ParticleInfluencer} to give physics reactions of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public class PhysicsInfluencer extends AbstractWithDataParticleInfluencer<PhysicsInfluencer.PhysicsInfluencerData> {

    protected static class PhysicsInfluencerData implements JmeCloneable {

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
        public @NotNull Object jmeClone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void cloneFields(@NotNull Cloner cloner, @NotNull Object original) {
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final PhysicsInfluencerData that = (PhysicsInfluencerData) o;
            if (collision != that.collision) return false;
            return Float.compare(that.interval, interval) == 0;
        }

        @Override
        public int hashCode() {
            int result = (collision ? 1 : 0);
            result = 31 * result + (interval != +0.0f ? Float.floatToIntBits(interval) : 0);
            return result;
        }

        @Override
        public String toString() {
            return "PhysicsInfluencerData{" + "collision=" + collision + ", interval=" + interval + '}';
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

        CollisionReaction(@NotNull String name) {
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
    public @NotNull PhysicsInfluencerData newDataObject() {
        return new PhysicsInfluencerData();
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_PHYSICS;
    }

    @Override
    protected void updateImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull PhysicsInfluencer.PhysicsInfluencerData data,
            float tpf
    ) {

        if (!data.collision) {
            findCollisions(emitterNode, particleData, data, tpf);
        } else {
            data.interval += tpf;
            if (data.interval >= collisionThreshold) {
                data.collision = false;
                data.interval = 0;
            }
        }

        super.updateImpl(emitterNode, particleData, data, tpf);
    }

    /**
     * Gets the collision results.
     *
     * @return the collision results.
     */
    private @NotNull CollisionResults getResults() {
        return results;
    }

    /**
     * Finds collisions.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     * @param data         the influence's data.
     * @param tpf          the tpf.
     */
    private void findCollisions(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull PhysicsInfluencerData data,
            float tpf
    ) {

        CollisionReaction collisionReaction = getCollisionReaction();
        GeometryList geometries = getGeometries();
        CollisionResults results = getResults();

        for (int i = 0; i < geometries.size(); i++) {
            Geometry geometry = geometries.get(i);
            try {

                if (results.size() != 0) {
                    results.clear();
                }

                updateCollisionShape(emitterNode, particleData, tpf);

                geometry.collideWith(geom.getWorldBound(), results);

                if (results.size() <= 0) {
                    continue;
                }

                Vector3f velocity = particleData.velocity;

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

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void initializeImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull PhysicsInfluencer.PhysicsInfluencerData data
    ) {

        data.collision = false;
        data.interval = 0;

        super.initializeImpl(emitterNode, particleData, data);
    }

    /**
     * Updates collision shape.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     * @param tpf          the tpf.
     */
    private void updateCollisionShape(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            float tpf
    ) {

        Vector3f translation = tempVec.set(particleData.position)
                .addLocal(emitterNode.getLocalTranslation());

        Vector3f angles = particleData.angles;

        quaternion.fromAngles(angles.x, angles.y, angles.z);
        geom.setLocalTranslation(translation);
        geom.setLocalRotation(quaternion);
        geom.setLocalScale(particleData.size);
        geom.updateLogicalState(tpf);
        geom.updateGeometricState();
        geom.updateModelBound();
    }

    /**
     * Adds a geometry to this influencer.
     *
     * @param geometry the geometry.
     */
    public void addCollidable(@NotNull Geometry geometry) {

        for (int i = 0; i < geometries.size(); i++) {
            if (geometries.get(i) == geometry) {
                throw new RuntimeException("The geometry " + geometry + " is already exists.");
            }
        }

        geometries.add(geometry);
    }

    /**
     * Removes a last geometry.
     */
    public void removeLast() {

        final int size = geometries.size();
        if (size < 1) {
            return;
        }

        removeCollidable(geometries.get(size - 1));
    }

    /**
     * Removes a geometry from this influencer.
     *
     * @param geometry the geometry.
     */
    public void removeCollidable(@NotNull Geometry geometry) {
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
     * Gets a geometry list.
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
    public void setRestitution(float restitution) {
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
    public void setCollisionReaction(@NotNull CollisionReaction collisionReaction) {
        this.collisionReaction = collisionReaction;
    }

    /**
     * Gets the collision reaction.
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
    public void setCollisionThreshold(float collisionThreshold) {
        this.collisionThreshold = collisionThreshold;
    }

    @Override
    public void write(@NotNull JmeExporter exporter) throws IOException {
        super.write(exporter);

        OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(collisionThreshold, "collisionThreshold", 0.1f);
        capsule.write(restitution, "restitution", 0.5f);
        capsule.write(collisionReaction.ordinal(), "collisionReaction", CollisionReaction.BOUNCE.ordinal());
    }

    @Override
    public void read(@NotNull JmeImporter importer) throws IOException {
        super.read(importer);

        InputCapsule capsule = importer.getCapsule(this);
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
        PhysicsInfluencer clone = (PhysicsInfluencer) super.clone();
        clone.setCollisionReaction(collisionReaction);
        clone.setRestitution(restitution);
        clone.setCollisionThreshold(collisionThreshold);
        return clone;
    }
}
