package io.github.acodili.jg.molecules.client.renderer.context;

import io.github.acodili.jg.molecules.level.Level;
import io.github.acodili.jg.molecules.util.SelectionMode;

import java.awt.geom.Point2D;
import java.util.Set;

public interface RenderContext {
	Level getLevel();

	Point2D.Double getMousePosition();

	Point2D.Double getMousePressedPosition();

	int getRecordedFPS();

	Set<Long> getSelectedMolecules();

	SelectionMode getSelectionMode();

	int getTaskCount();

	long getUpdateMillis();

	boolean isModifyingVelocities();

	boolean isTranslating();
}