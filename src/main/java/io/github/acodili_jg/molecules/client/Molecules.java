package io.github.acodili_jg.molecules.client;

import io.github.acodili_jg.molecules.level.CollisionConsumer;
import io.github.acodili_jg.molecules.level.Level;
import io.github.acodili_jg.molecules.molecule.MoleculeRef;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.util.UUID;
import javax.swing.JFrame;
import org.joml.Vector2dc;

public class Molecules {
    private static final Dimension DEFUALT_WINDOW_SIZE = new Dimension(800, 600);

    private static Dimension getMaximumWindowSize() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getMaximumWindowBounds()
            .getSize();
    }

    private final Canvas canvas;
    private final JFrame frame;
    private final Object2ReferenceOpenHashMap<UUID, MoleculeImpl> molecules;
    private final Renderer renderer;
    private Level level;
    private double time;

    private final CollisionConsumer collisionConsumer;

    public Molecules() {
        this.canvas = new Canvas();
        this.frame = new JFrame();
        this.molecules = new Object2ReferenceOpenHashMap<>();
        this.renderer = new Renderer();
        final var contentPane = this.frame.getContentPane();

        this.canvas.setSize(getMaximumWindowSize());

        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setSize(DEFUALT_WINDOW_SIZE);
        this.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.frame.setLocationRelativeTo(null);

        contentPane.add(this.canvas);

        this.collisionConsumer = this::syncOnCollision;
    }

    private void syncOnCollision(final double time, final UUID uuid, final Vector2dc velocity) {
        final var molecule = this.molecules.get(uuid);

        // positions aren't actually updated every frame, only on syncs
        molecule.positionMut().fma(time, molecule.velocity());
        molecule.setVelocity(velocity);

        // Accumulated time never resets, reverse instead
        molecule.positionMut().fma(-time, velocity);
    }

    public boolean isDisplayable() {
        return this.frame.isDisplayable();
    }

    public void show() {
        this.frame.setVisible(true);
        this.canvas.createBufferStrategy(3);
    }

    public Level level() {
        return this.level;
    }

    public void setLevel(final Level level) {
        final var oldLevel = this.level;
        this.level = level;

        if (oldLevel != null) {
            this.molecules.clear();
        }
        if (level != null) {
            for (final var molecule : this.molecules.values()) {
                level.addMovedMolecule(molecule);
            }
            this.molecules.clear();
            for (final var molecule : level.moleculeSet()) {
                this.addMolecule(molecule);
            }
        }
    }

    public void addMolecule(final MoleculeRef molecule) {
        this.molecules.put(molecule.uuid(), new MoleculeImpl(molecule));
        this.level.addClonedMolecule(molecule);
    }

    public void tick(final double dt) {
        final var time = this.time += dt;
        this.tickPhysics(time);
        this.render();
    }

    public void tickPhysics(final double time) {
        final var level = this.level;
        if (level == null) {
            return;
        }
        while (level.hasNextCollisionBefore(time)) {
            level.consumeNextSimultaneousCollisions(this.collisionConsumer);
        }
    }

    public void render() {
        if (this.level == null) {
            return;
        }
        this.renderer.render(
            this.canvas.getBufferStrategy(),
            this.frame.getWidth(),
            this.frame.getHeight(),
            this.time,
            this.molecules.values()
        );
    }
}
