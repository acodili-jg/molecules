package io.github.acodili_jg.molecules.client;

import io.github.acodili_jg.molecules.level.CollisionConsumer;
import io.github.acodili_jg.molecules.level.Level;
import io.github.acodili_jg.molecules.molecule.Molecule;
import io.github.acodili_jg.molecules.molecule.MoleculeRef;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ClientLevel implements Level {
    // TODO: use CustomHash instead of erroneously redefining hashCode()

    private final ObjectOpenHashSet<Molecule> molecules;

    private final Set<? extends MoleculeRef> moleculeSet;

    private final Object2ReferenceOpenHashMap<Molecule, DoubleOpenHashSet> associatedCollisionTimes;

    private final Double2ReferenceRBTreeMap<Object2ReferenceOpenHashMap<Molecule, ObjectOpenHashSet<Molecule>>> forecast;

    public ClientLevel() {
        this.molecules = new ObjectOpenHashSet<>();
        this.moleculeSet = Collections.unmodifiableSet(this.molecules);
        this.associatedCollisionTimes = new Object2ReferenceOpenHashMap<>();
        this.forecast = new Double2ReferenceRBTreeMap<>();
    }

    @Override
    public void addMolecule(final MoleculeRef moleculeRef) {
        final var molecule = new MoleculeImpl(moleculeRef);
        this.predictsCollisionsContaining(molecule);
        this.molecules.add(molecule);
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
        final Object2ReferenceOpenHashMap<Molecule, ObjectOpenHashSet<Molecule>> graph,
        final CollisionConsumer action
    ) {
        final var newMolecules = new ObjectArrayList<Molecule>(graph.size());
        for (final var instantEntry : graph.entrySet()) {
            final var lhs = new MoleculeImpl(instantEntry.getKey());
            lhs.positionMut().fma(time, lhs.velocity());

            ClientLevel.updateVelocitiesOnImpulse(lhs, instantEntry.getValue());

            // Accumulated time never resets, reverse instead
            lhs.positionMut().fma(-time, lhs.velocity());

            newMolecules.add(lhs);

        }
        for (final var oldMolecule : graph.keySet()) {
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

    private static void updateVelocitiesOnImpulse(final Molecule lhs, final Set<? extends Molecule> rhsSet) {
        // TODO do actual collision impulse
        // NOTE: either self-only or except-self
        // Except-self seems to make it easier to distribute impulse
        // when there are more than one collidee
        lhs.velocityMut().set(0, 0);
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
                .computeIfAbsent(collisionTime, key -> new Object2ReferenceOpenHashMap<>());
            instant
                .computeIfAbsent(lhs, key -> new ObjectOpenHashSet<>())
                .add(rhs);
            instant
                .computeIfAbsent(rhs, key -> new ObjectOpenHashSet<>())
                .add(lhs);

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
