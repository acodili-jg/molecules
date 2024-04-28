package io.github.acodili_jg.molecules.client;

import io.github.acodili_jg.molecules.molecule.Molecule;
import io.github.acodili_jg.molecules.molecule.MoleculeObject;
import io.github.acodili_jg.molecules.molecule.MoleculeRef;
import java.awt.Color;
import java.util.Objects;
import java.util.UUID;
import org.joml.Vector2d;
import org.joml.Vector2dc;

public class MoleculeImpl extends MoleculeObject {
    private static final Color DEFAULT_COLOR_MARKER = new Color(0xffffff);

    private static final Color toColor(final Object obj) {
        return new Color(Objects.hashCode(obj));
    }

    private final Color defaultColor;
    private Color color;

    public MoleculeImpl() {
        super();
        this.defaultColor = toColor(this.uuid());
        this.color = color;
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
        this(
            uuid,
            radius,
            position,
            velocity,
            DEFAULT_COLOR_MARKER
        );
    }

    public MoleculeImpl(
        final UUID uuid,
        final double radius,
        final Vector2dc position,
        final Vector2dc velocity,
        final Color color
    ) {
        super(
            uuid,
            radius,
            position,
            velocity
        );
        this.defaultColor = toColor(this.uuid());
        if (color == DEFAULT_COLOR_MARKER) {
            this.color = this.defaultColor;
        } else {
            this.color = Objects.requireNonNull(color, "Parameter color is null");
        }
    }

    public final Color defaultColor() {
        return this.defaultColor;
    }

    public Color color() {
        return this.color;
    }

    public void setColor(final Color color) {
        this.color = Objects.requireNonNull(color, "Parameter color is null");
    }
}
