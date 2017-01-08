#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform float m_Softness; // Power used in the contrast function
uniform float m_ColorMod;

#ifdef SOFT_PARTICLES
    uniform sampler2D m_SceneDepthTexture;
    varying vec2 vPos; // Position of the pixel
    varying vec2 projPos;// z and w valus in projection space
#endif

#ifdef USE_TEXTURE
    uniform sampler2D m_Texture;
    varying vec4 texCoord;
#endif

varying vec4 color;

float Contrast(float d) {
    float val = clamp(2.0 * ((d > 0.5) ? 1.0 - d : d ), 0.0, 1.0);
    float a = 0.5 * pow(val, m_Softness);
    return (d > 0.5) ? 1.0 - a : a;
}

float stdDiff(float d) {
    return clamp((d) * m_Softness, 0.0, 1.0);
}

void main() {

    vec4 colorMod = color;
    colorMod.r *= m_ColorMod;
    colorMod.g *= m_ColorMod;
    colorMod.b *= m_ColorMod;

    #ifdef SOFT_PARTICLES

        // color;
        vec4 resultColor = vec4(1.0, 1.0, 1.0, 1.0);

        #ifdef USE_TEXTURE
            #ifdef POINT_SPRITE
                vec2 uv = mix(texCoord.xy, texCoord.zw, gl_PointCoord.xy);
            #else
                vec2 uv = texCoord.xy;
            #endif
            resultColor = texture2D(m_Texture, uv) * colorMod;
        #endif

        // Scene depth
        float depthv = texture2D(m_SceneDepthTexture, vPos).x * 2.0 - 1.0;
        depthv *= projPos.y;

        float particleDepth = projPos.x;
        float zdiff = depthv - particleDepth;

        // Computes alpha based on the particles distance to the rest of the scene
        resultColor.a = resultColor.a * stdDiff(zdiff);// Contrast(zdiff);

        gl_FragColor = resultColor;

    #else
        #ifdef USE_TEXTURE

            #ifdef POINT_SPRITE
                vec2 uv = mix(texCoord.xy, texCoord.zw, gl_PointCoord.xy);
            #else
                vec2 uv = texCoord.xy;
            #endif

            vec4 tex = texture2D(m_Texture, uv) * colorMod;

            gl_FragColor = tex;
        #else
            gl_FragColor = colorMod;
        #endif
    #endif
}