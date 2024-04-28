package io.github.acodili_jg.molecules.molecule;

import java.util.Objects;
import java.util.UUID;
import org.joml.Vector2d;
import org.joml.Vector2dc;

public class MoleculeObject implements Molecule {
    public static final double DEFAULT_RADIUS = 25.0;

    private UUID uuid;
    private double radius;
    private final Vector2d position;
    private final Vector2d velocity;

    public MoleculeObject() {
        this.uuid = UUID.randomUUID();
        this.position = new Vector2d();
        this.velocity = new Vector2d();
        this.radius = DEFAULT_RADIUS;
    }

    public MoleculeObject(final MoleculeRef other) {
        this(
            other.uuid(),
            other.radius(),
            other.position(),
            other.velocity()
        );
    }

    public MoleculeObject(
        final UUID uuid,
        final double radius,
        final Vector2dc position,
        final Vector2dc velocity
    ) {
        this.uuid = Objects.requireNonNull(uuid, "Parameter uuid is null");
        this.radius = radius;
        this.position = new Vector2d(position);
        this.velocity = new Vector2d(velocity);
    }

    @Override
    public final UUID uuid() {
        return this.uuid;
    }

    protected void setUUID(final UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid);
    }

    @Override
    public double radius() {
        return this.radius;
    }

    @Override
    public void setRadius(final double radius) {
        this.radius = radius;
    }

    @Override
    public Vector2dc position() {
        return this.position;
    }

    @Override
    public Vector2d positionMut() {
        return this.position;
    }

    @Override
    public Vector2dc velocity() {
        return this.velocity;
    }

    @Override
    public Vector2d velocityMut() {
        return this.velocity;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } if (obj instanceof final MoleculeRef other) {
            return Objects.equals(this.uuid(), other.uuid())
                && this.radius() == other.radius()
                && Objects.equals(this.position(), other.position())
                && Objects.equals(this.velocity(), other.velocity());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.uuid(),
            this.radius(),
            this.position(),
            this.velocity()
        );
    }

    public String toAltDebugString() {
        return this.getClass().getCanonicalName()
            + """
             {
                uuid: \
                """
            + this.uuid()
            + """
            ,
                radius: \
                """
            + this.radius()
            + """
            ,
                position: \
                """
            + this.position() + """
            ,
                velocity: \
                """
            + this.velocity() + """
            ,
            }""";
    }
}
