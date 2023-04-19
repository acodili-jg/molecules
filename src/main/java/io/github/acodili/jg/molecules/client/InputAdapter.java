package io.github.acodili.jg.molecules.client;

import static io.github.acodili.jg.molecules.util.SelectionMode.ADDITIVE;
import static io.github.acodili.jg.molecules.util.SelectionMode.NONE;
import static io.github.acodili.jg.molecules.util.SelectionMode.SUBTRACTIVE;
import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_M;

import io.github.acodili.jg.molecules.client.action.DeleteSelectionAction;
import io.github.acodili.jg.molecules.client.action.StopSelectionAction;
import io.github.acodili.jg.molecules.client.action.ToggleSelectionAccelerationAction;
import io.github.acodili.jg.molecules.client.renderer.GameRenderer;
import io.github.acodili.jg.molecules.engine.EngineExecutor;
import io.github.acodili.jg.molecules.level.Level;
import io.github.acodili.jg.molecules.math.Vec2d;
import io.github.acodili.jg.molecules.molecule.Molecule;
import io.github.acodili.jg.molecules.util.SelectionMode;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.swing.KeyStroke;

public class InputAdapter implements MouseListener, MouseMotionListener {
	private EngineExecutor engineExecutor;

	private Level level;

	private transient boolean modifyingVelocities;

	private final Point2D.Double mousePosition;

	private final Point2D.Double mousePressedPosition;

	private int recordedFPS;

	private volatile boolean renderingPaused;

	private final HashSet<Long> selectedMolecules;

	private SelectionMode selectionMode;

	private volatile boolean translating;

	public InputAdapter() {
		this.mousePosition = new Point2D.Double(Double.NaN, Double.NaN);
		this.mousePressedPosition = new Point2D.Double();
		this.recordedFPS = -1;
		this.selectedMolecules = new HashSet<>();
		this.selectionMode = NONE;
	}

	public void applyForceToSelection(final Vec2d deltaVector) {
		final var level = getLevel();

		if (level == null)
			return;

		final var molecules = level.getMolecules();
		final var selectedMolecules = getSelectedMolecules();

		this.engineExecutor.execute(() -> {
			for (final var selectedMolecule : selectedMolecules) {
				final var molecule = molecules.get(selectedMolecule);

				molecule.velocity.add(deltaVector);
			}
		});
	}

	public EngineExecutor getEngineExecutor() {
		return this.engineExecutor;
	}

	public Level getLevel() {
		return this.level;
	}

	public Point2D.Double getMousePosition() {
		return this.mousePosition;
	}

	public Point2D.Double getMousePressedPosition() {
		return this.mousePressedPosition;
	}

	public int getRecordedFPS() {
		return this.recordedFPS;
	}

	public HashSet<Long> getSelectedMolecules() {
		return this.selectedMolecules;
	}

	public SelectionMode getSelectionMode() {
		return this.selectionMode;
	}

	public void init(final EngineExecutor engineExecutor, final GameRenderer gameRenderer) {
		if (this.engineExecutor != null)
			throw new IllegalStateException("Already initialized");

		Objects.requireNonNull(gameRenderer, "Parameter levelRenderer is null");
		Objects.requireNonNull(engineExecutor, "Parameter engineExecutor is null");

		this.engineExecutor = engineExecutor;

		gameRenderer.addMouseListener(this);
		gameRenderer.addMouseMotionListener(this);

		final var inputMap = gameRenderer.getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(VK_DELETE, 0), "molecules.action.delete-selection");
		inputMap.put(KeyStroke.getKeyStroke(VK_M, 0), "molecules.action.toggle-selection-acceleration");
		inputMap.put(KeyStroke.getKeyStroke(VK_M, ALT_DOWN_MASK), "molecules.action.stop-selection");

		final var actionMap = gameRenderer.getActionMap();
		actionMap.put("molecules.action.delete-selection", new DeleteSelectionAction(this));
		actionMap.put("molecules.action.stop-selection", new StopSelectionAction(this));
		actionMap.put("molecules.action.toggle-selection-acceleration", new ToggleSelectionAccelerationAction(this));
	}

	public boolean isModifyingVelocities() {
		return this.modifyingVelocities;
	}

	public boolean isRenderingPaused() {
		return this.renderingPaused;
	}

	public boolean isTranslating() {
		return this.translating;
	}

	@Override
	public void mouseClicked(final MouseEvent event) {
	}

	@Override
	public void mouseDragged(final MouseEvent event) {
		final var eventPoint = event.getPoint();
		final var lastMousePosition = (Point2D.Double) this.mousePosition.clone();
		final var deltaVector = Vec2d.of(eventPoint).subtract(lastMousePosition);
		this.mousePosition.setLocation(eventPoint);

		tryApplyingForceToSelection(deltaVector);

		switch (event.getModifiersEx()) {
		case BUTTON1_DOWN_MASK | ALT_DOWN_MASK | CTRL_DOWN_MASK ->
		    addMoleculesInALine(lastMousePosition, this.mousePosition, false);
		}
	}

	@Override
	public void mouseEntered(final MouseEvent event) {
	}

	@Override
	public void mouseExited(final MouseEvent event) {
	}

	@Override
	public void mouseMoved(final MouseEvent event) {
		final var eventPoint = event.getPoint();
		final var deltaVector = Vec2d.of(eventPoint).subtract(this.mousePosition);
		this.mousePosition.setLocation(eventPoint);

		tryApplyingForceToSelection(deltaVector);
	}

	@Override
	public void mousePressed(final MouseEvent event) {
		this.mousePressedPosition.setLocation(event.getPoint());
		this.mousePosition.setLocation(event.getPoint());

		setModifyingVelocities(false);

		switch (event.getModifiersEx()) {
		case BUTTON1_DOWN_MASK -> {
			if (!this.selectedMolecules.isEmpty())
				this.selectedMolecules.clear();
		}
		case BUTTON1_DOWN_MASK | SHIFT_DOWN_MASK -> this.setSelectionMode(ADDITIVE);
		case BUTTON1_DOWN_MASK | CTRL_DOWN_MASK -> addMoleculesInALine(this.mousePosition, this.mousePosition, true);
		case BUTTON1_DOWN_MASK | CTRL_DOWN_MASK | SHIFT_DOWN_MASK -> this.setSelectionMode(SUBTRACTIVE);
		case BUTTON1_DOWN_MASK | ALT_DOWN_MASK | CTRL_DOWN_MASK ->
		    addMoleculesInALine(this.mousePosition, this.mousePosition, false);
		case BUTTON3_DOWN_MASK -> setTranslating(true);
		}
	}

	@Override
	public void mouseReleased(final MouseEvent event) {
		this.mousePosition.setLocation(event.getPoint());

		if (getSelectionMode() != NONE) {
			final var selection = new Rectangle2D.Double();
			selection.setFrameFromDiagonal(this.mousePressedPosition.x, this.mousePressedPosition.y,
			        this.mousePosition.x, this.mousePosition.y);

			if (Math.max(selection.width, selection.height) <= 0.0d) {
				selection.x -= 1.25;
				selection.y -= 1.25;
				selection.width += 2.5;
				selection.height += 2.5;
			}

			for (final var keyedMolecule : this.level.getMolecules().entrySet()) {
				final var key = keyedMolecule.getKey();
				final var molecule = keyedMolecule.getValue();

				final var position = molecule.position;
				final var radius = molecule.radius;
				final var diameter = radius * 2.0d;

				if (selection.intersects(position.x - radius, position.y - radius, diameter, diameter))
					if (getSelectionMode() == ADDITIVE)
						this.selectedMolecules.add(key);
					else if (getSelectionMode() == SUBTRACTIVE)
						this.selectedMolecules.remove(key);
			}

			setSelectionMode(NONE);
		}

		if (isTranslating()) {
			final var level = getLevel();

			if (level == null)
				return;

			final var molecules = level.getMolecules();
			final var deltaPosition = Vec2d.of(this.mousePosition).subtract(this.mousePressedPosition);

			this.engineExecutor.execute(() -> {
				setRenderingPaused(true);
				setTranslating(false);

				for (final var molecule : molecules.values())
					molecule.position.add(deltaPosition);

				setRenderingPaused(false);
			});
		}
	}

	public void addMoleculesInALine(final Point2D from, final Point2D to, final boolean modifyVelocities) {
		final var level = this.level;

		if (level == null)
			return;

		final var molecules = level.getMolecules();
		final var selectedMolecules = getSelectedMolecules();
		final var cursorPosition = Vec2d.of(from);

		final int steps;
		final Vec2d normal;

		if (!from.equals(to)) {
			normal = Vec2d.of(to).subtract(from);
			final var distance = normal.magnitude();
			steps = (int) (distance / Molecule.DEFAULT_RADIUS);
			normal.multiply(2.0d * Molecule.DEFAULT_RADIUS).divide(distance);
		} else {
			steps = 0;
			normal = Vec2d.origin();
		}

		this.engineExecutor.execute(() -> {
			final var addees = new HashMap<Long, Molecule>();

			selectedMolecules.clear();

			IntStream.rangeClosed(0, steps)
			         .parallel()
			         .mapToObj(i -> Vec2d.fma(normal, i, cursorPosition, null))
			         .filter(position -> molecules.values().parallelStream().allMatch(molecule -> {
				         final var dx = molecule.position.x - position.x;
				         final var dy = molecule.position.y - position.y;
				         final var sumRadii = molecule.radius + 10.0d;

				         return dx * dx + dy * dy >= sumRadii * sumRadii;
			         }))
			         .map(position -> {
				         final var id = System.nanoTime();
				         final var molecule = new Molecule();
				         molecule.paint = Color.getHSBColor((float) Math.random(), 1.0f,
				                 0.75f * (float) Math.random() + 0.25f);
				         molecule.position.set(position);

				         return Map.entry(id, molecule);
			         })
			         .forEachOrdered(entry -> addees.put(entry.getKey(), entry.getValue()));

			molecules.putAll(addees);
			selectedMolecules.addAll(addees.keySet());
			setModifyingVelocities(modifyVelocities);
		});
	}

	public void setLevel(final Level level) {
		this.level = level;
	}

	public void setModifyingVelocities(final boolean modifyingVelocities) {
		this.modifyingVelocities = modifyingVelocities;
	}

	public void setRecordedFPS(final int recordedFPS) {
		this.recordedFPS = recordedFPS;
	}

	public void setRenderingPaused(final boolean renderingPaused) {
		this.renderingPaused = renderingPaused;
	}

	public void setSelectionMode(final SelectionMode selectionMode) {
		Objects.requireNonNull(selectionMode, "Parameter selectionMode is null");

		this.selectionMode = selectionMode;
	}

	public void setTranslating(final boolean translating) {
		this.translating = translating;
	}

	protected void tryApplyingForceToSelection(final Vec2d deltaVector) {
		if (!isModifyingVelocities())
			return;

		applyForceToSelection(deltaVector);
	}
}
