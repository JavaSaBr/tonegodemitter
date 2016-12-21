package tonegod.emitter;

import java.util.ResourceBundle;

/**
 * The class with all messages of this library.
 *
 * @author JavaSaBr
 */
public class Messages {

    public static final String BUNDLE_NAME = "messages/messages";

    public static final String INTERPOLATION_LINEAR;
    public static final String INTERPOLATION_FADE;
    public static final String INTERPOLATION_SINE;
    public static final String INTERPOLATION_SINE_IN;
    public static final String INTERPOLATION_SINE_OUT;
    public static final String INTERPOLATION_EXP_10;
    public static final String INTERPOLATION_EXP_10_IN;
    public static final String INTERPOLATION_EXP_10_OUT;
    public static final String INTERPOLATION_EXP_5;
    public static final String INTERPOLATION_EXP_5_IN;
    public static final String INTERPOLATION_EXP_5_OUT;
    public static final String INTERPOLATION_CIRCLE;
    public static final String INTERPOLATION_CIRCLE_IN;
    public static final String INTERPOLATION_CIRCLE_OUT;
    public static final String INTERPOLATION_SWING;
    public static final String INTERPOLATION_SWING_IN;
    public static final String INTERPOLATION_SWING_OUT;
    public static final String INTERPOLATION_BOUNCE;
    public static final String INTERPOLATION_BOUNCE_IN;
    public static final String INTERPOLATION_BOUNCE_OUT;
    public static final String INTERPOLATION_POW_2;
    public static final String INTERPOLATION_POW_2_IN;
    public static final String INTERPOLATION_POW_2_OUT;
    public static final String INTERPOLATION_POW_3;
    public static final String INTERPOLATION_POW_3_IN;
    public static final String INTERPOLATION_POW_3_OUT;
    public static final String INTERPOLATION_POW_4;
    public static final String INTERPOLATION_POW_4_IN;
    public static final String INTERPOLATION_POW_4_OUT;
    public static final String INTERPOLATION_POW_5;
    public static final String INTERPOLATION_POW_5_IN;
    public static final String INTERPOLATION_POW_5_OUT;
    public static final String INTERPOLATION_ELASTIC;
    public static final String INTERPOLATION_ELASTIC_IN;
    public static final String INTERPOLATION_ELASTIC_OUT;

    static {

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, ResourceControl.getInstance());

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
    }
}