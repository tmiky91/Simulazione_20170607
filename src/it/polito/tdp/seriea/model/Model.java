package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private Map<String, Team> teamsMap; // Identity Map per i Team

	// grafo delle partite (relativo alla stagione selezionata)
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> graph;
	
	// Variabili utilizzate dalla ricorsione
	private List<Team> bestDomino ;
	private Set<DefaultWeightedEdge> usedEdges ;
	

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
	
	public List<Team> calcolaDomino() {
		this.bestDomino = new ArrayList<>() ;
		this.usedEdges = new HashSet<>() ;
		
		List<Team> path = new ArrayList<>() ;
		
		/***ATTENZIONE***/
		/**
		 * Elimina dei vertici dal grafo per renderlo
		 * gestibile dalla ricorsione.
		 * Nella soluzione "vera" questa istruzione va rimossa
		 * (però l'algoritmo non termina in tempi umani).
		 */
		this.riduciGrafo(8);
		
		for(Team initial : graph.vertexSet()) {
			path.add(initial) ;
			dominoRecursive(1, initial, path) ;
			path.remove(initial) ;
		}
		
		return this.bestDomino ;
	}

	private void dominoRecursive(int step, Team t1, List<Team> path) {
				
		// controlla se ho migliorato il cammino "best"
		if(path.size() > this.bestDomino.size()) {
			// aggiorna il "best"
			this.bestDomino.clear();
			this.bestDomino.addAll(path) ;
			// oppure this.bestDomino = new ArrayList<>(path)
			// ma NON this.bestDomino = path

			//System.out.format("%2d %s\n", path.size(), path.toString());
		}
		
		// cerca di aggiungere un nuovo vertice
		for(DefaultWeightedEdge e: this.graph.outgoingEdgesOf(t1)) {
			Team t2 = graph.getEdgeTarget(e) ;
			
			// verifico che l'arco sia relativo ad una partita vinta
			// e che non sia ancora stato utilizzato
			if(graph.getEdgeWeight(e) == +1 && !this.usedEdges.contains(e)) {
				// provo ad attraversare l'arco
				path.add(t2) ;
				usedEdges.add(e) ;
				dominoRecursive(step+1, t2, path);
				usedEdges.remove(e) ;
				path.remove(path.size()-1) ; // togli l'ultimo aggiunto
				// Attenzione: path.remove(t2) ; non funziona perché t2 può comparire più di una volta
			}
		}
	}
	
	/**
	 * cancella dei vertici dal grafo in modo che la sua dimensione
	 * sia solamente pari a {@code dim} vertici
	 * @param dim
	 */
	private void riduciGrafo(int dim) {
		Set<Team> togliere = new HashSet<>() ;
		
		Iterator<Team> iter = graph.vertexSet().iterator() ;
		for(int i=0; i<graph.vertexSet().size()-dim; i++) {
			togliere.add(iter.next()) ;
		}
		graph.removeAllVertices(togliere) ;
		System.err.println("Attenzione: cancello dei vertici dal grafo");
		System.err.println("Vertici rimasti: "+graph.vertexSet().size()+"\n");
	}
}
