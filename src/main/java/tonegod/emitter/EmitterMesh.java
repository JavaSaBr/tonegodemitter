package tonegod.emitter;

import static java.util.Objects.requireNonNull;
import static tonegod.emitter.util.RandomUtils.nextRandomInt;
import com.jme3.export.*;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.util.RandomUtils;

import java.io.IOException;
import java.util.Random;

/**
 * The type Emitter mesh.
 *
 * @author t0neg0d, JavaSaBr
 */
public class EmitterMesh implements Cloneable, JmeCloneable, Savable {

    /**
     * The enum Direction type.
     */
    public enum DirectionType {
        /**
         * Normal direction type.
         */
        NORMAL(Messages.EMITTER_MESH_DIRECTION_TYPE_NORMAL),
        /**
         * Normal negate direction type.
         */
        NORMAL_NEGATE(Messages.EMITTER_MESH_DIRECTION_TYPE_NORMAL_NEGATE),
        /**
         * Random direction type.
         */
        RANDOM(Messages.EMITTER_MESH_DIRECTION_TYPE_RANDOM),
        /**
         * Random tangent direction type.
         */
        RANDOM_TANGENT(Messages.EMITTER_MESH_DIRECTION_TYPE_RANDOM_TANGENT),
        /**
         * Random normal aligned direction type.
         */
        RANDOM_NORMAL_ALIGNED(Messages.EMITTER_MESH_DIRECTION_TYPE_RANDOM_NORMAL_ALIGNED),
        /**
         * Random normal negate direction type.
         */
        RANDOM_NORMAL_NEGATE(Messages.EMITTER_MESH_DIRECTION_TYPE_RANDOM_NORMAL_NEGATE);

        private static final DirectionType[] VALUES = values();

        /**
         * Value of direction type.
         *
         * @param index the index
         * @return the direction type
         */
        public static DirectionType valueOf(final int index) {
            return VALUES[index];
        }

        @NotNull
        private final String uiName;

        DirectionType(@NotNull final String uiName) {
            this.uiName = uiName;
        }

        @Override
        public String toString() {
            return uiName;
        }
    }

    /**
     * The triangle.
     */
    @NotNull
    private Triangle triangle;

    /**
     * The emitter node.
     */
    @Nullable
    private ParticleEmitterNode emitterNode;

    /**
     * The mesh.
     */
    @Nullable
    private Mesh mesh;

    @NotNull
    private Node pointNode;

    @NotNull
    private Node node1;

    @NotNull
    private Node node2;

    @NotNull
    private Node node3;

    @NotNull
    private Vector3f point1;

    @NotNull
    private Vector3f point2;

    @NotNull
    private Vector3f point3;

    @NotNull
    private Vector3f interpolationA;

    @NotNull
    private Vector3f interpolationB;

    @NotNull
    private Vector3f resultInterpolation;

    @NotNull
    private Vector3f tempDirection;

    @NotNull
    private Vector3f tempDirection2;

    @NotNull
    private Quaternion tempQuaternion;

    @NotNull
    private Quaternion tempQuaternion2;

    /**
     * The triangle index.
     */
    private int triangleIndex;

    /**
     * The triangle count.
     */
    private int triangleCount;

    /**
     * The current triangle.
     */
    private int currentTriangle;

    /**
     * Instantiates a new Emitter mesh.
     */
    public EmitterMesh() {
        this.triangle = new Triangle();
        this.pointNode = new Node();
        this.node1 = new Node();
        this.node2 = new Node();
        this.node3 = new Node();
        this.pointNode.attachChild(node1);
        this.pointNode.attachChild(node2);
        this.pointNode.attachChild(node3);
        this.point1 = new Vector3f();
        this.point2 = new Vector3f();
        this.point3 = new Vector3f();
        this.interpolationA = new Vector3f();
        this.interpolationB = new Vector3f();
        this.resultInterpolation = new Vector3f();
        this.tempDirection = new Vector3f();
        this.tempDirection2 = new Vector3f();
        this.tempQuaternion = new Quaternion();
        this.tempQuaternion2 = new Quaternion();
    }

    /**
     * Sets the mesh to use as the emitter shape
     *
     * @param emitterNode the emitter node
     * @param mesh        The mesh to use as the emitter shape
     */
    public void setShape(@NotNull final ParticleEmitterNode emitterNode, @NotNull final Mesh mesh) {
        this.emitterNode = emitterNode;
        this.mesh = mesh;
        this.triangleCount = mesh.getTriangleCount();
    }

    /**
     * Set an emitter node.
     *
     * @param emitterNode the emitter node.
     */
    public void setEmitterNode(@NotNull final ParticleEmitterNode emitterNode) {
        this.emitterNode = emitterNode;
    }

    /**
     * Returns the mesh used as the particle emitter shape
     *
     * @return The particle emitter shape mesh
     */
    @NotNull
    public Mesh getMesh() {
        return requireNonNull(mesh);
    }

    /**
     * Gets emitter node.
     *
     * @return the emitter node.
     */
    @NotNull
    public ParticleEmitterNode getEmitterNode() {
        return requireNonNull(emitterNode);
    }

    /**
     * Selects interpolationA random face as the next particle emission point
     */
    public void setNext() {

        final ParticleEmitterNode emitterNode = getEmitterNode();
        final Mesh mesh = getMesh();

        if (emitterNode.isSequentialEmissionFace()) {

            if (emitterNode.isSequentialSkipPattern()) {
                currentTriangle += 2;
            } else {
                currentTriangle++;
            }

            if (currentTriangle >= triangleCount) {
                currentTriangle = 0;
            }

            triangleIndex = currentTriangle;
        } else {
            final Random random = RandomUtils.getRandom();
            triangleIndex = random.nextInt(triangleCount);
        }

        mesh.getTriangle(triangleIndex, triangle);

        calculateTransform();

        triangle.calculateCenter();
        triangle.calculateNormal();
    }

    /**
     * Set the current particle emission face to the specified faces index
     *
     * @param triangleIndex The index of the face to set as the particle emission point
     */
    public void setNext(final int triangleIndex) {

        final Mesh mesh = getMesh();
        mesh.getTriangle(triangleIndex, triangle);

        calculateTransform();

        final Triangle triangle = getTriangle();
        triangle.calculateCenter();
        triangle.calculateNormal();
    }

    private void calculateTransform() {

        final ParticleEmitterNode emitterNode = getEmitterNode();

        node1.setLocalTranslation(triangle.get1());
        node2.setLocalTranslation(triangle.get2());
        node3.setLocalTranslation(triangle.get3());

        pointNode.setLocalRotation(emitterNode.getLocalRotation());
        pointNode.setLocalScale(emitterNode.getLocalScale());

        final Triangle triangle = getTriangle();
        triangle.set1(node1.getWorldTranslation());
        triangle.set2(node2.getWorldTranslation());
        triangle.set3(node3.getWorldTranslation());
    }

    /**
     * Returns the index of the current face being used as the particle emission point
     *
     * @return the triangle index
     */
    public int getTriangleIndex() {
        return triangleIndex;
    }

    /**
     * Gets normal.
     *
     * @return the normal of current triangle.
     */
    @NotNull
    public Vector3f getNormal() {
        return triangle.getNormal();
    }

    /**
     * Returns the local position of the center of the selected face
     *
     * @return A Vector3f representing the local translation of the selected emission point
     */
    @NotNull
    public Vector3f getNextTranslation() {
        return triangle.getCenter();
    }

    /**
     * @return the triangle.
     */
    @NotNull
    private Triangle getTriangle() {
        return triangle;
    }

    /**
     * Ger a random translation.
     *
     * @return the translation.
     */
    @NotNull
    public Vector3f getRandomTranslation() {

        final Triangle triangle = getTriangle();
        final Vector3f center = triangle.getCenter();
        final Random random = RandomUtils.getRandom();

        switch (nextRandomInt(random, 1, 3)) {
            case 1: {
                point1.set(triangle.get1()).subtractLocal(center);
                point2.set(triangle.get2()).subtractLocal(center);
                point3.set(triangle.get3()).subtractLocal(center);
                break;
            }
            case 2: {
                point1.set(triangle.get2()).subtractLocal(center);
                point2.set(triangle.get1()).subtractLocal(center);
                point3.set(triangle.get3()).subtractLocal(center);
                break;
            }
            case 3: {
                point1.set(triangle.get3()).subtractLocal(center);
                point2.set(triangle.get2()).subtractLocal(center);
                point3.set(triangle.get1()).subtractLocal(center);
                break;
            }
        }

        interpolationA.interpolateLocal(point1, point2, 1F - random.nextFloat());
        interpolationB.interpolateLocal(point1, point3, 1F - random.nextFloat());

        resultInterpolation.interpolateLocal(interpolationA, interpolationB, random.nextFloat());

        return resultInterpolation;
    }

    /**
     * Returns the normal of the selected emission point
     *
     * @return A Vector3f containing the normal of the selected emission point
     */
    @NotNull
    public Vector3f getNextDirection() {

        final ParticleEmitterNode emitterNode = getEmitterNode();

        switch (emitterNode.getDirectionType()) {
            case NORMAL: {
                return getDirectionNormal();
            }
            case NORMAL_NEGATE: {
                return getDirectionNormal().negateLocal();
            }
            case RANDOM: {
                return getDirectionRandom();
            }
            case RANDOM_TANGENT: {
                return getDirectionRandomTangent();
            }
            case RANDOM_NORMAL_ALIGNED: {

                final Vector3f directionRandom = getDirectionRandom();

                if (directionRandom.dot(getDirectionNormal()) < 0) {
                    directionRandom.negateLocal();
                }

                return directionRandom;
            }
            case RANDOM_NORMAL_NEGATE: {

                final Vector3f directionRandom = getDirectionRandom();

                if (directionRandom.dot(getDirectionNormal()) > 0) {
                    directionRandom.negateLocal();
                }

                return directionRandom;
            }
            default: {
                return tempDirection;
            }
        }
    }

    /**
     * @return the direction normal.
     */
    @NotNull
    private Vector3f getDirectionNormal() {
        return triangle.getNormal();
    }

    /**
     * @return the random direction.
     */
    @NotNull
    private Vector3f getDirectionRandom() {

        final Random random = RandomUtils.getRandom();

        tempQuaternion.fromAngles(
                random.nextFloat() * FastMath.TWO_PI,
                random.nextFloat() * FastMath.TWO_PI,
                random.nextFloat() * FastMath.TWO_PI
        );

        tempQuaternion.mult(Vector3f.UNIT_Y, tempDirection);

        return tempDirection;
    }

    /**
     * @return the direction random target.
     */
    @NotNull
    private Vector3f getDirectionRandomTangent() {

        final Random random = RandomUtils.getRandom();
        final Vector3f normal = getNormal();

        tempQuaternion2.lookAt(normal, Vector3f.UNIT_Y);
        tempQuaternion2.mult(Vector3f.UNIT_Y, tempDirection2);

        tempQuaternion.fromAngleAxis(random.nextFloat() * 360 * FastMath.DEG_TO_RAD, normal);
        tempQuaternion.mult(tempDirection2, tempDirection);

        return tempDirection;
    }

    @Override
    public EmitterMesh clone() {
        try {
            return (EmitterMesh) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmitterMesh jmeClone() {
        return clone();
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        mesh = cloner.clone(mesh);
        emitterNode = cloner.clone(emitterNode);
        triangle = cloner.clone(triangle);
        point1 = cloner.clone(point1);
        point2 = cloner.clone(point2);
        point3 = cloner.clone(point3);
        interpolationA = cloner.clone(interpolationA);
        interpolationB = cloner.clone(interpolationB);
        resultInterpolation = cloner.clone(resultInterpolation);
        pointNode = cloner.clone(pointNode);
        node1 = cloner.clone(node1);
        node2 = cloner.clone(node2);
        node3 = cloner.clone(node3);
        tempQuaternion = cloner.clone(tempQuaternion);
        tempQuaternion2 = cloner.clone(tempQuaternion2);
        tempDirection = cloner.clone(tempDirection);
        tempDirection2 = cloner.clone(tempDirection2);
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(mesh, "mesh", null);
        capsule.write(triangle, "triangle", null);
        capsule.write(point1, "point1", null);
        capsule.write(point2, "point2", null);
        capsule.write(point3, "point3", null);
        capsule.write(interpolationA, "interpolationA", null);
        capsule.write(interpolationB, "interpolationB", null);
        capsule.write(resultInterpolation, "resultInterpolation", null);
        capsule.write(pointNode, "pointNode", null);
        capsule.write(tempDirection, "tempDirection", null);
        capsule.write(triangleCount, "triangleCount", 1);
        capsule.write(currentTriangle, "currentTriangle", 0);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        mesh = (Mesh) capsule.readSavable("mesh", null);
        triangle = (Triangle) capsule.readSavable("triangle", capsule.readSavable("triStore", null));
        point1 = (Vector3f) capsule.readSavable("point1", capsule.readSavable("p1", null));
        point2 = (Vector3f) capsule.readSavable("point2", capsule.readSavable("p2", null));
        point3 = (Vector3f) capsule.readSavable("point3", capsule.readSavable("p3", null));
        interpolationA = (Vector3f) capsule.readSavable("interpolationA", capsule.readSavable("a", null));
        interpolationB = (Vector3f) capsule.readSavable("interpolationB", capsule.readSavable("b", null));
        resultInterpolation = (Vector3f) capsule.readSavable("resultInterpolation", capsule.readSavable("result", null));
        pointNode = (Node) capsule.readSavable("pointNode", capsule.readSavable("p", null));
        tempDirection = (Vector3f) capsule.readSavable("tempDirection", capsule.readSavable("tempDir", null));
        triangleCount = capsule.readInt("triangleCount", capsule.readInt("triCount", 1));
        currentTriangle = capsule.readInt("currentTriangle", capsule.readInt("currentTri", 1));
    }
}
