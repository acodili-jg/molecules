package io.github.acodili.jg.molecules.client.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Objects;

import io.github.acodili.jg.molecules.math.Vec2d;
import io.github.acodili.jg.molecules.molecule.Molecule;
import io.github.acodili.jg.molecules.util.SelectionMode;

public interface MoleculeRenderer {
    Paint CONTROLLING_OUTLINE_PAINT = Color.MAGENTA;

    Paint OUTLINE_PAINT = Color.ORANGE;

	Stroke OUTLINE_STROKE = new BasicStroke(3.0f);

	Stroke SELECTION_HINT_STROKE = new BasicStroke(5.0f);

    default void renderBase(final Graphics2D graphics, final Molecule molecule) {
        Objects.requireNonNull(graphics, "Parameter graphics is null");
        Objects.requireNonNull(molecule, "Parameter molecule is null");

        renderBase(graphics, molecule.position, molecule.radius, molecule.paint);
    }

    void renderBase(Graphics2D graphics, Vec2d position, double radius, Paint paint);

    default void renderOutline(final Graphics2D graphics, final Molecule molecule, final boolean controlled) {
        Objects.requireNonNull(graphics, "Parameter graphics is null");
        Objects.requireNonNull(molecule, "Parameter molecule is null");

        renderOutline(graphics, molecule.position, molecule.radius, controlled ? CONTROLLING_OUTLINE_PAINT : OUTLINE_PAINT);
    }

    void renderOutline(Graphics2D graphics, Vec2d position, double radius, Paint paint);

    default void renderSelectionHint(final Graphics2D graphics, final Molecule molecule, final SelectionMode selectionMode) {
        Objects.requireNonNull(graphics, "Parameter graphics is null");
        Objects.requireNonNull(molecule, "Parameter molecule is null");
        Objects.requireNonNull(selectionMode, "Parameter selectionMode is null");

        renderSelectionHint(graphics, molecule.position, molecule.radius, selectionMode);
    }

    void renderSelectionHint(Graphics2D graphics, Vec2d position, double radius, SelectionMode selectionMode);
}
