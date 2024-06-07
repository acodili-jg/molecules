package io.github.acodili_jg.molecules.client;

import io.github.acodili_jg.molecules.level.CollisionConsumer;
import io.github.acodili_jg.molecules.level.Level;
import io.github.acodili_jg.molecules.molecule.Molecule;
import io.github.acodili_jg.molecules.molecule.MoleculeRef;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.joml.Vector2d;

public class ClientLevel implements Level {
    private static Hash.Strategy<MoleculeRef> HASH_STRATEGY = new Strategy<MoleculeRef>() {
        @Override
        public boolean equals(MoleculeRef lhs, MoleculeRef rhs) {
            return (lhs == rhs) || (lhs != null && rhs != null && lhs.uuid().equals(rhs.uuid()));
        }

        @Override
        public int hashCode(MoleculeRef self) {
            return self.uuid().hashCode();
        }      
    };

    private final ObjectOpenCustomHashSet<Molecule> molecules;

    private final Set<? extends MoleculeRef> moleculeSet;

    private final Object2ReferenceOpenCustomHashMap<Molecule, DoubleOpenHashSet> associatedCollisionTimes;

    private final Double2ReferenceRBTreeMap<Object2ReferenceOpenCustomHashMap<Molecule, ObjectOpenCustomHashSet<Molecule>>> forecast;

    public ClientLevel() {
        this.molecules = new ObjectOpenCustomHashSet<>(HASH_STRATEGY);
        this.moleculeSet = Collections.unmodifiableSet(this.molecules);
        this.associatedCollisionTimes = new Object2ReferenceOpenCustomHashMap<>(HASH_STRATEGY);
        this.forecast = new Double2ReferenceRBTreeMap<>();
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
        return this.forecast.isEmpty() ? Double.POSITIVE_INFINITY : this.forecast.firstDoubleKey();
    }

    @Override
    public boolean consumeNextSimultaneousCollisions(final CollisionConsumer action) {
        Objects.requireNonNull(action, "Parameter action is null");
        if (this.forecast.isEmpty()) {
            return false;
        }

        final var collisionTime = this.forecast.firstDoubleKey();
        System.out.println(collisionTime);

        final var collisionGraph = this.forecast.remove(collisionTime);
        this.handleTimedCollision(collisionTime, collisionGraph, action);

        return true;
    }

    private void handleTimedCollision(
        final double time,
        final Object2ReferenceOpenCustomHashMap<Molecule, ObjectOpenCustomHashSet<Molecule>> graph,
        final CollisionConsumer action
    ) {
        final var newMolecules = new ObjectArrayList<Molecule>(graph.size());
        for (final var instantEntry: graph.entrySet()) {
            final var lhs = instantEntry.getKey();
            final var rhsSet = instantEntry.getValue();

            graph.remove(lhs);
            lhs.positionMut().fma(time, lhs.velocity());
            // lhs.velocityMut().div(rhsSet.size());
            graph.put(new MoleculeImpl(lhs), rhsSet);
        }
        for (final var instantEntry : graph.entrySet()) {
            final var lhs = instantEntry.getKey();
            final var rhsSet = instantEntry.getValue();
            ClientLevel.updateVelocitiesOnImpulse(lhs, rhsSet);
            newMolecules.add(lhs);

            // Accumulated time never resets, reverse instead
            // lhs.positionMut().fma(-time, lhs.velocity());
        }
        // ASSERTION: works with a custom hash strategy
        for (final var oldMolecule : newMolecules) {
            this.associatedCollisionTimes.get(oldMolecule).remove(time);
            this.forgetsForcasting(oldMolecule);
            this.molecules.remove(oldMolecule);
        }
        for (final MoleculeRef molecule : newMolecules) {
            System.out.println(((MoleculeImpl) molecule).toAltDebugString());
            action.accept(time, molecule.uuid(), molecule.velocity());
        }
        this.addAllMolecules(newMolecules);
    }

    private static void updateVelocitiesOnImpulse(final Molecule lhs, final Set<? extends MoleculeRef> rhsSet) {
        final var p1 = lhs.position();
        final var r1 = lhs.radius();
        final var v1 = new Vector2d(lhs.velocity());
        final var m1 = 2 * Math.PI * r1 * r1;
        final var newVelocity = lhs.velocityMut().set(0);

        for (final var rhs : rhsSet) {
            final var p2 = rhs.position();
            final var v2 = rhs.velocity();
            final var r2 = rhs.radius();
            final var m2 = 2 * Math.PI * r2 * r2;

            final var normal = new Vector2d(p2).sub(p1).div(r1 + r2);
            final var p = 2 * (v1.dot(normal) - v2.dot(normal)) / (m1 + m2);
            newVelocity.add(v1).fma(-p * m1, normal);
        }
        System.out.println("speed: " + newVelocity.length());

        // TODO do actual collision impulse
        // NOTE: either self-only or except-self
        // Except-self seems to make it easier to distribute impulse
        // when there are more than one collidee
        // lhs.velocityMut().set(0, 0);
    }

    private void addAllMolecules(final Collection<? extends Molecule> collection) {
        for (final var lhs : collection) {
            this.predictsCollisionsContaining(lhs);
            this.molecules.add(lhs);
        }
        collection.clear();
    }

    private void predictsCollisionsContaining(final Molecule lhs) {
        // ASSERTION: lhs is not yet a member of `this.molecules`
        for (final var rhs : this.molecules) {
            final var collisionTime = Level.calculateCollisionTime(lhs, rhs);

            final var instant = this
                .forecast
                .computeIfAbsent(collisionTime, key -> new Object2ReferenceOpenCustomHashMap<>(HASH_STRATEGY));
            buildInstant(instant, lhs, rhs);
            buildInstant(instant, rhs, lhs);

            this
                .associatedCollisionTimes
                .computeIfAbsent(lhs, key -> new DoubleOpenHashSet())
                .add(collisionTime);
            this
                .associatedCollisionTimes
                .computeIfAbsent(rhs, key -> new DoubleOpenHashSet())
                .add(collisionTime);
        }
    }

    private static void buildInstant(Object2ReferenceOpenCustomHashMap<Molecule, ObjectOpenCustomHashSet<Molecule>> instant, final Molecule key, final Molecule value) {
        // var map = instant.get(key);
        // if (map == null) {
        //     map = new ObjectOpenCustomHashSet<Molecule>(HASH_STRATEGY);
        //     instant.put(new MoleculeImpl(key), map);
        // }
        // map.add(value);
        instant.computeIfAbsent(key, k -> new ObjectOpenCustomHashSet<Molecule>(HASH_STRATEGY)).add(value);
    }

    private void forgetsForcasting(final Molecule molecule) {
        final var collisionTimes = this.associatedCollisionTimes.remove(molecule);

        for (
            final var iter = collisionTimes.iterator();
            iter.hasNext();
        ) {
            final var collisionTime = iter.nextDouble();
            final var instant = this.forecast.get(collisionTime);
            if (instant == null) {
                continue;
            }

            final var contacts = instant.remove(molecule);
            for (final var contact : contacts) {
                final var mutuals = instant.get(contact);
                mutuals.remove(molecule);
                if (mutuals.isEmpty()) {
                    instant.remove(contact);
                }
            }

            if (instant.isEmpty()) {
                this.forecast.remove(collisionTime);
            }
        }
    }
}
