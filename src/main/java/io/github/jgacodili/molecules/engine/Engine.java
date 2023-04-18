package io.github.jgacodili.molecules.engine;

import io.github.jgacodili.molecules.level.Level;
import io.github.jgacodili.molecules.math.Vec2d;
import io.github.jgacodili.molecules.molecule.Molecule;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Engine {
	public static final double DEFAULT_UPDATE_INTERVAL = 1.0d / 16.0d;

	private Level level;

	private double updateInterval;

	public Engine() {
		this.updateInterval = DEFAULT_UPDATE_INTERVAL;
	}

	public Level getLevel() {
		return this.level;
	}

	public double getUpdateInterval() {
		return this.updateInterval;
	}

	public void setLevel(final Level level) {
		this.level = level;
	}

	public void setUpdateInterval(final double updateIntervalInSeconds) {
		if (!Double.isFinite(updateIntervalInSeconds))
			throw new IllegalArgumentException("Parameter updateIntervalInSeconds is not finite");
		if (updateIntervalInSeconds <= 0.0d)
			throw new IllegalArgumentException("Parameter updateIntervalInSeconds is nonpositive");

		this.updateInterval = updateIntervalInSeconds;
	}

	public void update() {
		updateStreamed();
	}

	private void updateInteractingForce(final Vec2d position0, final Vec2d velocity0, final double radius0,
	        final double area0, final Vec2d force0, final Entry<Long, Molecule> otherEntry) {
		final var molecule1 = otherEntry.getValue();
		final var position1 = molecule1.position;
		final var velocity1 = molecule1.velocity;
		final var radius1 = molecule1.radius;
		final var area1 = Math.PI * radius1 * radius1;

		final var relativePosition = position1.minus(position0);

		if (relativePosition.x == 0.0d && relativePosition.y == 0.0d)
			return;

		final var relativeVelocity = velocity0.minus(velocity1);

		if (relativePosition.dot(relativeVelocity) <= 0.0d)
			return;

		final var distance = relativePosition.magnitude();

		if (distance >= radius0 + radius1)
			return;

		final var collisionNormal = relativePosition.dividedBy(distance);
		final var impulse = 2.0d * (position0.dot(collisionNormal) - position1.dot(collisionNormal)) / (area0 + area1);

		Vec2d.fma(collisionNormal, impulse * area1, force0, force0);
	}

	public void updateLooped() {
		final var level = getLevel();

		if (level == null)
			throw new IllegalStateException("Level is not set");

		final var updateInterval = this.updateInterval;
		final var molecules = level.getMolecules();
		final var forces = new HashMap<Long, Vec2d>();

		for (final var molecule0Entry : molecules.entrySet()) {
			final var id0 = molecule0Entry.getKey();
			final var molecule0 = molecule0Entry.getValue();

			final var position0 = molecule0.position;
			final var velocity0 = molecule0.velocity;
			final var radius0 = molecule0.radius;
			final var area0 = Math.PI * radius0 * radius0;
			final var force0 = new Vec2d();

			for (final var otherEntry : molecules.entrySet())
				updateInteractingForce(position0, velocity0, radius0, area0, force0, otherEntry);

			forces.put(id0, force0);
		}

		for (final var moleculeEntry : molecules.entrySet()) {
			final var id = moleculeEntry.getKey();
			final var molecule = moleculeEntry.getValue();

			final var position = molecule.position;
			final var velocity = molecule.velocity;

			final var force = forces.get(id);

			if (force != null)
				velocity.add(force);

			Vec2d.fma(velocity, updateInterval, position, position);
		}
	}

	public void updateStreamed() {
		final var level = getLevel();

		if (level == null)
			throw new IllegalStateException("Level is not set");

		final var updateInterval = this.updateInterval;
		final var molecules = level.getMolecules();

		final Map<Long, Vec2d> forces = molecules.entrySet().parallelStream().map(entry -> {
			final var molecule0 = entry.getValue();
			final var position0 = molecule0.position;
			final var velocity0 = molecule0.velocity;
			final var radius0 = molecule0.radius;
			final var area0 = Math.PI * radius0 * radius0;
			final var force0 = new Vec2d();

			molecules.entrySet()
			         .parallelStream()
			         .filter(otherEntry -> !entry.equals(otherEntry))
			         .forEach(otherEntry -> updateInteractingForce(position0, velocity0, radius0, area0, force0,
			                 otherEntry));

			return Map.entry(entry.getKey(), force0);
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		molecules.entrySet().parallelStream().forEach(entry -> {
			final var molecule = entry.getValue();
			final var id = entry.getKey();
			final var position = molecule.position;
			final var velocity = molecule.velocity;

			final var force = forces.get(id);

			if (force != null)
				velocity.add(force);

			Vec2d.fma(velocity, updateInterval, position, position);
		});
	}
}
