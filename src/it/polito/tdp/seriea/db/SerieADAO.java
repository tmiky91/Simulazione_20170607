package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

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
			// TODO Auto-generated catch block
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
		final String sql="select HomeTeam as casa, AwayTeam as trasferta, " + 
				"case " + 
				"when ftr = \"H\" then 1 " + 
				"when ftr = \"A\" then -1 " + 
				"else 0 " + 
				"end as peso " + 
				"from matches as m " + 
				"where season = ?";
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s.getSeason());
			ResultSet res = st.executeQuery() ;
			
			
			while(res.next()) {
				String squadraCasa = res.getString("casa");
				String squadraTrasferta = res.getString("trasferta");
				double peso = res.getDouble("peso");
				
				int punteggio=0;
				
				Team teamCasa = new Team(squadraCasa);
				Team teamTrasferta = new Team(squadraTrasferta);
				
				if(peso==1) {
					punteggio=3;
				}
				if(peso==0) {
					punteggio=1;
				}
				else {
					punteggio=0;
				}
				
				if(!grafo.containsVertex(teamCasa)) {
					grafo.addVertex(teamCasa);
				}
				if(!grafo.containsVertex(teamTrasferta)) {
					grafo.addVertex(teamTrasferta);
				}
				if(!grafo.containsEdge(teamCasa, teamTrasferta)) {
					DefaultWeightedEdge edge = grafo.addEdge(teamCasa, teamTrasferta);
					grafo.setEdgeWeight(edge, peso);
					teamCasa.setPunteggio(punteggio);
				}
				
			}
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();	
		}
		
	}


}
