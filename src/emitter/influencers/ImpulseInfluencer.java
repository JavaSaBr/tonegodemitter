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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.io.IOException;
import emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class ImpulseInfluencer implements ParticleInfluencer {
	private boolean enabled = true;
	private transient Vector3f temp = new Vector3f();
    private transient Vector3f velocityStore = new Vector3f();
	private float chance = .02f;
	private float magnitude = .2f;
	private float strength = 3;
	
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			if (FastMath.rand.nextFloat() > 1-(chance+tpf)) {
				velocityStore.set(p.velocity);
				temp.set(FastMath.nextRandomFloat()*strength,
						FastMath.nextRandomFloat()*strength,
						FastMath.nextRandomFloat()*strength
				);
				if (FastMath.rand.nextBoolean()) temp.x = -temp.x;
				if (FastMath.rand.nextBoolean()) temp.y = -temp.y;
				if (FastMath.rand.nextBoolean()) temp.z = -temp.z;
				temp.multLocal(velocityStore.length());
				velocityStore.interpolate(temp, magnitude);
				p.velocity.interpolate(velocityStore, magnitude);
			}
		}
	}
	
	public void initialize(ParticleData p) {
		
	}
	
	public void reset(ParticleData p) {
		
	}
	
	/**
	 * Sets the chance the influencer has of successfully affecting the particle's velocity vector
	 * @param chance float
	 */
	public void setChance(float chance) { this.chance = chance; }
	
	/**
	 * Returns the chance the influencer has of successfully affecting the particle's velocity vector
	 * @return float
	 */
	public float getChance() { return chance; }
	/**
	 * Sets the magnitude at which the impulse will effect the particle's velocity vector
	 * @param magnitude float
	 */
	public void setMagnitude(float magnitude) { this.magnitude = magnitude; }
	/**
	 * Returns  the magnitude at which the impulse will effect the particle's velocity vector
	 * @return float
	 */
	public float getMagnitude() { return magnitude; }
	/**
	 * Sets the strength of the full impulse
	 * @param strength float
	 */
	public void setStrength(float strength) { this.strength = strength; }
	/**
	 * Returns the strength of the full impulse
	 * @return float
	 */
	public float getStrength() { return strength; }
	
	public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(velocityStore, "initialVelocity", Vector3f.ZERO);
        oc.write(chance, "chance", 0.02f);
        oc.write(magnitude, "magnitude", 0.2f);
        oc.write(strength, "strength", 3f);
    }
	
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		velocityStore = (Vector3f) ic.readSavable("startVelocity", Vector3f.ZERO.clone());
		chance = ic.readFloat("chance", 0.02f);
    	magnitude = ic.readFloat("magnitude", 0.2f);
    	strength = ic.readFloat("strength", 3f);
    }
	
    @Override
    public ParticleInfluencer clone() {
        try {
            ImpulseInfluencer clone = (ImpulseInfluencer) super.clone();
            clone.setChance(chance);
			clone.setMagnitude(magnitude);
			clone.setStrength(strength);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public Class getInfluencerClass() {
		return ImpulseInfluencer.class;
	}
}
