package io.github.acodili.jg.molecules.client.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.Objects;

import io.github.acodili.jg.molecules.math.Vec2d;
import io.github.acodili.jg.molecules.molecule.Molecule;
import io.github.acodili.jg.molecules.util.SelectionMode;

public class BasicMoleculeRenderer implements MoleculeRenderer {
    private final Rectangle2D.Double frame;

    private final Ellipse2D.Double base;

    public BasicMoleculeRenderer() {
        this.base = new Ellipse2D.Double();
        this.frame = new Rectangle2D.Double();
    }

    protected Rectangle2D.Double getFrame() {
        return getFrame(new Rectangle2D.Double());
    }

    protected <S extends RectangularShape> S getFrame(final S shape) {
        if (shape == null)
            return null;

        shape.setFrame(this.frame);

        return shape;
    }

    protected void renderBase(final Graphics2D graphics, final Paint paint) {
        final var oldPaint = graphics.getPaint();

        graphics.setPaint(paint);
        graphics.fill(this.base);
        graphics.setPaint(oldPaint);
    }

    @Override
    public void renderBase(final Graphics2D graphics, final Vec2d position, final double radius, final Paint paint) {
        Objects.requireNonNull(graphics, "Parameter graphics is null");
        Objects.requireNonNull(position, "Parameter position is null");

        setFrameFromCenter(position, radius);
        setBaseFrame();
        renderBase(graphics, paint);
    }

    protected void renderOutline(final Graphics2D graphics, final Paint paint) {
        final var oldPaint = graphics.getPaint();
        final var oldStroke = graphics.getStroke();

        graphics.setPaint(paint);
        graphics.setStroke(OUTLINE_STROKE);
        graphics.draw(this.base);
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }

    @Override
    public void renderOutline(final Graphics2D graphics, final Vec2d position, final double radius, final Paint paint) {
        Objects.requireNonNull(graphics, "Parameter graphics is null");
        Objects.requireNonNull(position, "Parameter position is null");

        setFrameFromCenter(position, radius);
        setBaseFrame();
        renderOutline(graphics, paint);
    }

    protected void renderSelectionHint(final Graphics2D graphics, final SelectionMode selectionMode) {
        final var oldPaint = graphics.getPaint();
        final var oldStroke = graphics.getStroke();
        final var paint = selectionMode.getSelectionBorderColor();

        graphics.setPaint(paint);
        graphics.setStroke(SELECTION_HINT_STROKE);
        graphics.draw(this.frame);
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }

    @Override
    public void renderSelectionHint(final Graphics2D graphics, final Vec2d position, final double radius, final SelectionMode selectionMode) {
        Objects.requireNonNull(graphics, "Parameter graphics is null");
        Objects.requireNonNull(position, "Parameter position is null");
        Objects.requireNonNull(selectionMode, "Parameter selectionMode is null");

        setFrameFromCenter(position, radius);
        renderSelectionHint(graphics, selectionMode);
    }

    protected void setBaseFrame() {
        this.base.setFrame(this.frame);
    }

    protected void setFrameFromCenter(final Vec2d position, final double radius) {
        final var diameter = radius * 2.0d;

        this.frame.setFrame(position.x - radius, position.y - radius, diameter, diameter);
    }
}
