package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {

	private List<Team> teams; // tutte le squadre, di ogni anno
	private List<Season> seasons;
	private Map<String, Team> teamsMap;

	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> graph;

	public Model() {

		SerieADAO dao = new SerieADAO();
		this.teamsMap = new HashMap<>();

		this.teams = dao.listTeams(teamsMap);
		this.seasons = dao.listSeasons();

	}

	public List<Team> getTeams() {
		return teams;
	}

	public List<Season> getSeasons() {
		return seasons;
	}

	public void caricaPartite(Season season) {

		// Crea il grafo
		this.graph = new SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		SerieADAO dao = new SerieADAO();

		List<Match> matches = dao.listMatches(season, teamsMap);

		for (Match m : matches) {
			graph.addVertex(m.getHomeTeam());
			graph.addVertex(m.getAwayTeam());

			int peso;
			switch (m.getFtr()) {
			case "H":
				peso = +1;
				break;
			case "A":
				peso = -1;
				break;
			case "D":
				peso = 0;
				break;
			default:
				throw new IllegalArgumentException("Errore interno: risultato non valido = " + m.getFtr());

			}

			Graphs.addEdgeWithVertices(graph, m.getHomeTeam(), m.getAwayTeam(), peso);
		}

	}

	public Set<Team> getCurrentTeams() {
		return this.graph.vertexSet();
	}

	/**
	 * Calcola e restituisce la classifica delle squadre, per la stagione
	 * corrispondente ai dati memorizzati nel grafo
	 * 
	 * @return la lista dei {@link Team} partecipanti alla stagione, con il
	 *         valore corretto dell'attributo {@code punti}. La lista è ordinata
	 *         per valori descrescenti di punteggio.
	 */
	public List<Team> getClassifica() {
		// azzero i punteggi
		for (Team t : graph.vertexSet())
			t.setPunti(0);

		// considero ogni partita
		for (DefaultWeightedEdge e : graph.edgeSet()) {
			Team home = graph.getEdgeSource(e);
			Team away = graph.getEdgeTarget(e);
			switch ((int) graph.getEdgeWeight(e)) {
			case +1:
				home.setPunti(home.getPunti() + 3);
				break;
			case -1:
				away.setPunti(away.getPunti() + 3);
				break;
			case 0:
				home.setPunti(home.getPunti() + 1);
				away.setPunti(away.getPunti() + 1);
				break;
			}
		}

		List<Team> classifica = new ArrayList<Team>(graph.vertexSet());
		Collections.sort(classifica, new Comparator<Team>() {

			@Override
			public int compare(Team o1, Team o2) {
				return -(o1.getPunti() - o2.getPunti());
			}
		});

		return classifica;
	}

}
