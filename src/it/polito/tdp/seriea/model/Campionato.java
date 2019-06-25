package it.polito.tdp.seriea.model;

public class Campionato {
	
	private Team teamCasa;
	private Team teamTrasferta;
	private int goalCasa;
	private int goalTrasferta;
	private String ris;
	public Campionato(Team teamCasa, Team teamTrasferta, String ris) {
		super();
		this.teamCasa = teamCasa;
		this.teamTrasferta = teamTrasferta;
		this.ris = ris;
	}
	public Team getTeamCasa() {
		return teamCasa;
	}
	public void setTeamCasa(Team teamCasa) {
		this.teamCasa = teamCasa;
	}
	public Team getTeamTrasferta() {
		return teamTrasferta;
	}
	public void setTeamTrasferta(Team teamTrasferta) {
		this.teamTrasferta = teamTrasferta;
	}
	public int getGoalCasa() {
		return goalCasa;
	}
	public void setGoalCasa(int goalCasa) {
		this.goalCasa = goalCasa;
	}
	public int getGoalTrasferta() {
		return goalTrasferta;
	}
	public void setGoalTrasferta(int goalTrasferta) {
		this.goalTrasferta = goalTrasferta;
	}
	public String getRis() {
		return ris;
	}
	public void setRis(String ris) {
		this.ris = ris;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((teamCasa == null) ? 0 : teamCasa.hashCode());
		result = prime * result + ((teamTrasferta == null) ? 0 : teamTrasferta.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Campionato other = (Campionato) obj;
		if (teamCasa == null) {
			if (other.teamCasa != null)
				return false;
		} else if (!teamCasa.equals(other.teamCasa))
			return false;
		if (teamTrasferta == null) {
			if (other.teamTrasferta != null)
				return false;
		} else if (!teamTrasferta.equals(other.teamTrasferta))
			return false;
		return true;
	}

}
