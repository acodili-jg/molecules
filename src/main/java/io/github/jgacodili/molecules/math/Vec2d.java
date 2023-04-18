package io.github.jgacodili.molecules.math;

import java.awt.geom.Point2D;
import java.util.Objects;

public class Vec2d {
	public static Vec2d euler(final double theta) {
		return new Vec2d(Math.cos(theta), Math.sin(theta));
	}

	public static Vec2d euler(final double theta, Vec2d buffer) {
		if (buffer == null)
			buffer = new Vec2d();

		buffer.set(Math.cos(theta), Math.sin(theta));

		return buffer;
	}

	public static Vec2d fma(final double multiplierX, final double multiplierY, final double multiplicand,
	        final double addendX, final double addendY, Vec2d buffer) {
		if (buffer == null)
			buffer = new Vec2d();

		buffer.x = Math.fma(multiplierX, multiplicand, addendX);
		buffer.y = Math.fma(multiplierY, multiplicand, addendY);

		return buffer;
	}

	public static Vec2d fma(final Object multiplier, final double multiplicand, final Object addend, Vec2d buffer) {
		final double multiplierX;
		final double multiplierY;
		final double addendX;
		final double addendY;

		if (buffer == null)
			buffer = new Vec2d();

		if (multiplier instanceof final Point2D point0) {
			multiplierX = point0.getX();
			multiplierY = point0.getY();
		} else if (multiplier instanceof final Vec2d vector0) {
			multiplierX = vector0.x;
			multiplierY = vector0.y;
		} else {
			buffer.set(Double.NaN, Double.NaN);

			return buffer;
		}

		if (addend instanceof final Point2D point1) {
			addendX = point1.getX();
			addendY = point1.getY();
		} else if (addend instanceof final Vec2d vector1) {
			addendX = vector1.x;
			addendY = vector1.y;
		} else {
			buffer.set(Double.NaN, Double.NaN);

			return buffer;
		}

		buffer.x = Math.fma(multiplierX, multiplicand, addendX);
		buffer.y = Math.fma(multiplierY, multiplicand, addendY);

		return buffer;
	}

	public static Vec2d normalize(final double x, final double y) {
		return new Vec2d(x, y).normalized();
	}

	public static Vec2d normalize(final Object obj) {
		return new Vec2d(obj).normalized();
	}

	public static Vec2d of(final Object obj) {
		return new Vec2d(obj);
	}

	public static Vec2d origin() {
		return new Vec2d(0.0d, 0.0d);
	}

	public double x;

	public double y;

	public Vec2d() {
	}

	public Vec2d(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public Vec2d(final Object other) {
		set(other);
	}

	public Vec2d add(final double addendX, final double addendY) {
		this.x += addendX;
		this.y += addendY;

		return this;
	}

	public Vec2d add(final Object addend) {
		if (addend instanceof final Point2D point)
			return add(point.getX(), point.getY());
		else if (addend instanceof final Vec2d vector)
			return add(vector.x, vector.y);
		else
			return add(Double.NaN, Double.NaN);
	}

	public double atan() {
		return Math.atan2(this.y, this.x);
	}

	public Vec2d divide(final double divisor) {
		this.x /= divisor;
		this.y /= divisor;

		return this;
	}

	public Vec2d dividedBy(final double divisor) {
		return new Vec2d(this).divide(divisor);
	}

	public double dot(final double otherX, final double otherY) {
		return this.x * otherX + this.y * otherY;
	}

	public double dot(final Object other) {
		if (other instanceof final Point2D point)
			return dot(point.getX(), point.getY());
		else if (other instanceof final Vec2d vector)
			return dot(vector.x, vector.y);
		else
			return Double.NaN;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof final Vec2d other)
			return this.x == other.x && this.y == other.y;
		else
			return false;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.x, this.y);
	}

	public double magnitude() {
		return Math.hypot(this.x, this.y);
	}

	public Vec2d minus(final double subtrahendX, final double subtrahendY) {
		return new Vec2d(this).subtract(subtrahendX, subtrahendY);
	}

	public Vec2d minus(final Object subtrahend) {
		return new Vec2d(this).subtract(subtrahend);
	}

	public Vec2d multiply(final double multiplicand) {
		this.x *= multiplicand;
		this.y *= multiplicand;

		return this;
	}

	public Vec2d normalized() {
		return divide(magnitude());
	}

	public Vec2d plus(final double subtrahendX, final double subtrahendY) {
		return new Vec2d(this).add(subtrahendX, subtrahendY);
	}

	public Vec2d plus(final Object subtrahend) {
		return new Vec2d(this).add(subtrahend);
	}

	public void set(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public void set(final Object other) {
		if (other instanceof final Point2D point)
			set(point.getX(), point.getY());
		else if (other instanceof final Vec2d vector)
			set(vector.x, vector.y);
		else
			set(Double.NaN, Double.NaN);
	}

	public void setX(final double x) {
		this.x = x;
	}

	public void setY(final double y) {
		this.y = y;
	}

	public Vec2d subtract(final double subtrahendX, final double subtrahendY) {
		this.x -= subtrahendX;
		this.y -= subtrahendY;

		return this;
	}

	public Vec2d subtract(final Object subtrahend) {
		if (subtrahend instanceof final Point2D point)
			return subtract(point.getX(), point.getY());
		else if (subtrahend instanceof final Vec2d vector)
			return subtract(vector.x, vector.y);
		else
			return subtract(Double.NaN, Double.NaN);
	}

	@Override
	public String toString() {
		return "<" + this.x + ", " + this.y + ">";
	}
}
