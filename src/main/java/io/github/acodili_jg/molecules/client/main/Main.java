package io.github.acodili_jg.molecules.client.main;

import io.github.acodili_jg.molecules.client.ClientLevel;
import io.github.acodili_jg.molecules.client.Molecules;
import io.github.acodili_jg.molecules.molecule.MoleculeObject;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.UUID;

public class Main {
    private static class MoleculeImpl extends MoleculeObject {
        public MoleculeImpl() {
            super();
        }

        @Override
        public void setUUID(final UUID uuid) {
            super.setUUID(uuid);
        }
    }

    public static void main(final String... args) throws InterruptedException {
        final var molecules = new Molecules();
        final var level = new ClientLevel();
        final var molecule = new MoleculeImpl();
        molecules.setLevel(level);

        molecule.setUUID(UUID.randomUUID());
        molecule.positionMut().set(0.0, 0.0);
        molecule.velocityMut().set(25.0, 25.0);
        molecules.addMolecule(molecule);

        molecule.setUUID(UUID.randomUUID());
        molecule.positionMut().set(0.0, 300.0);
        molecule.velocityMut().set(25.0, 0.0);
        molecules.addMolecule(molecule);

        molecule.setUUID(UUID.randomUUID());
        molecule.positionMut().set(300.0, 0.0);
        molecule.velocityMut().set(0.0, 25.0);
        molecules.addMolecule(molecule);

        molecules.show();

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
