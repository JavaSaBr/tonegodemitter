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
import com.jme3.math.Vector3f;
import java.io.IOException;
import emitter.Interpolation;
import emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class GravityInfluencer implements ParticleInfluencer {
	public static enum GravityAlignment {
		World,
		Reverse_Velocity,
		Emission_Point,
		Emitter_Center
	}
	private boolean enabled = true;
	private Vector3f gravity = new Vector3f(0,1f,0);
	private transient Vector3f store = new Vector3f();
	private boolean useNegativeVelocity = false;
	private float magnitude = 1;
	private GravityAlignment alignment = GravityAlignment.World;
	
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			if (!p.emitter.getUseStaticParticles()) {
				switch (alignment) {
					case World:
						store.set(gravity).multLocal(tpf);
						p.velocity.subtractLocal(store);
						break;
					case Reverse_Velocity:
						store.set(p.reverseVelocity).multLocal(tpf);
						p.velocity.addLocal(store);
						break;
					case Emission_Point:
						p.emitter.getShape().setNext(p.triangleIndex);
						if (p.emitter.getUseRandomEmissionPoint())
							store.set(p.emitter.getShape().getNextTranslation().addLocal(p.randomOffset));
						else
							store.set(p.emitter.getShape().getNextTranslation());
						store.subtractLocal(p.position).multLocal(p.initialLength*magnitude).multLocal(tpf);
						p.velocity.addLocal(store);
						break;
					case Emitter_Center:
						store.set(p.emitter.getShape().getMesh().getBound().getCenter());
						store.subtractLocal(p.position).multLocal(p.initialLength*magnitude).multLocal(tpf);
						p.velocity.addLocal(store);
						break;
				}
			}
		}
	}
	
	public void initialize(ParticleData p) {
		p.reverseVelocity.set(p.velocity.negate().mult(magnitude));
	}

	public void reset(ParticleData p) {
		
	}

	public void setAlignment(GravityAlignment alignment) {
		this.alignment = alignment;
	}
	
	public GravityAlignment getAlignment() {
		return this.alignment;
	}
	
	public void setMagnitude(float magnitude) {
		this.magnitude = magnitude;
	}
	
	public float getMagnitude() {
		return this.magnitude;
	}
	
	/**
	 * Sets gravity to the provided Vector3f
	 * @param gravity Vector3f representing gravity
	 */
	public void setGravity(Vector3f gravity) {
		this.gravity.set(gravity);
	}
	/**
	 * Sets gravity per axis to the specified values
	 * @param x Gravity along the x axis
	 * @param y Gravity along the y axis
	 * @param z Gravity along the z axis
	 */
	public void setGravity(float x, float y, float z) {
		this.gravity.set(x, y, z);
	}
	
	public Vector3f getGravity() {
		return this.gravity;
	}
	
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
        oc.write(gravity, "gravity", new Vector3f(0,1,0));
		oc.write(enabled, "enabled" ,true);
		oc.write(useNegativeVelocity, "useNegativeVelocity", false);
		oc.write(magnitude, "magnitude", 1f);
	}

	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		gravity = (Vector3f) ic.readSavable("gravity", new Vector3f(0,1,0));
	}
	
	@Override
	public ParticleInfluencer clone() {
		try {
			GravityInfluencer clone = (GravityInfluencer) super.clone();
			clone.setGravity(gravity);
			clone.enabled = enabled;
			clone.useNegativeVelocity = false;
			clone.magnitude = 1;
			clone.alignment = alignment;
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
		return GravityInfluencer.class;
	}
}
