package it.polito.tdp.seriea.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo;
	private Map<String, Team> idMap;
	
	public Model() {
		idMap = new HashMap<>();
	}

	public List<Season> getAllSeasons() {
		SerieADAO dao = new SerieADAO();
		return dao.listSeasons();
	}

	public String calcolaClassifica(Season s) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		String risultato="";
		SerieADAO dao = new SerieADAO();
		dao.listTeams(idMap);
		List<Campionato> campionato = dao.getCampionato(idMap, s);
		int puntiC=0;
		int puntiT=0;
		for(Campionato c: campionato) {
			double peso=0;
			if(c.getRis().compareTo("H")==0) {
				peso=1;
				puntiC=3;
				puntiT=0;
			}
			else if(c.getRis().compareTo("A")==0) {
				peso=-1;
				puntiT=3;
				puntiC=0;
			}
			else if(c.getRis().compareTo("D")==0) {
				peso=0;
				puntiC=1;
				puntiT=1;
			}
			if(!grafo.containsVertex(c.getTeamCasa())) {
				grafo.addVertex(c.getTeamCasa());
			}
			if(!grafo.containsVertex(c.getTeamTrasferta())) {
				grafo.addVertex(c.getTeamTrasferta());
			}
			DefaultWeightedEdge edge = grafo.getEdge(c.getTeamCasa(), c.getTeamTrasferta());
			if(edge==null) {
				Graphs.addEdgeWithVertices(grafo, c.getTeamCasa(), c.getTeamTrasferta(), peso);
				idMap.get(c.getTeamCasa().getTeam()).setPunteggio(puntiC);
				idMap.get(c.getTeamTrasferta().getTeam()).setPunteggio(puntiT);
			}
			else {
				grafo.getEdgeSource(edge).setPunteggio(puntiC);
				grafo.getEdgeTarget(edge).setPunteggio(puntiT);
			}
		}
		System.out.println("Grafo Creato! Vertici: "+grafo.vertexSet().size()+" Archi: "+grafo.edgeSet().size());
		for(DefaultWeightedEdge edge: grafo.edgeSet()) {
			System.out.println(grafo.getEdgeSource(edge).getTeam()+" - "+grafo.getEdgeTarget(edge).getTeam()+" "+grafo.getEdgeWeight(edge)+"\n");
		}
		List<Team> classifica = new LinkedList<>(grafo.vertexSet());
		Collections.sort(classifica);
		for(Team t: classifica) {
			risultato+=t.getTeam()+" "+t.getPunteggio()+"\n";
		}
		return risultato;
	}

}
