package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
			Map<Team, Team> team = new HashMap<>();
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s.getSeason());
			ResultSet res = st.executeQuery() ;
			
			
			while(res.next()) {
				String squadraCasa = res.getString("casa");
				String squadraTrasferta = res.getString("trasferta");
				double peso = res.getDouble("peso");
				
				int punteggioCasa=0;
				int punteggioTrasferta=0;
				
				Team teamCasa = new Team(squadraCasa);
				Team teamTrasferta = new Team(squadraTrasferta);
				//Se usassi direttamente il team appena creato, facendo ogni volta new, il programma sovrascrive l'oggetto
				//e risetterebbe il punteggio a 0 quindi aggiungo i team ad una mappa in modo che richiamando i team dalla
				//mappa quando creo il grafo sono sicuro di avere sempre il primo creato
				
				if(!team.containsKey(teamCasa)) {
					team.put(teamCasa, teamCasa);
				}
				if(!team.containsKey(teamTrasferta)) {
					team.put(teamTrasferta, teamTrasferta);
				}
				
				if(peso==1) {
					punteggioCasa=3;
					punteggioTrasferta=0;
				}
				else if(peso==0) {
					punteggioCasa=1;
					punteggioTrasferta=1;
				}
				else {
					punteggioCasa=0;
					punteggioTrasferta=3;
				}
				
				if(!grafo.containsVertex(teamCasa)) {
					grafo.addVertex(team.get(teamCasa));
				}
				if(!grafo.containsVertex(teamTrasferta)) {
					grafo.addVertex(team.get(teamTrasferta));
				}
				if(!grafo.containsEdge(teamCasa, teamTrasferta)) {
					DefaultWeightedEdge edge = grafo.addEdge(team.get(teamCasa), team.get(teamTrasferta));
					grafo.setEdgeWeight(edge, peso);
					team.get(teamCasa).setPunteggio(punteggioCasa);
					team.get(teamTrasferta).setPunteggio(punteggioTrasferta);
				}
				else {
					DefaultWeightedEdge edge= grafo.getEdge(team.get(teamCasa), team.get(teamTrasferta));
					grafo.getEdgeSource(edge).setPunteggio(punteggioCasa);
					grafo.getEdgeTarget(edge).setPunteggio(punteggioTrasferta);	
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			throw new RuntimeException("Errore Db");	
		}
		
	}


}
