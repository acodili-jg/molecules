package molecules.level;

import java.util.concurrent.ConcurrentHashMap;

import molecules.molecule.Molecule;

public class Level {
	private final ConcurrentHashMap<Long, Molecule> molecules;

	public Level() {
		this.molecules = new ConcurrentHashMap<>();
	}

	public ConcurrentHashMap<Long, Molecule> getMolecules() {
		return this.molecules;
	}
}
