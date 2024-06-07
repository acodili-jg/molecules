package io.github.acodili_jg.molecules.client;

import io.github.acodili_jg.molecules.level.Level;
import io.github.acodili_jg.molecules.molecule.Molecule;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;

public class CollisionForecast {
    private final Double2ReferenceRBTreeMap<Object2ReferenceOpenCustomHashMap<Molecule, ObjectOpenCustomHashSet<Molecule>>> collisionTimeToSystem;

    private final Object2ReferenceOpenCustomHashMap<Molecule, DoubleOpenHashSet> constituentToCollisionTimeSet;

    private final Iterable<? extends Molecule> molecules;

    private double accumulatedTime;

    public CollisionForecast(final Iterable<? extends Molecule> molecules) {
        this.collisionTimeToSystem = new Double2ReferenceRBTreeMap<>();
        this.constituentToCollisionTimeSet = new Object2ReferenceOpenCustomHashMap<>(Molecule.HASH_STRATEGY);
        this.molecules = molecules;
    }

    public double accumulatedTime() {
        return this.accumulatedTime;
    }

    public int size() {
        return this.collisionTimeToSystem.size();
    }

    public boolean isEmpty() {
        return this.collisionTimeToSystem.isEmpty();
    }

    public double firstCollisionTime() {
        if (this.collisionTimeToSystem.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        } else {
            return this.collisionTimeToSystem.firstDoubleKey();
        }
    }

    public Double2ReferenceMap.Entry<Object2ReferenceOpenCustomHashMap<Molecule, ObjectOpenCustomHashSet<Molecule>>> popCollision() {
        final var collision = this.collisionTimeToSystem.double2ReferenceEntrySet().removeFirst();
        this.accumulatedTime += collision.getDoubleKey() - this.accumulatedTime;
        return collision;
    }

    public void add(final Molecule lhs) {
        final var accumulatedTime = this.accumulatedTime;

        // ASSERTION: lhs is not yet a member of `this.molecules`
        for (final var rhs : this.molecules) {
            final var collisionTime = Level.calculateCollisionTime(lhs, rhs);
            if (collisionTime <= accumulatedTime) continue;

            final var system = this
                .collisionTimeToSystem
                .computeIfAbsent(collisionTime, key -> new Object2ReferenceOpenCustomHashMap<>(Molecule.HASH_STRATEGY));
            system.computeIfAbsent(lhs, k -> new ObjectOpenCustomHashSet<Molecule>(Molecule.HASH_STRATEGY)).add(rhs);
            system.computeIfAbsent(rhs, k -> new ObjectOpenCustomHashSet<Molecule>(Molecule.HASH_STRATEGY)).add(lhs);

            this
                .constituentToCollisionTimeSet
                .computeIfAbsent(lhs, key -> new DoubleOpenHashSet())
                .add(collisionTime);
            this
                .constituentToCollisionTimeSet
                .computeIfAbsent(rhs, key -> new DoubleOpenHashSet())
                .add(collisionTime);
        }
    }

    public void remove(final Molecule molecule) {
        final var timeSet = this.constituentToCollisionTimeSet.remove(molecule);

        for (
            final var iter = timeSet.iterator();
            iter.hasNext();
        ) {
            final var time = iter.nextDouble();
            final var system = this.collisionTimeToSystem.get(time);
            if (system == null) {
                continue;
            }

            final var contacts = system.remove(molecule);
            if (contacts != null) {
                for (final var contact : contacts) {
                    final var mutuals = system.get(contact);
                    mutuals.remove(molecule);
                    if (mutuals.isEmpty()) {
                        system.remove(contact);
                    }
                }
            }

            if (system.isEmpty()) {
                this.collisionTimeToSystem.remove(time);
            }
        }
    }
}
