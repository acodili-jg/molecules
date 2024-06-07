package io.github.acodili_jg.molecules.client;

import io.github.acodili_jg.molecules.level.CollisionConsumer;
import io.github.acodili_jg.molecules.level.Level;
import io.github.acodili_jg.molecules.molecule.Molecule;
import io.github.acodili_jg.molecules.molecule.MoleculeRef;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.joml.Vector2d;

public class ClientLevel implements Level {
    private final CollisionForecast forecast;

    private final ObjectOpenCustomHashSet<Molecule> molecules;

    private final Set<? extends MoleculeRef> moleculeSet;

    // Pre-allocated buffer vector.
    private final Vector2d vector1;

    public ClientLevel() {
        this.molecules = new ObjectOpenCustomHashSet<>(Molecule.HASH_STRATEGY);

        final var moleculeSet = Collections.unmodifiableSet(this.molecules);
        this.forecast = new CollisionForecast(moleculeSet);
        this.moleculeSet = moleculeSet;

        // Pre-allocations
        this.vector1 = new Vector2d();
    }

    @Override
    public void addMovedMolecule(final Molecule molecule) {
        this.forecast.add(molecule);
        this.molecules.add(molecule);
    }

    @Override
    public void addClonedMolecule(final MoleculeRef molecule) {
        this.addMovedMolecule(new MoleculeImpl(molecule));
    }

    @Override
    public Set<? extends MoleculeRef> moleculeSet() {
        return this.moleculeSet;
    }

    @Override
    public double peekCollisionTime() {
        return this.forecast.firstCollisionTime();
    }

    @Override
    public boolean consumeNextSimultaneousCollisions(final CollisionConsumer action) {
        Objects.requireNonNull(action, "Parameter action is null");
        if (this.forecast.isEmpty()) {
            return false;
        }

        final var collision = this.forecast.popCollision();
        final var time = collision.getDoubleKey();
        final var system = collision.getValue();

        this.handleTimedCollision(time, system, action);

        return true;
    }

    private void handleTimedCollision(
        final double time,
        final Object2ReferenceOpenCustomHashMap<Molecule, ObjectOpenCustomHashSet<Molecule>> system,
        final CollisionConsumer action
    ) {
        final var accumulatedTime = this.forecast.accumulatedTime();

        final var n = system.size();
        final var lhsArr = new Molecule[n];
        final var rhsSetArr = new Set[n];
        final var vectorArr = new Vector2d[n];
        {
            final var iter = system.entrySet().iterator();
            var idx = 0;
            while (iter.hasNext()) {
                final var entry = iter.next();
                final var lhs = entry.getKey();
                final var rhsSet = entry.getValue();

                iter.remove();
                lhs.positionMut().fma(accumulatedTime, lhs.velocity());
                lhs.velocityMut().div(rhsSet.size());

                lhsArr[idx] = lhs;
                rhsSetArr[idx] = rhsSet;
                vectorArr[idx] = new Vector2d();
                idx++;
            }
        }

        for (var idx = 0; idx < n; idx++) {
            final var lhs = lhsArr[idx];
            final var rhsSet = rhsSetArr[idx];
            ClientLevel.updateVelocitiesOnImpulse(lhs, rhsSet, this.vector1, vectorArr[idx]);

        }

        for (var idx = 0; idx < n; idx++) {
            final var lhs = lhsArr[idx];

            lhs.setVelocity(vectorArr[idx]);
            // Accumulated time never resets, reverse instead
            lhs.positionMut().fma(-accumulatedTime, lhs.velocity());
        }

        for (final var molecule : lhsArr) {
            // hash by UUID
            molecule.uuid();
            this.forecast.remove(molecule);
            this.molecules.remove(molecule);

            action.accept(time, molecule.uuid(), molecule.velocity());
            this.addMovedMolecule(molecule);
        }
    }

    private static void updateVelocitiesOnImpulse(final MoleculeRef lhs, final Set<? extends MoleculeRef> rhsSet, final Vector2d normal, final Vector2d newVelocity) {
        final var p1 = lhs.position();
        final var r1 = lhs.radius();
        final var v1 = lhs.velocity();
        final var m1 = 2 * Math.PI * r1 * r1;

        for (final var rhs : rhsSet) {
            final var p2 = rhs.position();
            final var v2 = rhs.velocity();
            final var r2 = rhs.radius();
            final var m2 = 2 * Math.PI * r2 * r2;

            normal.set(p2).sub(p1).div(r1 + r2);
            final var p = 2 * (v1.dot(normal) - v2.dot(normal)) / (m1 + m2);
            newVelocity.add(v1).fma(-p * m2, normal);
        }
    }
}
