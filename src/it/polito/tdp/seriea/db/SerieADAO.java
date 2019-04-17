package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Season(res.getInt("season"), res.getString("description"))) ;
				
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Team(res.getString("team"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public void popolaGrafo(SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo, Season s) {
		final String sql = "select t1.team as casa, t2.team as trasferta, ftr as risultato " + 
							"from teams as t1, seasons as s, matches as m, teams as t2 " + 
							"where t1.team=m.HomeTeam " + 
							"and t2.team=m.AwayTeam " + 
							"and s.season=m.Season " + 
							"and s.season=?";
		int peso;
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			st.setInt(1, s.getSeason());
			
			while(res.next()) {
				String nomeSquadraCasa = res.getString("casa");
				String nomeSquadraTrasferta = res.getString("trasferta");
				String risultato = res.getString("risultato");
				
				Team squadraInCasa = new Team(nomeSquadraCasa);
				Team squadraInTrasferta = new Team(nomeSquadraTrasferta);
				
				if(!grafo.containsVertex(squadraInCasa)) {
					grafo.addVertex(squadraInCasa);
				}
				if(!grafo.containsVertex(squadraInTrasferta)) {
					grafo.addVertex(squadraInTrasferta);
				}
//				if(!grafo.containsEdge(squadraInCasa, squadraInTrasferta) && !grafo.containsEdge(squadraInTrasferta, squadraInCasa)) {
//					grafo.addEdge(squadraInCasa, squadraInTrasferta);
//				}
				if(risultato.compareTo("H")==0) {
					peso=1;
				}
				else if(risultato.compareTo("A")==0){
					peso=-1;
				}
				else {
					peso=0;
				}
				Graphs.addEdge(grafo, squadraInCasa, squadraInTrasferta, peso);
			}
			conn.close();
			
		} catch (SQLException e) {
			throw new RuntimeException("Errore Db");
		}
	}
	
	public List<Match> getRisultatiPartite(Season s) {
		final String sql = "select t1.team as casa, t2.team as trasferta, ftr as risultato " + 
				"from teams as t1, seasons as s, matches as m, teams as t2 " + 
				"where t1.team=m.HomeTeam " + 
				"and t2.team=m.AwayTeam " + 
				"and s.season=m.Season " + 
				"and s.season=?";
		
		List<Match> calendario = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection() ;
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			st.setInt(1, s.getSeason());
			
			while(res.next()) {
				String nomeSquadraCasa = res.getString("casa");
				String nomeSquadraTrasferta = res.getString("trasferta");
				String risultato = res.getString("risultato");
				
				Team squadraInCasa = new Team(nomeSquadraCasa);
				Team squadraInTrasferta = new Team(nomeSquadraTrasferta);
				Match match = new Match(squadraInCasa, squadraInTrasferta, risultato);
				
				calendario.add(match);
				
			}
			conn.close();
			return calendario;
			
		} catch (SQLException e) {
			throw new RuntimeException("Errore Db");
		}
		
	}


}
