package molecules.client.main;

import static java.awt.DisplayMode.REFRESH_RATE_UNKNOWN;
import static java.awt.Frame.NORMAL;

import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import molecules.client.Client;
import molecules.client.InputAdapter;
import molecules.client.renderer.GameRenderer;
import molecules.level.Level;

public class Main {
	private static int getSystemRefreshRate() {
		final var localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final var defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
		final var displayMode = defaultScreenDevice.getDisplayMode();
		final var refreshRate = displayMode.getRefreshRate();

		return refreshRate != REFRESH_RATE_UNKNOWN ? refreshRate : 60;
	}

	public static void main(final String... args) {
		final var client = new Client();
		final var engineExecutor = client.getEngineExecutor();
		final var frame = client.getFrame();
		final var inputAdapter = client.getInputAdapter();
		final var gameRenderer = client.getGameRenderer();

		final var level = new Level();
		client.setLevel(level);

		engineExecutor.start();

		// inputAdapter.addMoleculesInALine(new Point(0, 0), new Point(1_000, 1_000),
		// false);

		repaintRepeatedly(frame, inputAdapter, gameRenderer);
	}

	private static void repaintRepeatedly(final JFrame frame, final InputAdapter inputAdapter,
	        final GameRenderer gameRenderer) {
		var frames = 0;
		final var minDelayMillis = 500 / getSystemRefreshRate();
		var lastFrameMillis = System.currentTimeMillis();
		var lastReportMillis = System.currentTimeMillis();

		while (frame.isVisible()) {
			if (frame.getState() != NORMAL || inputAdapter.isRenderingPaused()) {
				frames = -1;
				continue;
			}

			final var currentMillis = System.currentTimeMillis();

			if (currentMillis - lastReportMillis >= 1_000L) {
				inputAdapter.setRecordedFPS(frames);

				frames = 0;
				lastReportMillis += 1_000L;
			}

			if (currentMillis - lastFrameMillis >= minDelayMillis) {
				gameRenderer.repaint();

				frames++;
				lastFrameMillis += minDelayMillis;
			}
		}
	}
}
