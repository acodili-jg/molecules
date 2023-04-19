package io.github.acodili.jg.molecules.client.renderer;

import static io.github.acodili.jg.molecules.client.renderer.context.FallbackRenderContext.FALLBACK_CONTEXT;
import static io.github.acodili.jg.molecules.util.SelectionMode.NONE;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import io.github.acodili.jg.molecules.client.renderer.context.RenderContext;
import io.github.acodili.jg.molecules.level.Level;
import io.github.acodili.jg.molecules.math.Vec2d;
import io.github.acodili.jg.molecules.molecule.Molecule;
import io.github.acodili.jg.molecules.util.SelectionMode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

public class GameRenderer extends JPanel {
	private static final Color ACCELERATE_COLOR;

	private static final Color HIGHLIGHT_COLOR;

	private static final BasicStroke HIGHLIGHT_STROKE;

	private static final BasicStroke SELECTION_STROKE;

	private static final Color VELOCITY_COLOR;

	private static final BasicStroke VELOCITY_STROKE;

	@Serial
	private static final long serialVersionUID = 1L;

	static {
		ACCELERATE_COLOR = MAGENTA;
		HIGHLIGHT_COLOR = ORANGE;
		VELOCITY_COLOR = RED;

		HIGHLIGHT_STROKE = new BasicStroke(3.0f);
		SELECTION_STROKE = HIGHLIGHT_STROKE;
		VELOCITY_STROKE = new BasicStroke();
	}

	private RenderContext context;

	private final Rectangle2D.Double moleculeFrame;

	private final Ellipse2D.Double moleculeShape;

	private final Rectangle2D.Double selectionShape;

	private final Line2D.Double velocityShape;

	public GameRenderer() {
		this.moleculeShape = new Ellipse2D.Double();
		this.selectionShape = new Rectangle2D.Double();
		this.moleculeFrame = new Rectangle2D.Double();
		this.velocityShape = new Line2D.Double();
	}

	private Collection<? extends String> collectDebugLines(final int recordedFPS, final long updateMillis,
	        final int taskCount, final int moleculeCount) {
		final var debugLines = new ArrayList<String>();

		debugLines.add("Client");
		debugLines.add(recordedFPS != -1 ? recordedFPS + " FPS" : "??? FPS");
		debugLines.add("");
		debugLines.add("Server");
		debugLines.add(updateMillis != -1 ? updateMillis + " ms" : "???? ms");
		debugLines.add("Tasks: " + taskCount);
		debugLines.add("Molecules: " + moleculeCount);

		return debugLines;
	}

	public RenderContext getContext() {
		return this.context;
	}

	protected void paintBaseMolecule(final Graphics2D graphics, final Paint paint) {
		this.moleculeShape.setFrame(this.moleculeFrame);
		graphics.setPaint(paint);
		graphics.fill(this.moleculeShape);
	}

	@Override
	protected void paintComponent(final Graphics graphics) {
		super.paintComponent(graphics);

		final var level = this.context.getLevel();

		if (level == null)
			return;

		paintLevel((Graphics2D) graphics, level);
	}

	protected void paintDebugInformation(final Graphics2D graphics, final int width, final Color foreground,
	        final Collection<? extends String> lines) {
		final var fontMetrics = graphics.getFontMetrics();
		var x = 0.0f;
		var y = 0.0f;

		graphics.setColor(foreground);

		for (final var line : lines) {
			final var lineBounds = fontMetrics.getStringBounds(line, graphics);
			x = width - (float) lineBounds.getWidth() - 6.0f;
			y += (float) lineBounds.getHeight();
			graphics.drawString(line, x, y);
		}
	}

	protected void paintLevel(final Graphics2D graphics, final Level level) {
		final var foreground = getForeground();
		// final var height = getHeight();
		final var width = getWidth();
		final var molecules = level.getMolecules();

		final var context = Objects.requireNonNullElse(getContext(), FALLBACK_CONTEXT);
		final var modifyingVelocities = context.isModifyingVelocities();
		final var moleculeCount = molecules.size();
		final var mousePosition = context.getMousePosition();
		final var mousePressedPosition = context.getMousePressedPosition();
		final var recordedFPS = context.getRecordedFPS();
		final var selectedMolecules = context.getSelectedMolecules();
		final var selectionMode = context.getSelectionMode();
		final var taskCount = context.getTaskCount();
		final var translating = context.isTranslating();
		final var updateMillis = context.getUpdateMillis();

		graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

		if (selectionMode != NONE)
			this.selectionShape.setFrameFromDiagonal(mousePressedPosition.x, mousePressedPosition.y, mousePosition.x,
			        mousePosition.y);

		if (translating)
			graphics.translate(mousePosition.x - mousePressedPosition.x, mousePosition.y - mousePressedPosition.y);

		paintMolecules(graphics, molecules, modifyingVelocities, selectedMolecules, selectionMode);
		paintSelection(graphics, selectionMode, this.selectionShape, true, true);

		if (translating)
			graphics.translate(mousePressedPosition.x - mousePosition.x, mousePressedPosition.y - mousePosition.y);

		final var debugLines = collectDebugLines(recordedFPS, updateMillis, taskCount, moleculeCount);
		paintDebugInformation(graphics, width, foreground, debugLines);
	}

	protected void paintMolecule(final Graphics2D graphics, final Long id, final Molecule molecule,
	        final boolean modifyingVelocities, final Set<Long> selectedMolecules, final SelectionMode selectionMode) {
		final var paint = molecule.paint;
		final var position = molecule.position;
		final var velocity = molecule.velocity;
		final var radius = molecule.radius;
		final var diameter = radius * 2.0d;
		final var selected = selectedMolecules.contains(id);

		this.moleculeFrame.setFrame(position.x - radius, position.y - radius, diameter, diameter);

		paintMoleculeSelection(graphics, selectionMode, selected);

		if (selected && modifyingVelocities)
			paintMoleculeVelocity(graphics, position, velocity, radius);

		paintBaseMolecule(graphics, paint);

		if (selected)
			paintSelectionHighlight(graphics, modifyingVelocities, this.moleculeShape);
	}

	protected void paintMolecules(final Graphics2D graphics, final ConcurrentHashMap<Long, Molecule> molecules,
	        final boolean modifyingVelocities, final Set<Long> selectedMolecules, final SelectionMode selectionMode) {
		for (final var moleculeEntry : molecules.entrySet()) {
			final var id = moleculeEntry.getKey();
			final var molecule = moleculeEntry.getValue();

			paintMolecule(graphics, id, molecule, modifyingVelocities, selectedMolecules, selectionMode);
		}
	}

	protected void paintMoleculeSelection(final Graphics2D graphics, final SelectionMode selectionMode,
	        final boolean selected) {
		this.moleculeFrame.x -= 2.5d;
		this.moleculeFrame.y -= 2.5d;
		this.moleculeFrame.width += 5.0d;
		this.moleculeFrame.height += 5.0d;

		paintSelection(graphics, selectionMode, this.moleculeFrame, selected, false);

		this.moleculeFrame.x += 2.5d;
		this.moleculeFrame.y += 2.5d;
		this.moleculeFrame.width -= 5.0d;
		this.moleculeFrame.height -= 5.0d;
	}

	protected void paintMoleculeVelocity(final Graphics2D graphics, final Vec2d position, final Vec2d velocity,
	        final double radius) {
		final var relativeSurfacePoint = Vec2d.euler(velocity.atan());
		final var surfacePoint = Vec2d.fma(relativeSurfacePoint, radius, position, relativeSurfacePoint);

		this.velocityShape.setLine(surfacePoint.x, surfacePoint.y, surfacePoint.x + velocity.x,
		        surfacePoint.y + velocity.y);
		graphics.setColor(VELOCITY_COLOR);
		graphics.setStroke(VELOCITY_STROKE);
		graphics.draw(this.velocityShape);
	}

	protected void paintSelection(final Graphics2D graphics, final SelectionMode selectionMode,
	        final Rectangle2D itemShape, final boolean itemSelected, final boolean fill) {
		if (!selectionMode.isRenderable())
			return;
		if (itemShape != this.selectionShape && !selectionMode.matches(itemSelected, this.selectionShape, itemShape))
			return;

		if (fill) {
			graphics.setColor(selectionMode.getSelectionColor());
			graphics.fill(itemShape);
		}

		graphics.setColor(selectionMode.getSelectionBorderColor());
		graphics.setStroke(SELECTION_STROKE);
		graphics.draw(itemShape);
	}

	protected void paintSelectionHighlight(final Graphics2D graphics, final boolean modifyingVelocities,
	        final Shape shape) {
		graphics.setColor(modifyingVelocities ? ACCELERATE_COLOR : HIGHLIGHT_COLOR);
		graphics.setStroke(HIGHLIGHT_STROKE);
		graphics.draw(shape);
	}

	public void setContext(final RenderContext context) {
		this.context = context;
	}
}
