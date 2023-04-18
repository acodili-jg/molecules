package molecules.client.action;

import java.awt.event.ActionEvent;
import java.io.Serial;

import javax.swing.AbstractAction;

import molecules.client.InputAdapter;

public final class ToggleSelectionAccelerationAction extends AbstractAction {
	@Serial
	private static final long serialVersionUID = 1L;

	private final InputAdapter inputAdapter;

	public ToggleSelectionAccelerationAction(final InputAdapter uia) {
		super("Toggle Selection Acceleration");

		this.inputAdapter = uia;
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		this.inputAdapter.setModifyingVelocities(!this.inputAdapter.isModifyingVelocities());
	}
}