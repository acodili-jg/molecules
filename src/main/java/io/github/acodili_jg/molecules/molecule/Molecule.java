package io.github.acodili_jg.molecules.molecule;

import org.joml.Vector2d;
import org.joml.Vector2dc;

public interface Molecule extends MoleculeRef {
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
