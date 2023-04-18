package io.github.jgacodili.molecules.level;

import io.github.jgacodili.molecules.molecule.Molecule;

import java.util.concurrent.ConcurrentHashMap;

public class Level {
	private final ConcurrentHashMap<Long, Molecule> molecules;

	public Level() {
		this.molecules = new ConcurrentHashMap<>();
	}

	public ConcurrentHashMap<Long, Molecule> getMolecules() {
		return this.molecules;
	}
}
