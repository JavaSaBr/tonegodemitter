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

import java.io.IOException;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for influence to color of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class PhysicsInfluencer extends AbstractParticleInfluencer {

    public enum CollisionReaction {
        BOUNCE,
        STICK,
        DESTROY;

        private static final CollisionReaction[] VALUES = values();

        public static CollisionReaction valueOf(final int index) {
            return VALUES[index];
        }
    }

    /**
     * The list of collidable geometries.
     */
    @NotNull
    private final GeometryList geometries;

    /**
     * The list of temprary collidable geometries.
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
     * The contact surface.
     */
    @NotNull
    private final Triangle contactSurface;

    /**
     * The quanterion.
     */
    @NotNull
    private final Quaternion quat;

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
    private CollisionResult result;

    /**
     * The collision reaction.
     */
    private CollisionReaction collisionReaction;

    /**
     * The two dot value.
     */
    private float twoDot;

    /**
     * The length value.
     */
    private float len;

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
        this.quat = new Quaternion();
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

    @NotNull
    @Override
    public String getName() {
        return "Physics influencer";
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        if (!particleData.collision) {
            findCollisions(particleData, tpf);
        } else {
            particleData.collisionInterval += tpf;
            if (particleData.collisionInterval >= collisionThreshold) {
                particleData.collision = false;
                particleData.collisionInterval = 0;
            }
        }

        super.updateImpl(particleData, tpf);
    }

    /**
     * Find collisions.
     *
     * @param particleData the particle data.
     * @param tpf          the tpf.
     */
    private void findCollisions(final @NotNull ParticleData particleData, final float tpf) {

        final ParticleEmitterNode emitterNode = particleData.getEmitterNode();

        for (int i = 0; i < geometries.size(); i++) {
            final Geometry geometry = geometries.get(i);
            try {

                if (results.size() != 0) {
                    results.clear();
                }

                updateCollisionShape(particleData, tpf);

                geometry.collideWith(geom.getWorldBound(), results);

                if (results.size() > 0) {

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
                            len = velocity.length() * (restitution - 0.1f) + (FastMath.nextRandomFloat() * 0.2f);
                            velocity.set(reflect).multLocal(len);
                            particleData.collision = true;
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
                }

            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
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
        quat.fromAngles(angles.x, angles.y, angles.z);
        geom.setLocalTranslation(translation);
        geom.setLocalRotation(quat);
        geom.setLocalScale(particleData.size);
        geom.updateLogicalState(tpf);
        geom.updateGeometricState();
        geom.updateModelBound();
    }

    /**
     * Add collidable geometry to this influencer.
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
     * Remove a last collidable geometry.
     */
    public void removeLast() {
        final int size = geometries.size();
        if (size < 1) return;
        removeCollidable(geometries.get(size - 1));
    }

    /**
     * Remove the collidable geometry from this influencer.
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
     * The collidable geometry list.
     *
     * @return the list of collidable geometries.
     */
    @NotNull
    public GeometryList getGeometries() {
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
     * @return the bounciness of the particle
     */
    public float getRestitution() {
        return restitution;
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.collision = false;
        particleData.collisionInterval = 0;
    }

    /**
     * Defines the response when a particle collides with a geometry in the collidables list
     */
    public void setCollisionReaction(final CollisionReaction collisionReaction) {
        this.collisionReaction = collisionReaction;
    }

    /**
     * @return the collision reaction.
     */
    public CollisionReaction getCollisionReaction() {
        return collisionReaction;
    }

    /**
     * @return the collision threshold value.
     */
    public float getCollisionThreshold() {
        return collisionThreshold;
    }

    /**
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
    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final PhysicsInfluencer clone = (PhysicsInfluencer) super.clone();
        clone.setCollisionReaction(collisionReaction);
        clone.setRestitution(restitution);
        clone.setCollisionThreshold(collisionThreshold);
        return clone;
    }
}
