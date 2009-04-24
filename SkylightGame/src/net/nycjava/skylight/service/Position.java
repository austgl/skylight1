package net.nycjava.skylight.service;

public class Position {
	final float x;

	final float y;

	final float z;

	public Position(float anX, float aY, float aZ) {
		x = anX;
		y = aY;
		z = aZ;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}
}
