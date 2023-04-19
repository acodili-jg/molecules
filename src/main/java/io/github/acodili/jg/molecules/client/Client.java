package io.github.acodili.jg.molecules.client;

import static java.awt.Font.PLAIN;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import io.github.acodili.jg.molecules.client.renderer.GameRenderer;
import io.github.acodili.jg.molecules.client.renderer.context.DelegatingRenderContext;
import io.github.acodili.jg.molecules.client.renderer.context.RenderContext;
import io.github.acodili.jg.molecules.engine.Engine;
import io.github.acodili.jg.molecules.engine.EngineExecutor;
import io.github.acodili.jg.molecules.level.Level;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.JFrame;

public class Client {
	public static final Font DEBUG_FONT = new Font("Consolas", PLAIN, 15);

	public static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(900, 600);

	private static final LayoutManager NO_LAYOUT = null;

	private final Engine engine;

	private final EngineExecutor engineExecutor;

	private final JFrame frame;

	private final InputAdapter inputAdapter;

	private Level level;

	private final GameRenderer gameRenderer;

	private final RenderContext renderContext;

	public Client() {
		this.engine = new Engine();
		this.engineExecutor = new EngineExecutor(this.engine);

		this.gameRenderer = new GameRenderer();
		this.gameRenderer.setFont(DEBUG_FONT);

		this.inputAdapter = new InputAdapter();
		this.inputAdapter.init(this.engineExecutor, this.gameRenderer);

		this.renderContext = new DelegatingRenderContext(this.engineExecutor, this.inputAdapter);
		this.gameRenderer.setContext(this.renderContext);

		this.frame = new JFrame();
		this.frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.frame.setSize(DEFAULT_WINDOW_SIZE);
		this.frame.setLocationRelativeTo(null);
		this.frame.setTitle("Molecules");

		this.frame.setContentPane(this.gameRenderer);
		this.frame.setLayout(NO_LAYOUT);

		this.frame.setVisible(true);

	}

	public Engine getEngine() {
		return this.engine;
	}

	public EngineExecutor getEngineExecutor() {
		return this.engineExecutor;
	}

	public JFrame getFrame() {
		return this.frame;
	}

	public GameRenderer getGameRenderer() {
		return this.gameRenderer;
	}

	public InputAdapter getInputAdapter() {
		return this.inputAdapter;
	}

	public Level getLevel() {
		return this.level;
	}

	public void setLevel(final Level level) {
		this.level = level;
		getEngine().setLevel(level);
		getInputAdapter().setLevel(level);
	}
}
