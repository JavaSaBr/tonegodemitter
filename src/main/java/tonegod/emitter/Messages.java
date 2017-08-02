package tonegod.emitter;

import tonegod.emitter.util.PropertyLoader;

import java.util.ResourceBundle;

/**
 * The class with all messages of this library.
 *
 * @author JavaSaBr
 */
public class Messages {

    /**
     * The constant BUNDLE_NAME.
     */
    public static final String BUNDLE_NAME = "tonegod/emitter/messages/messages";

    /**
     * The constant EMISSION_POINT_CENTER.
     */
    public static final String EMISSION_POINT_CENTER;
    /**
     * The constant EMISSION_POINT_EDGE_TOP.
     */
    public static final String EMISSION_POINT_EDGE_TOP;
    /**
     * The constant EMISSION_POINT_EDGE_BOTTOM.
     */
    public static final String EMISSION_POINT_EDGE_BOTTOM;

    /**
     * The constant EMITTER_MESH_DIRECTION_TYPE_NORMAL.
     */
    public static final String EMITTER_MESH_DIRECTION_TYPE_NORMAL;
    /**
     * The constant EMITTER_MESH_DIRECTION_TYPE_NORMAL_NEGATE.
     */
    public static final String EMITTER_MESH_DIRECTION_TYPE_NORMAL_NEGATE;
    /**
     * The constant EMITTER_MESH_DIRECTION_TYPE_RANDOM.
     */
    public static final String EMITTER_MESH_DIRECTION_TYPE_RANDOM;
    /**
     * The constant EMITTER_MESH_DIRECTION_TYPE_RANDOM_TANGENT.
     */
    public static final String EMITTER_MESH_DIRECTION_TYPE_RANDOM_TANGENT;
    /**
     * The constant EMITTER_MESH_DIRECTION_TYPE_RANDOM_NORMAL_ALIGNED.
     */
    public static final String EMITTER_MESH_DIRECTION_TYPE_RANDOM_NORMAL_ALIGNED;
    /**
     * The constant EMITTER_MESH_DIRECTION_TYPE_RANDOM_NORMAL_NEGATE.
     */
    public static final String EMITTER_MESH_DIRECTION_TYPE_RANDOM_NORMAL_NEGATE;

    /**
     * The constant BILLBOARD_MODE_VELOCITY.
     */
    public static final String BILLBOARD_MODE_VELOCITY;
    /**
     * The constant BILLBOARD_MODE_VELOCITY_Z_UP.
     */
    public static final String BILLBOARD_MODE_VELOCITY_Z_UP;
    /**
     * The constant BILLBOARD_MODE_VELOCITY_Z_UP_Y_LEFT.
     */
    public static final String BILLBOARD_MODE_VELOCITY_Z_UP_Y_LEFT;
    /**
     * The constant BILLBOARD_MODE_NORMAL.
     */
    public static final String BILLBOARD_MODE_NORMAL;
    /**
     * The constant BILLBOARD_MODE_NORMAL_Y_UP.
     */
    public static final String BILLBOARD_MODE_NORMAL_Y_UP;
    /**
     * The constant BILLBOARD_MODE_CAMERA.
     */
    public static final String BILLBOARD_MODE_CAMERA;
    /**
     * The constant BILLBOARD_MODE_UNIT_X.
     */
    public static final String BILLBOARD_MODE_UNIT_X;
    /**
     * The constant BILLBOARD_MODE_UNIT_Y.
     */
    public static final String BILLBOARD_MODE_UNIT_Y;
    /**
     * The constant BILLBOARD_MODE_UNIT_Z.
     */
    public static final String BILLBOARD_MODE_UNIT_Z;

    /**
     * The constant INTERPOLATION_LINEAR.
     */
    public static final String INTERPOLATION_LINEAR;
    /**
     * The constant INTERPOLATION_FADE.
     */
    public static final String INTERPOLATION_FADE;
    /**
     * The constant INTERPOLATION_SINE.
     */
    public static final String INTERPOLATION_SINE;
    /**
     * The constant INTERPOLATION_SINE_IN.
     */
    public static final String INTERPOLATION_SINE_IN;
    /**
     * The constant INTERPOLATION_SINE_OUT.
     */
    public static final String INTERPOLATION_SINE_OUT;
    /**
     * The constant INTERPOLATION_EXP_10.
     */
    public static final String INTERPOLATION_EXP_10;
    /**
     * The constant INTERPOLATION_EXP_10_IN.
     */
    public static final String INTERPOLATION_EXP_10_IN;
    /**
     * The constant INTERPOLATION_EXP_10_OUT.
     */
    public static final String INTERPOLATION_EXP_10_OUT;
    /**
     * The constant INTERPOLATION_EXP_5.
     */
    public static final String INTERPOLATION_EXP_5;
    /**
     * The constant INTERPOLATION_EXP_5_IN.
     */
    public static final String INTERPOLATION_EXP_5_IN;
    /**
     * The constant INTERPOLATION_EXP_5_OUT.
     */
    public static final String INTERPOLATION_EXP_5_OUT;
    /**
     * The constant INTERPOLATION_CIRCLE.
     */
    public static final String INTERPOLATION_CIRCLE;
    /**
     * The constant INTERPOLATION_CIRCLE_IN.
     */
    public static final String INTERPOLATION_CIRCLE_IN;
    /**
     * The constant INTERPOLATION_CIRCLE_OUT.
     */
    public static final String INTERPOLATION_CIRCLE_OUT;
    /**
     * The constant INTERPOLATION_SWING.
     */
    public static final String INTERPOLATION_SWING;
    /**
     * The constant INTERPOLATION_SWING_IN.
     */
    public static final String INTERPOLATION_SWING_IN;
    /**
     * The constant INTERPOLATION_SWING_OUT.
     */
    public static final String INTERPOLATION_SWING_OUT;
    /**
     * The constant INTERPOLATION_BOUNCE.
     */
    public static final String INTERPOLATION_BOUNCE;
    /**
     * The constant INTERPOLATION_BOUNCE_IN.
     */
    public static final String INTERPOLATION_BOUNCE_IN;
    /**
     * The constant INTERPOLATION_BOUNCE_OUT.
     */
    public static final String INTERPOLATION_BOUNCE_OUT;
    /**
     * The constant INTERPOLATION_POW_2.
     */
    public static final String INTERPOLATION_POW_2;
    /**
     * The constant INTERPOLATION_POW_2_IN.
     */
    public static final String INTERPOLATION_POW_2_IN;
    /**
     * The constant INTERPOLATION_POW_2_OUT.
     */
    public static final String INTERPOLATION_POW_2_OUT;
    /**
     * The constant INTERPOLATION_POW_3.
     */
    public static final String INTERPOLATION_POW_3;
    /**
     * The constant INTERPOLATION_POW_3_IN.
     */
    public static final String INTERPOLATION_POW_3_IN;
    /**
     * The constant INTERPOLATION_POW_3_OUT.
     */
    public static final String INTERPOLATION_POW_3_OUT;
    /**
     * The constant INTERPOLATION_POW_4.
     */
    public static final String INTERPOLATION_POW_4;
    /**
     * The constant INTERPOLATION_POW_4_IN.
     */
    public static final String INTERPOLATION_POW_4_IN;
    /**
     * The constant INTERPOLATION_POW_4_OUT.
     */
    public static final String INTERPOLATION_POW_4_OUT;
    /**
     * The constant INTERPOLATION_POW_5.
     */
    public static final String INTERPOLATION_POW_5;
    /**
     * The constant INTERPOLATION_POW_5_IN.
     */
    public static final String INTERPOLATION_POW_5_IN;
    /**
     * The constant INTERPOLATION_POW_5_OUT.
     */
    public static final String INTERPOLATION_POW_5_OUT;
    /**
     * The constant INTERPOLATION_ELASTIC.
     */
    public static final String INTERPOLATION_ELASTIC;
    /**
     * The constant INTERPOLATION_ELASTIC_IN.
     */
    public static final String INTERPOLATION_ELASTIC_IN;
    /**
     * The constant INTERPOLATION_ELASTIC_OUT.
     */
    public static final String INTERPOLATION_ELASTIC_OUT;

    /**
     * The constant PARTICLE_INFLUENCER_ALPHA.
     */
    public static final String PARTICLE_INFLUENCER_ALPHA;
    /**
     * The constant PARTICLE_INFLUENCER_COLOR.
     */
    public static final String PARTICLE_INFLUENCER_COLOR;
    /**
     * The constant PARTICLE_INFLUENCER_DESTINATION.
     */
    public static final String PARTICLE_INFLUENCER_DESTINATION;
    /**
     * The constant PARTICLE_INFLUENCER_GRAVITY.
     */
    public static final String PARTICLE_INFLUENCER_GRAVITY;
    /**
     * The constant PARTICLE_INFLUENCER_IMPULSE.
     */
    public static final String PARTICLE_INFLUENCER_IMPULSE;
    /**
     * The constant PARTICLE_INFLUENCER_PHYSICS.
     */
    public static final String PARTICLE_INFLUENCER_PHYSICS;
    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY;
    /**
     * The constant PARTICLE_INFLUENCER_ROTATION.
     */
    public static final String PARTICLE_INFLUENCER_ROTATION;
    /**
     * The constant PARTICLE_INFLUENCER_SIZE.
     */
    public static final String PARTICLE_INFLUENCER_SIZE;
    /**
     * The constant PARTICLE_INFLUENCER_SPRITE.
     */
    public static final String PARTICLE_INFLUENCER_SPRITE;

    /**
     * The constant PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_WORLD.
     */
    public static final String PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_WORLD;
    /**
     * The constant PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_REVERSE_VELOCITY.
     */
    public static final String PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_REVERSE_VELOCITY;
    /**
     * The constant PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_EMISSION_POINT.
     */
    public static final String PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_EMISSION_POINT;
    /**
     * The constant PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_EMITTER_CENTER.
     */
    public static final String PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_EMITTER_CENTER;

    /**
     * The constant PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_BOUNCE.
     */
    public static final String PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_BOUNCE;
    /**
     * The constant PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_STICK.
     */
    public static final String PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_STICK;
    /**
     * The constant PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_DESTROY.
     */
    public static final String PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_DESTROY;

    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_ALIGNMENT_EMISSION_POINT.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_ALIGNMENT_EMISSION_POINT;
    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_ALIGNMENT_EMITTER_CENTER.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_ALIGNMENT_EMITTER_CENTER;

    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_ABSOLUTE.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_ABSOLUTE;
    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_X.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_X;
    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_Y.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_Y;
    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_Z.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_Z;

    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_NORMAL.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_NORMAL;
    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_X.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_X;
    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_Y.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_Y;
    /**
     * The constant PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_Z.
     */
    public static final String PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_Z;

    static {

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, PropertyLoader.getInstance());

        INTERPOLATION_LINEAR = bundle.getString("Interpolation.Linear");
        INTERPOLATION_FADE = bundle.getString("Interpolation.Fade");
        INTERPOLATION_SINE = bundle.getString("Interpolation.Sine");
        INTERPOLATION_SINE_IN = bundle.getString("Interpolation.SineIn");
        INTERPOLATION_SINE_OUT = bundle.getString("Interpolation.SineOut");
        INTERPOLATION_EXP_10 = bundle.getString("Interpolation.Exp10");
        INTERPOLATION_EXP_10_IN = bundle.getString("Interpolation.ExpIn10");
        INTERPOLATION_EXP_10_OUT = bundle.getString("Interpolation.ExpOut10");
        INTERPOLATION_EXP_5 = bundle.getString("Interpolation.Exp5");
        INTERPOLATION_EXP_5_IN = bundle.getString("Interpolation.ExpIn5");
        INTERPOLATION_EXP_5_OUT = bundle.getString("Interpolation.ExpOut5");
        INTERPOLATION_CIRCLE = bundle.getString("Interpolation.Circle");
        INTERPOLATION_CIRCLE_IN = bundle.getString("Interpolation.CircleIn");
        INTERPOLATION_CIRCLE_OUT = bundle.getString("Interpolation.CircleOut");
        INTERPOLATION_SWING = bundle.getString("Interpolation.Swing");
        INTERPOLATION_SWING_IN = bundle.getString("Interpolation.SwingIn");
        INTERPOLATION_SWING_OUT = bundle.getString("Interpolation.SwingOut");
        INTERPOLATION_BOUNCE = bundle.getString("Interpolation.Bounce");
        INTERPOLATION_BOUNCE_IN = bundle.getString("Interpolation.BounceIn");
        INTERPOLATION_BOUNCE_OUT = bundle.getString("Interpolation.BounceOut");
        INTERPOLATION_POW_2 = bundle.getString("Interpolation.Pow2");
        INTERPOLATION_POW_2_IN = bundle.getString("Interpolation.PowIn2");
        INTERPOLATION_POW_2_OUT = bundle.getString("Interpolation.PowOut2");
        INTERPOLATION_POW_3 = bundle.getString("Interpolation.Pow3");
        INTERPOLATION_POW_3_IN = bundle.getString("Interpolation.PowIn3");
        INTERPOLATION_POW_3_OUT = bundle.getString("Interpolation.PowOut3");
        INTERPOLATION_POW_4 = bundle.getString("Interpolation.Pow4");
        INTERPOLATION_POW_4_IN = bundle.getString("Interpolation.PowIn4");
        INTERPOLATION_POW_4_OUT = bundle.getString("Interpolation.PowOut4");
        INTERPOLATION_POW_5 = bundle.getString("Interpolation.Pow5");
        INTERPOLATION_POW_5_IN = bundle.getString("Interpolation.PowIn5");
        INTERPOLATION_POW_5_OUT = bundle.getString("Interpolation.PowOut5");
        INTERPOLATION_ELASTIC = bundle.getString("Interpolation.Elastic");
        INTERPOLATION_ELASTIC_IN = bundle.getString("Interpolation.ElasticIn");
        INTERPOLATION_ELASTIC_OUT = bundle.getString("Interpolation.ElasticOut");

        EMISSION_POINT_CENTER = bundle.getString("Emission.Point.Center");
        EMISSION_POINT_EDGE_TOP = bundle.getString("Emission.Point.EdgeTop");
        EMISSION_POINT_EDGE_BOTTOM = bundle.getString("Emission.Point.EdgeBottom");

        EMITTER_MESH_DIRECTION_TYPE_NORMAL = bundle.getString("EmitterMesh.DirectionType.Normal");
        EMITTER_MESH_DIRECTION_TYPE_NORMAL_NEGATE = bundle.getString("EmitterMesh.DirectionType.NormalNegate");
        EMITTER_MESH_DIRECTION_TYPE_RANDOM = bundle.getString("EmitterMesh.DirectionType.Random");
        EMITTER_MESH_DIRECTION_TYPE_RANDOM_TANGENT = bundle.getString("EmitterMesh.DirectionType.RandomTangent");
        EMITTER_MESH_DIRECTION_TYPE_RANDOM_NORMAL_ALIGNED = bundle.getString("EmitterMesh.DirectionType.RandomNormalAligned");
        EMITTER_MESH_DIRECTION_TYPE_RANDOM_NORMAL_NEGATE = bundle.getString("EmitterMesh.DirectionType.RandomNormalNegate");

        BILLBOARD_MODE_VELOCITY = bundle.getString("BillboardMode.Velocity");
        BILLBOARD_MODE_VELOCITY_Z_UP = bundle.getString("BillboardMode.VelocityZUp");
        BILLBOARD_MODE_VELOCITY_Z_UP_Y_LEFT = bundle.getString("BillboardMode.VelocityZUpYLeft");
        BILLBOARD_MODE_NORMAL = bundle.getString("BillboardMode.Normal");
        BILLBOARD_MODE_NORMAL_Y_UP = bundle.getString("BillboardMode.NormalYUp");
        BILLBOARD_MODE_CAMERA = bundle.getString("BillboardMode.Camera");
        BILLBOARD_MODE_UNIT_X = bundle.getString("BillboardMode.UnitX");
        BILLBOARD_MODE_UNIT_Y = bundle.getString("BillboardMode.UnitY");
        BILLBOARD_MODE_UNIT_Z = bundle.getString("BillboardMode.UnitZ");

        PARTICLE_INFLUENCER_ALPHA = bundle.getString("ParticleInfluencer.Alpha");
        PARTICLE_INFLUENCER_COLOR = bundle.getString("ParticleInfluencer.Color");
        PARTICLE_INFLUENCER_DESTINATION = bundle.getString("ParticleInfluencer.Destination");
        PARTICLE_INFLUENCER_GRAVITY = bundle.getString("ParticleInfluencer.Gravity");
        PARTICLE_INFLUENCER_IMPULSE = bundle.getString("ParticleInfluencer.Impulse");
        PARTICLE_INFLUENCER_PHYSICS = bundle.getString("ParticleInfluencer.Physics");
        PARTICLE_INFLUENCER_RADIAL_VELOCITY = bundle.getString("ParticleInfluencer.RadialVelocity");
        PARTICLE_INFLUENCER_ROTATION = bundle.getString("ParticleInfluencer.Rotation");
        PARTICLE_INFLUENCER_SIZE = bundle.getString("ParticleInfluencer.Size");
        PARTICLE_INFLUENCER_SPRITE = bundle.getString("ParticleInfluencer.Sprite");

        PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_WORLD = bundle.getString("ParticleInfluencer.Gravity.Alignment.World");
        PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_REVERSE_VELOCITY = bundle.getString("ParticleInfluencer.Gravity.Alignment.ReverseVelocity");
        PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_EMISSION_POINT = bundle.getString("ParticleInfluencer.Gravity.Alignment.EmissionPoint");
        PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_EMITTER_CENTER = bundle.getString("ParticleInfluencer.Gravity.Alignment.EmitterCenter");

        PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_BOUNCE = bundle.getString("ParticleInfluencer.Physics.CollisionReaction.Bounce");
        PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_STICK = bundle.getString("ParticleInfluencer.Physics.CollisionReaction.Stick");
        PARTICLE_INFLUENCER_PHYSICS_COLLISION_REACTION_DESTROY = bundle.getString("ParticleInfluencer.Physics.CollisionReaction.Destroy");

        PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_ALIGNMENT_EMISSION_POINT = bundle.getString("ParticleInfluencer.RadialVelocity.PullAlignment.EmissionPoint");
        PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_ALIGNMENT_EMITTER_CENTER = bundle.getString("ParticleInfluencer.RadialVelocity.PullAlignment.EmitterCenter");

        PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_ABSOLUTE = bundle.getString("ParticleInfluencer.RadialVelocity.PullCenter.Absolute");
        PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_X = bundle.getString("ParticleInfluencer.RadialVelocity.PullCenter.X");
        PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_Y = bundle.getString("ParticleInfluencer.RadialVelocity.PullCenter.Y");
        PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_Z = bundle.getString("ParticleInfluencer.RadialVelocity.PullCenter.Z");

        PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_NORMAL = bundle.getString("ParticleInfluencer.RadialVelocity.UpAlignment.Normal");
        PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_X = bundle.getString("ParticleInfluencer.RadialVelocity.UpAlignment.UnitX");
        PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_Y = bundle.getString("ParticleInfluencer.RadialVelocity.UpAlignment.UnitY");
        PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_Z = bundle.getString("ParticleInfluencer.RadialVelocity.UpAlignment.UnitZ");
    }
}