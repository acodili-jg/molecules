package io.github.acodili_jg.molecules.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import org.joml.Vector2d;

public class Renderer {
    // minimize object allocations by reusing these:
    private final Ellipse2D.Double ellipse;
    private final Line2D.Double line;
    private final Vector2d vec;

    public Renderer() {
        this.ellipse = new Ellipse2D.Double();
        this.line = new Line2D.Double();
        this.vec = new Vector2d();
    }

    public void render(
        final BufferStrategy strategy,
        final int width,
        final int height,
        final double time,
        final Iterable<? extends MoleculeImpl> molecules
    ) {
        do {
            // The following loop ensures that the contents of the drawing
            // buffer are consistent in case the underlying surface was
            // recreated
            do {
                // Get a new graphics context every time through the loop to
                // make sure the strategy is validated
                final var graphics = strategy.getDrawGraphics();

                graphics.clearRect(0, 0, width, height);

                if (graphics instanceof final Graphics2D graphics2d) {
                    this.renderAll(graphics2d, time, molecules);
                } else {
                    // AWT/Swing implementation lacks Graphics2D capabilities
                    throw new InternalError("Legacy rendering not yet implemented");
                }

                // Dispose the graphics
                graphics.dispose();

                // Repeat the rendering if the drawing buffer contents
                // were restored
            } while (strategy.contentsRestored());

            // Display the buffer
            strategy.show();

            // Repeat the rendering if the drawing buffer was lost
        } while (strategy.contentsLost());
    }

    private void renderAll(
        final Graphics2D graphics,
        final double time,
        final Iterable<? extends MoleculeImpl> molecules
    ) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (final var molecule : molecules) {
            this.renderSingle(graphics, time, molecule);
        }
    }

    private void renderSingle(final Graphics2D graphics, final double time, final MoleculeImpl molecule) {
        this.vec.set(molecule.position());

        final var radius = molecule.radius();
        // this.ellipse.x = this.vec.x() - radius;
        // this.ellipse.y = this.vec.y() - radius;
        this.ellipse.width = this.ellipse.height = 2 * radius;
        graphics.setColor(molecule.color());
        // graphics.draw(this.ellipse);

        // line.x1 = this.vec.x();
        // line.y1 = this.vec.y();
        this.vec.fma(time, molecule.velocity());
        // line.x2 = this.vec.x();
        // line.y2 = this.vec.y();
        // graphics.draw(line);

        this.ellipse.x = this.vec.x() - radius;
        this.ellipse.y = this.vec.y() - radius;
        graphics.fill(this.ellipse);

        // line.x2 = (line.x1 = this.vec.x()) + molecule.velocity().x();
        // line.y2 = (line.y1 = this.vec.y()) + molecule.velocity().y();
        // graphics.setColor(Color.BLACK);
        // graphics.draw(line);
    }
}
