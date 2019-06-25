package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.model.Campionato;
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
	
	public List<Team> listTeams(Map<String, Team> idMap) {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				if(!idMap.containsKey(res.getString("team"))) {
					Team team = new Team(res.getString("team"));
					result.add(team);
					idMap.put(res.getString("team"), team);
				}else {
					result.add(idMap.get(res.getString("team")));
				}
				
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Campionato> getCampionato(Map<String, Team> idMap, Season s) {
		final String sql=	"select t1.team as teamC, t2.team as teamT, m1.FTR as ris " + 
							"from matches as m1, teams as t1, teams as t2 " + 
							"where m1.Season=? " + 
							"and m1.HomeTeam != m1.AwayTeam " + 
							"and t1.team=m1.HomeTeam " + 
							"and t2.team=m1.AwayTeam";
		List<Campionato> result = new LinkedList<>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s.getSeason());
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				Team casa = idMap.get(res.getString("teamC"));
				Team tras = idMap.get(res.getString("teamT"));
				if(casa!=null && tras!=null){
					Campionato campionato = new Campionato(casa, tras, res.getString("ris"));
					result.add(campionato);
				}			
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}


}
