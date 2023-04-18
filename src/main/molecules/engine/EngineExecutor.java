package molecules.engine;

import static java.lang.Thread.MAX_PRIORITY;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class EngineExecutor implements Runnable, Executor {
	public static final long UNKNOWN = -1;

	private static final long MILLIS_PER_SECOND = 1_000L;

	private volatile long deltaMillis;

	private final Queue<Runnable> commands;

	private final Engine engine;

	private volatile boolean running;

	private final Thread thread;

	public EngineExecutor(final Engine engine) {
		Objects.requireNonNull(engine, "Parameter engine is null");

		this.commands = new ConcurrentLinkedQueue<>();
		this.deltaMillis = UNKNOWN;
		this.engine = engine;
		this.thread = new Thread(this);

		this.thread.setDaemon(true);
		this.thread.setPriority(MAX_PRIORITY);
	}

	@Override
	public void execute(final Runnable command) {
		this.commands.offer(command);
	}

	public int getCommandCount() {
		return this.commands.size();
	}

	public long getDeltaMillis() {
		return this.deltaMillis;
	}

	@Override
	public void run() {
		var lastMillis = System.currentTimeMillis();

		while (true) {
			final var command = this.commands.poll();
			if (command != null)
				command.run();

			final var updateMillis = Math.round(MILLIS_PER_SECOND * this.engine.getUpdateInterval());
			final var currentMillis = System.currentTimeMillis();
			final var deltaMillis = currentMillis - lastMillis;

			if (deltaMillis < updateMillis)
				continue;

			this.deltaMillis = deltaMillis;
			this.engine.update();
			lastMillis = currentMillis;
		}
	}

	public void start() {
		if (this.running)
			throw new IllegalArgumentException("Already running");

		this.thread.start();

		this.running = true;
	}

	public void stop() {
		if (!this.running)
			throw new IllegalArgumentException("Not running");

		this.running = false;
	}
}
