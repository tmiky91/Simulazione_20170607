package it.polito.tdp.seriea.model;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo;
	private int punti=0;

	public List<Season> getAllSeasons() {
		SerieADAO dao = new SerieADAO();
		return dao.listSeasons();
	}

	public String calcolaPartite(Season s) {
		SerieADAO dao = new SerieADAO();
		grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		List<Match> calendario = dao.getRisultatiPartite(s);
		String risultato="";
		dao.popolaGrafo(grafo, s);
//		for(Team t1: grafo.vertexSet()) {
//			for(Team t2: grafo.vertexSet()) {
//				for(DefaultWeightedEdge edge: grafo.edgeSet()) {
//					if(grafo.getEdgeWeight(edge)==1) {
//						punti=t1.getPunti()+3;
//					}
//					else if(grafo.getEdgeWeight(edge)==0) {
//						punti=t1.getPunti()+1;
//						punti=t2.getPunti()+1;
//					}
//					else {
//						punti=t2.getPunti()+3;
//					}
//				}
//			}
//		}
		for(Match m: calendario) {
			if(m.getFtr().compareTo("H")==0) {
				punti=m.getHomeTeam().getPunti()+3;
			}
			else if(m.getFtr().compareTo("D")==0) {
				punti=m.getHomeTeam().getPunti()+1;
				punti=m.getAwayTeam().getPunti()+1;
			}
			else {
				punti=m.getAwayTeam().getPunti()+3;
			}
		}
		//Team t1 = new Team();
		List<Team> classifica = new LinkedList<Team>();
		
		return risultato;
	}

}
