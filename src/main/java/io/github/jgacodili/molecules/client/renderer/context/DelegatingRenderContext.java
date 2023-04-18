package molecules.client.renderer.context;

import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.Set;

import molecules.client.InputAdapter;
import molecules.engine.EngineExecutor;
import molecules.level.Level;
import molecules.util.SelectionMode;

public class DelegatingRenderContext implements RenderContext {
	private final EngineExecutor engineExecutor;

	private final InputAdapter inputAdapter;

	public DelegatingRenderContext(final EngineExecutor engineExecutor, final InputAdapter inputAdapter) {
		Objects.requireNonNull(engineExecutor, "Parameter engineExecutor is null");
		Objects.requireNonNull(inputAdapter, "Parameter inputAdapter is null");

		this.engineExecutor = engineExecutor;
		this.inputAdapter = inputAdapter;
	}

	@Override
	public Level getLevel() {
		return this.inputAdapter.getLevel();
	}

	@Override
	public Point2D.Double getMousePosition() {
		return this.inputAdapter.getMousePosition();
	}

	@Override
	public Point2D.Double getMousePressedPosition() {
		return this.inputAdapter.getMousePressedPosition();
	}

	@Override
	public int getRecordedFPS() {
		return this.inputAdapter.getRecordedFPS();
	}

	@Override
	public Set<Long> getSelectedMolecules() {
		return this.inputAdapter.getSelectedMolecules();
	}

	@Override
	public SelectionMode getSelectionMode() {
		return this.inputAdapter.getSelectionMode();
	}

	@Override
	public int getTaskCount() {
		return this.engineExecutor.getCommandCount();
	}

	@Override
	public long getUpdateMillis() {
		return this.engineExecutor.getDeltaMillis();
	}

	@Override
	public boolean isModifyingVelocities() {
		return this.inputAdapter.isModifyingVelocities();
	}

	@Override
	public boolean isTranslating() {
		return this.inputAdapter.isTranslating();
	}
}