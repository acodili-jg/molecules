package io.github.jgacodili.molecules.client.action;

import io.github.jgacodili.molecules.client.InputAdapter;

import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.Set;

import javax.swing.AbstractAction;

public final class StopSelectionAction extends AbstractAction {
	@Serial
	private static final long serialVersionUID = 1L;

	private final InputAdapter inputAdapter;

	public StopSelectionAction(final InputAdapter uia) {
		super("Delete Selection");

		this.inputAdapter = uia;
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		final var level = this.inputAdapter.getLevel();

		if (level == null)
			return;

		final var molecules = level.getMolecules();
		final var selectedMolecules = this.inputAdapter.getSelectedMolecules();
		final var stopees = Set.copyOf(selectedMolecules);

		this.inputAdapter.getEngineExecutor().execute(() -> {
			for (final var stopee : stopees) {
				final var molecule = molecules.get(stopee);

				molecule.getVelocity().set(0.0d, 0.0d);
			}
		});
	}
}