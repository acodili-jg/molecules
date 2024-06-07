package io.github.acodili_jg.molecules.molecule;

import org.joml.Vector2d;
import org.joml.Vector2dc;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Hash.Strategy;

public interface Molecule extends MoleculeRef {
    Hash.Strategy<MoleculeRef> HASH_STRATEGY = new Strategy<MoleculeRef>() {
        @Override
        public boolean equals(MoleculeRef lhs, MoleculeRef rhs) {
            return (lhs == rhs) || (lhs != null && rhs != null && lhs.uuid().equals(rhs.uuid()));
        }

        @Override
        public int hashCode(MoleculeRef self) {
            return self.uuid().hashCode();
        }      
    };

    void setRadius(final double radius);

    Vector2d positionMut();

    default void setPosition(final Vector2dc position) {
        this.positionMut().set(position);
    }

    Vector2d velocityMut();

    default void setVelocity(final Vector2dc velocity) {
        this.velocityMut().set(velocity);
    }
}
