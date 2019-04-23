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
		final String sql = "select HomeTeam as casa, AwayTeam as trasferta, " + 
							"case " + 
							"when ftr = 'H' then 1 " + 
							"when ftr = 'A' then -1 " + 
							"else 0 " + 
							"end as risultato " + 
							"from matches as m1 " + 
							"where season = ? ";
		
		Map<Team, Team> mappa = new HashMap<>();
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s.getSeason());
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				String squadraCasa = res.getString("casa");
				String squadraTrasferta = res.getString("trasferta");
				double peso = res.getDouble("risultato");
				
				Team casa = new Team(squadraCasa);
				Team trasferta = new Team(squadraTrasferta);
				
				int punteggioCasa =0;
				int punteggioTrasferta=0;
				
				if(!mappa.containsKey(casa)) {
					mappa.put(casa, casa);
				}
				if(!mappa.containsKey(trasferta)) {
					mappa.put(trasferta, trasferta);
				}
				
				if(peso==1) {
					punteggioCasa =3;
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
				if(!grafo.containsVertex(mappa.get(casa))) {
					grafo.addVertex(mappa.get(casa));
				}
				if(!grafo.containsVertex(mappa.get(trasferta))) {
					grafo.addVertex(mappa.get(trasferta));
				}
				if(!grafo.containsEdge(mappa.get(casa), mappa.get(trasferta))) {
					DefaultWeightedEdge edge = grafo.addEdge(mappa.get(casa), mappa.get(trasferta));
					grafo.setEdgeWeight(edge, peso);
					mappa.get(casa).setPunteggio(punteggioCasa);
					mappa.get(trasferta).setPunteggio(punteggioTrasferta);
				}
				else {
					DefaultWeightedEdge edge =grafo.getEdge(mappa.get(casa), mappa.get(trasferta));
					grafo.getEdgeSource(edge).setPunteggio(punteggioCasa);
					grafo.getEdgeTarget(edge).setPunteggio(punteggioTrasferta);
				}
			}
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		
		
	}


}
