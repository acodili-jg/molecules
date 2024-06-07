package io.github.acodili_jg.molecules.client.main;

import io.github.acodili_jg.molecules.client.ClientLevel;
import io.github.acodili_jg.molecules.client.Molecules;
import io.github.acodili_jg.molecules.molecule.MoleculeObject;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.UUID;

public class Main {
    public static void main(final String... args) throws InterruptedException {
        class MoleculeImpl extends MoleculeObject {
            public MoleculeImpl() {
                super();
            }
    
            @Override
            public void setUUID(final UUID uuid) {
                super.setUUID(uuid);
            }
        }
    
        final var molecules = new Molecules();
        final var level = new ClientLevel();
        final var molecule = new MoleculeImpl();
        molecules.setLevel(level);

        for (var u = 1; u < 50; u++) {
            for (var v = 1; v < 25; v++) {
                molecule.setUUID(UUID.randomUUID());
                molecule.setRadius(10);
                molecule.positionMut().set(15.0 + u * (30.0), 15.0 + v * (30.0));
                molecule.velocityMut().set(10 * Math.random() - 5, 10 * Math.random() - 5);
                molecules.addMolecule(molecule);
            }
        }

        molecules.show();
        Thread.sleep(1_000);

        var lastMs = System.currentTimeMillis();
        final var refreshWaitMs = 1_000 / getDefaultRefreshRate();
        final var dt = refreshWaitMs / 1_000.0;

        // final var frameCounts = new java.util.HashMap<Long, Integer>();

        while (molecules.isDisplayable()) {
            final var deltaMs = (System.currentTimeMillis() - lastMs);
            final var frames = deltaMs / refreshWaitMs;
            // frameCounts.compute(frames, (k, v) -> v == null ? 1 : v + 1);
            if (frames <= 0) {
                Thread.sleep(deltaMs % refreshWaitMs);
                continue;
            }

            molecules.tick(frames * dt);
            lastMs += frames * refreshWaitMs;
            Thread.yield();
        }

        // System.out.println(frameCounts);
    }

    private static int getDefaultRefreshRate() {
        final var refreshRate = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDisplayMode()
            .getRefreshRate();
        if (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN) {
            return 15;
        } else {
            return refreshRate;
        }
    }
}
