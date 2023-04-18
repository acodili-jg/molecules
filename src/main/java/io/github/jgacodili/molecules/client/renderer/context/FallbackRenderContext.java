package io.github.jgacodili.molecules.client.renderer.context;

import static io.github.jgacodili.molecules.engine.EngineExecutor.UNKNOWN;
import static io.github.jgacodili.molecules.util.SelectionMode.NONE;

import io.github.jgacodili.molecules.level.Level;
import io.github.jgacodili.molecules.util.SelectionMode;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Set;

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