package io.github.jgacodili.molecules.client.action;

import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.Set;

import javax.swing.AbstractAction;

import molecules.client.InputAdapter;

public final class DeleteSelectionAction extends AbstractAction {
	@Serial
	private static final long serialVersionUID = 1L;

	private final InputAdapter inputAdapter;

	public DeleteSelectionAction(final InputAdapter uia) {
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
		final var deletees = Set.copyOf(selectedMolecules);

		this.inputAdapter.setModifyingVelocities(false);

		selectedMolecules.removeAll(deletees);
		this.inputAdapter.getEngineExecutor().execute(() -> molecules.keySet().removeAll(deletees));
	}
}