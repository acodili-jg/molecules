package io.github.jgacodili.molecules.molecule;

import io.github.jgacodili.molecules.math.Vec2d;

import java.awt.Color;
import java.awt.Paint;
import java.util.Objects;

public class Molecule {
	public static final Paint DEFAULT_PAINT = Color.BLACK;

	public static final double DEFAULT_RADIUS = 10.0d;

	public Paint paint;

	public Vec2d position;

	public double radius;

	public Vec2d velocity;

	public Molecule() {
		this(Vec2d.origin(), DEFAULT_RADIUS);
	}

	public Molecule(final Vec2d position, final double radius) {
		this.paint = DEFAULT_PAINT;
		this.position = position;
		this.radius = radius;
		this.velocity = new Vec2d();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof final Molecule other)
			return Objects.equals(this.position, other.position) && this.radius == other.radius
			        && Objects.equals(this.velocity, other.velocity);
		else
			return false;
	}

	public Paint getPaint() {
		return this.paint;
	}

	public Vec2d getPosition() {
		return this.position;
	}

	public double getRadius() {
		return this.radius;
	}

	public Vec2d getVelocity() {
		return this.velocity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.position, this.radius, this.velocity);
	}

	public void setPaint(final Paint paint) {
		this.paint = paint;
	}

	public void setPosition(final Vec2d position) {
		this.position = position;
	}

	public void setRadius(final double radius) {
		this.radius = radius;
	}

	public void setVelocity(final Vec2d velocity) {
		this.velocity = velocity;
	}
}
