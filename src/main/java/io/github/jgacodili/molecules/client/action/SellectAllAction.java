package io.github.jgacodili.molecules.client.action;

import io.github.jgacodili.molecules.client.InputAdapter;

import java.awt.event.ActionEvent;
import java.io.Serial;

import javax.swing.AbstractAction;

public final class SellectAllAction extends AbstractAction {
	@Serial
	private static final long serialVersionUID = 1L;

	private final InputAdapter inputAdapter;

	public SellectAllAction(final InputAdapter uia) {
		super("Sellect All");

		this.inputAdapter = uia;
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		final var level = this.inputAdapter.getLevel();

		if (level == null)
			return;

		final var selectedMolecules = this.inputAdapter.getSelectedMolecules();
		final var molecules = level.getMolecules();

		if (selectedMolecules.size() == molecules.size())
			selectedMolecules.clear();
		else
			selectedMolecules.addAll(molecules.keySet());
	}
}