package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {

	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons";

		List<Season> result = new ArrayList<>();

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Season(res.getInt("season"), res.getString("description")));
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Team> listTeams(Map <String,Team> teamIdMap) {
		String sql = "SELECT team FROM teams";

		List<Team> result = new ArrayList<>();

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				Team t = new Team(res.getString("team")) ;
				teamIdMap.put(t.getTeam(), t) ;
				result.add(t);
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Match> listMatches(Season season, Map<String, Team> teamIdMap) {
		String sql = "select match_id, season, `Div`, Date, HomeTeam, AwayTeam, FTHG, FTAG, FTR "
				+ "from matches where season=?";
		
		Connection conn = DBConnect.getConnection() ;
		
		List<Match> result = new ArrayList<>() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, season.getSeason());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Match(
						res.getInt("match_id"),
						season,
						res.getString("Div"),
						res.getDate("Date").toLocalDate(),
						teamIdMap.get(res.getString("HomeTeam")),
						teamIdMap.get(res.getString("AwayTeam")),
						res.getInt("FTHG"),
						res.getInt("FTAG"),
						res.getString("FTR")
						)) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

}
