package it.polito.tdp.seriea.model;

import java.util.Comparator;

public class ComparatorePunti implements Comparator<Team>{

	@Override
	public int compare(Team t1, Team t2) {
		return t2.getPunti()-t1.getPunti();
	}

}
