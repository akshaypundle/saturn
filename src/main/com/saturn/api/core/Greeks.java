package com.saturn.api.core;

import com.google.common.base.MoreObjects;

public final class Greeks {

	private final float iv;
	private final float delta;
	private final float gamma;
	private final float theta;
	private final float vega;

	public Greeks(float iv, float delta, float gamma, float theta, float vega) {
		this.iv = iv;
		this.delta = delta;
		this.gamma = gamma;
		this.theta = theta;
		this.vega = vega;
	}

	public float getDelta() {
		return this.delta;
	}

	public float getGamma() {
		return this.gamma;
	}

	public float getIv() {
		return this.iv;
	}

	public float getTheta() {
		return this.theta;
	}

	public float getVega() {
		return this.vega;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("iv", this.iv) //
				.add("delta", this.delta) //
				.add("gamma", this.gamma) //
				.add("theta", this.theta) //
				.add("vega", this.vega) //
				.toString();
	}
}
