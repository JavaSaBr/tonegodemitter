package emitter;

import com.jme3.math.FastMath;

/**
 *
 * @author t0neg0d
 * Based on original code from Nathan Sweet
 */
public enum Interpolation {
	bounce(
		new float[] { 0.34f, 0.34f, 0.2f, 0.15f },
		new float[] { 0.34f, 0.26f, 0.11f, 0.03f }
	) {
		private float out (float a) {
			float test = a + widths[0] / 2;
			if (test < widths[0]) return test / (widths[0] / 2) - 1;
			return bounceOut.apply(a);
		}

		@Override
		public float apply (float a) {
			if (a <= 0.5f) return (1 - out(1 - a * 2)) / 2;
			return out(a * 2 - 1) / 2 + 0.5f;
		}
	},
	bounceIn(
		new float[] { 0.34f, 0.34f, 0.2f, 0.15f },
		new float[] { 0.34f, 0.26f, 0.11f, 0.03f }
	) {
		@Override
		public float apply (float a) {
			return 1 - bounceOut.apply(1 - a);
		}
	},
	bounceOut(
		new float[] { 0.34f, 0.34f, 0.2f, 0.15f },
		new float[] { 0.34f, 0.26f, 0.11f, 0.03f }
	) {
		@Override
		public float apply (float a) {
			a += widths[0] / 2;
			float width = 0, height = 0;
			for (int i = 0, n = widths.length; i < n; i++) {
				width = widths[i];
				if (a <= width) {
					height = heights[i];
					break;
				}
				a -= width;
			}
			a /= width;
			float z = 4 / width * height * a;
			return 1 - (z - z * a) * width;
		}
	},
	circle {
		@Override
		public float apply (float a) {
			if (a <= 0.5f) {
				a *= 2;
				return (1 - FastMath.sqrt(1 - a * a)) / 2;
			}
			a--;
			a *= 2;
			return (FastMath.sqrt(1 - a * a) + 1) / 2;
		}
	},
	circleIn {
		@Override
		public float apply (float a) {
			return 1 - FastMath.sqrt(1 - a * a);
		}
	},
	circleOut {
		@Override
		public float apply (float a) {
			a--;
			return FastMath.sqrt(1 - a * a);
		}
	},
	exp(2,1) {
		@Override
		public float apply (float a) {
			if (a <= 0.5f) return (FastMath.pow(value, power * (a * 2 - 1)) - min) * scale / 2;
			return (2 - (FastMath.pow(value, -power * (a * 2 - 1)) - min) * scale) / 2;
		}
	},
	expIn(2,1) {
		@Override
		public float apply (float a) {
			return (FastMath.pow(value, power * (a - 1)) - min) * scale;
		}
	},
	expOut(2,1) {
		@Override
		public float apply (float a) {
			return 1 - (FastMath.pow(value, -power * a) - min) * scale;
		}
	},
	exp5(2,5) {
		@Override
		public float apply(float a) {
			return exp.apply(a);
		}
	},
	exp5In(2,5) {
		@Override
		public float apply(float a) {
			return expIn.apply(a);
		}
	},
	exp5Out(2,5) {
		@Override
		public float apply(float a) {
			return expOut.apply(a);
		}
	},
	exp10(2,10) {
		@Override
		public float apply(float a) {
			return exp.apply(a);
		}
	},
	exp10In(2,10) {
		@Override
		public float apply(float a) {
			return expIn.apply(a);
		}
	},
	exp10Out(2,10) {
		@Override
		public float apply(float a) {
			return expOut.apply(a);
		}
	},
	elastic(2,10) {
		@Override
		public float apply (float a) {
			if (a <= 0.5f) {
				a *= 2;
				return FastMath.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f / 2;
			}
			a = 1 - a;
			a *= 2;
			return 1 - FastMath.pow(value, power * (a - 1)) * FastMath.sin((a) * 20) * 1.0955f / 2;
		}
	},
	elasticIn(2,10) {
		@Override
		public float apply (float a) {
			return FastMath.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f;
		}
	},
	elasticOut(2,10) {
		@Override
		public float apply (float a) {
			a = 1 - a;
			return (1 - FastMath.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f);
		}
	},
	fade {
		@Override
		public float apply (float a) {
			return FastMath.clamp(a * a * a * (a * (a * 6 - 15) + 10), 0, 1);
		}
	},
	linear {
		@Override
		public float apply (float a) {
			return a;
		}
	},
	pow((int)1) {
		@Override
		public float apply (float a) {
			if (a <= 0.5f) return FastMath.pow(a * 2, power) / 2;
			return FastMath.pow((a - 1) * 2, power) / (power % 2 == 0 ? -2 : 2) + 1;
		}
	},
	powIn((int)1) {
		@Override
		public float apply(float a) {
			return FastMath.pow(a, power);
		}
	},
	powOut((int)1) {
		@Override
		public float apply(float a) {
			return FastMath.pow(a - 1, power) * (power % 2 == 0 ? -1 : 1) + 1;
		}
	},
	pow2((int)2) {
		@Override
		public float apply(float a) {
			return pow.apply(a);
		}
	},
	pow2In((int)2) {
		@Override
		public float apply(float a) {
			return powIn.apply(a);
		}
	},
	pow2Out((int)2) {
		@Override
		public float apply(float a) {
			return powOut.apply(a);
		}
	},
	pow3((int)3) {
		@Override
		public float apply(float a) {
			return pow.apply(a);
		}
	},
	pow3In((int)3) {
		@Override
		public float apply(float a) {
			return powIn.apply(a);
		}
	},
	pow3Out((int)3) {
		@Override
		public float apply(float a) {
			return powOut.apply(a);
		}
	},
	pow4((int)4) {
		@Override
		public float apply(float a) {
			return pow.apply(a);
		}
	},
	pow4In((int)4) {
		@Override
		public float apply(float a) {
			return powIn.apply(a);
		}
	},
	pow4Out((int)4) {
		@Override
		public float apply(float a) {
			return powOut.apply(a);
		}
	},
	pow5((int)5) {
		@Override
		public float apply(float a) {
			return pow.apply(a);
		}
	},
	pow5In((int)5) {
		@Override
		public float apply(float a) {
			return powIn.apply(a);
		}
	},
	pow5Out((int)5) {
		@Override
		public float apply(float a) {
			return powOut.apply(a);
		}
	},
	sine {
		@Override
		public float apply (float a) {
			return (1 - FastMath.cos(a * FastMath.PI)) / 2;
		}
	},
	sineIn {
		@Override
		public float apply (float a) {
			return 1 - FastMath.cos(a * FastMath.PI / 2);
		}
	},
	sineOut {
		@Override
		public float apply (float a) {
			return FastMath.sin(a * FastMath.PI / 2);
		}
	},
	swing(1.5f) {
		@Override
		public float apply (float a) {
			if (a <= 0.5f) {
				a *= 2;
				return a * a * ((scale + 1) * a - scale) / 2;
			}
			a--;
			a *= 2;
			return a * a * ((scale + 1) * a + scale) / 2 + 1;
		}
	},
	swingIn(2f) {
		@Override
		public float apply (float a) {
			return a * a * ((scale + 1) * a - scale);
		}
	},
	swingOut(2f) {
		@Override
		public float apply (float a) {
			a--;
			return a * a * ((scale + 1) * a + scale) + 1;
		}
	};
	
	final float[] widths, heights;
	final float value, power, min, scale;
	
	Interpolation() {
		this(0,0,0,0,null,null);
	}
	Interpolation(int power) {
		this(0,power,0,0,null,null);
	}
	Interpolation(float scale) {
		this(0,0,0,scale,null,null);
	}
	Interpolation(float value, float power) {
		this(
			value,
			power,
			FastMath.pow(value, -power),
			1 / (1 - FastMath.pow(value, -power)),
			null,
			null
		);
	}
	Interpolation(float[] widths, float[] heights) {
		this(0,0,0,0,widths,heights);
	}
	Interpolation(float value, float power, float min, float scale, float[] widths, float[] heights) {
		this.value = value;
		this.power = power;
		this.min = min;
		this.scale = scale;
		this.widths = widths;
		this.heights = heights;
	}
	abstract public float apply (float a);
}
