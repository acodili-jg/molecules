package molecules.client.renderer.context;

import static molecules.engine.EngineExecutor.UNKNOWN;
import static molecules.util.SelectionMode.NONE;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Set;

import molecules.level.Level;
import molecules.util.SelectionMode;

public class FallbackRenderContext implements RenderContext {
	public static final FallbackRenderContext FALLBACK_CONTEXT = new FallbackRenderContext();

	public FallbackRenderContext() {
	}

	@Override
	public Level getLevel() {
		return null;
	}

	@Override
	public Point2D.Double getMousePosition() {
		return new Point2D.Double();
	}

	@Override
	public Point2D.Double getMousePressedPosition() {
		return new Point2D.Double();
	}

	@Override
	public int getRecordedFPS() {
		return -1;
	}

	@Override
	public Set<Long> getSelectedMolecules() {
		return Collections.emptySet();
	}

	@Override
	public SelectionMode getSelectionMode() {
		return NONE;
	}

	@Override
	public int getTaskCount() {
		return 0;
	}

	@Override
	public long getUpdateMillis() {
		return UNKNOWN;
	}

	@Override
	public boolean isModifyingVelocities() {
		return false;
	}

	@Override
	public boolean isTranslating() {
		return false;
	}
}