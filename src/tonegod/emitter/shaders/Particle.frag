#import "Common/ShaderLib/GLSLCompat.glsllib"

#ifdef USE_TEXTURE
	uniform sampler2D m_Texture;
	varying vec4 texCoord;
#endif

varying vec4 color;

void main() {

	#ifdef POINT_SPRITE
		vec2 uv = mix(texCoord.xy, texCoord.zw, gl_PointCoord.xy);
	#else
		vec2 uv = texCoord.xy;
	#endif

	vec4 tex = texture2D(m_Texture, uv);

	gl_FragColor =  tex;
}