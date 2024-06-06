package io.github.acodili_jg.molecules.level;

import io.github.acodili_jg.molecules.molecule.MoleculeObject;
import io.github.acodili_jg.molecules.molecule.MoleculeRef;
import java.util.Objects;
import java.util.UUID;
import org.joml.Vector2dc;

public class MoleculeImpl extends MoleculeObject {
    public MoleculeImpl() {
        super();
    }

    public MoleculeImpl(final MoleculeRef other) {
        this(
            other.uuid(),
            other.radius(),
            other.position(),
            other.velocity()
        );
    }

    public MoleculeImpl(
        final UUID uuid,
        final double radius,
        final Vector2dc position,
        final Vector2dc velocity
    ) {
        super(
            uuid,
            radius,
            position,
            velocity
        );
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } if (obj instanceof final MoleculeRef other) {
            return Objects.equals(this.uuid(), other.uuid());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid());
    }

    public String toAltDebugString() {
        return """
            MoleculeImpl {
                uuid: \
                """ + this.uuid() + """
            ,
                position: \
                """ + this.position() + """
            ,
                velocity: \
                """ + this.velocity() + """
            ,
                radius: \
                """ + this.radius() + """
            ,
            }""";
    }
}
