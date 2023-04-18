package molecules.client.renderer.context;

import java.awt.geom.Point2D;
import java.util.Set;

import molecules.level.Level;
import molecules.util.SelectionMode;

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