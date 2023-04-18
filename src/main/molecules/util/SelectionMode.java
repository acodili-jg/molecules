package molecules.util;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public enum SelectionMode {
	NONE(false, new Color(79, 79, 79)) {
		@Override
		public boolean matches(final boolean selected, final Rectangle2D selection, final Rectangle2D item) {
			return false;
		}
	},
	ADDITIVE(true, new Color(79, 79, 255)) {
		@Override
		public boolean matches(final boolean selected, final Rectangle2D selection, final Rectangle2D item) {
			return !selected && selection.intersects(item);
		}
	},
	SUBTRACTIVE(true, new Color(255, 79, 79)) {
		@Override
		public boolean matches(final boolean selected, final Rectangle2D selection, final Rectangle2D item) {
			return selected && selection.intersects(item);
		}
	};

	private final boolean renderable;

	private final Color selectionBorderColor;

	private final Color selectionColor;

	private SelectionMode(final boolean renderable, final Color baseColor) {
		this.renderable = renderable;
		this.selectionBorderColor = new Color(baseColor.getRGB() & 0xBFFFFFFF, true);
		this.selectionColor = new Color(baseColor.getRGB() & 0x27FFFFFF, true);
	}

	public Color getSelectionBorderColor() {
		return this.selectionBorderColor;
	}

	public Color getSelectionColor() {
		return this.selectionColor;
	}

	public boolean isRenderable() {
		return this.renderable;
	}

	public abstract boolean matches(boolean selected, Rectangle2D selection, Rectangle2D item);
}
