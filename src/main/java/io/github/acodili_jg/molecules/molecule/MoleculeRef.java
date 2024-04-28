package io.github.acodili_jg.molecules.molecule;

import java.util.UUID;
import org.joml.Vector2dc;

public interface MoleculeRef {
    UUID uuid();
    double radius();
    Vector2dc position();
    Vector2dc velocity();
}
