package it.polito.tdp.seriea.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo;

	public static List<Season> getAllSeasons() {
		SerieADAO dao = new SerieADAO();
		return dao.listSeasons();
	}

	public static List<Team> getAllTeams() {
		SerieADAO dao = new SerieADAO();
		return dao.listTeams();
	}

	public String getClassifica(Season s) {
		grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		String risultato="";
		SerieADAO dao = new SerieADAO();
		dao.popolaGrafo(grafo, s);
		List<Team> classifica = new LinkedList<Team>(grafo.vertexSet());
		Collections.sort(classifica);
		for(Team t: classifica) {
			risultato+=t.getTeam()+" "+t.getPunteggio()+"\n";
		}
		return risultato;
	}

}
