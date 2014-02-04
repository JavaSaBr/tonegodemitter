/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package emitter.influencers;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.FastMath;
import java.io.IOException;
import emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class SpriteInfluencer implements ParticleInfluencer {
	private boolean enabled = true;
	private boolean useRandomImage = false;
	private boolean animate = true;
	private int totalFrames = -1;
	private float fixedDuration = 0f;
	private boolean cycle = false;
	private transient float targetInterval;
	private int[] frameSequence = null;
	private int frame;
	
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			if (animate) {
				p.spriteInterval += tpf;
				targetInterval = (cycle) ? fixedDuration : p.spriteDuration;
				if (p.spriteInterval >= targetInterval) {
					updateFrame(p);
				}
			}
		}
	}
	
	private void updateFrame(ParticleData p) {
		if (frameSequence == null) {
			p.spriteCol++;
			if (p.spriteCol == p.emitter.getSpriteColCount()) {
				p.spriteCol = 0;
				p.spriteRow++;
				if (p.spriteRow == p.emitter.getSpriteRowCount())
					p.spriteRow = 0;
			}
		} else {
			p.spriteIndex++;
			if (p.spriteIndex == frameSequence.length)
				p.spriteIndex = 0;
			frame = frameSequence[p.spriteIndex];
			p.spriteRow = (int)FastMath.floor(frame/p.emitter.getSpriteRowCount())-2;
			p.spriteCol = (int)frame%p.emitter.getSpriteColCount();
		}
		p.spriteInterval -= targetInterval;
	}
	
	public void initialize(ParticleData p) {
		if (frameSequence != null) {
			p.spriteIndex = 0;
			frame = frameSequence[p.spriteIndex];
			p.spriteRow = (int)FastMath.floor(frame/p.emitter.getSpriteRowCount())-2;
			p.spriteCol = (int)frame%p.emitter.getSpriteColCount();
		}
		if (totalFrames == -1) {
			totalFrames = p.emitter.getSpriteColCount()*p.emitter.getSpriteRowCount();
			if (totalFrames == 1) setAnimate(false);
		}
		if (useRandomImage) {
			if (frameSequence == null) {
				p.spriteCol = FastMath.nextRandomInt(0,p.emitter.getSpriteColCount()-1);
				p.spriteRow = FastMath.nextRandomInt(0,p.emitter.getSpriteRowCount()-1);
			} else {
				p.spriteIndex = FastMath.nextRandomInt(0,frameSequence.length-1);
				frame = frameSequence[p.spriteIndex];
				p.spriteRow = (int)FastMath.floor(frame/p.emitter.getSpriteRowCount())-2;
				p.spriteCol = (int)frame%p.emitter.getSpriteColCount();
			}
		}
		if (animate) {
			p.spriteInterval = 0;
			if (!cycle) {
				if (frameSequence == null)
					p.spriteDuration = p.startlife/(float)totalFrames;
				else
					p.spriteDuration = p.startlife/(float)frameSequence.length;
			}
		}
	}

	public void reset(ParticleData p) {
		p.spriteCol = 0;
		p.spriteRow = 0;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Particles will/will not use sprite animations
	 * @param animate boolean
	 */
	public void setAnimate(boolean animate) { this.animate = animate; }
	/**
	 * Current animation state of particle
	 * @return Returns if particles use sprite animation
	 */
	public boolean getAnimate() { return this.animate; }
	/**
	 * Sets if particles should select a random start image from the provided sprite texture
	 * @param useRandomImage boolean
	 */
	public void setUseRandomStartImage(boolean useRandomImage) { this.useRandomImage = useRandomImage; }
	/**
	 * Returns if particles currently select a random start image from the provided sprite texture
	 * @param useRandomImage boolean
	 * @return 
	 */
	public boolean getUseRandomStartImage() { return this.useRandomImage; }
	
	public void setFrameSequence(int... frame) {
		frameSequence = frame;
	}
	
	public int[] getFrameSequence() { return this.frameSequence; }
	
	public void clearFrameSequence() {
		frameSequence = null;
	}
	
	/**
	 * Animated texture should cycle and use the provided duration between frames (0 diables cycling)
	 * @param fixedDuration duration between frame updates
	 */
	public void setFixedDuration(float fixedDuration) {
		if (fixedDuration != 0) {
			this.cycle = true;
			this.fixedDuration = fixedDuration;
		} else {
			this.cycle = false;
			this.fixedDuration = 0;
		}
	}
	/**
	 * Returns the current duration used between frames for cycled animation
	 * @return 
	 */
	public float getFixedDuration() { return this.fixedDuration; }
	
	public void write(JmeExporter ex) throws IOException {
		
	}

	public void read(JmeImporter im) throws IOException {
		
	}
	
	@Override
	public ParticleInfluencer clone() {
		try {
			SpriteInfluencer clone = (SpriteInfluencer) super.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	public Class getInfluencerClass() {
		return SpriteInfluencer.class;
	}
}
