package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams";

		List<Team> result = new ArrayList<>();

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Team(res.getString("team")));
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Match> listMatches(Season season, List<Team> teams) {
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
						teams.get(teams.indexOf(new Team(res.getString("HomeTeam")))),
						teams.get(teams.indexOf(new Team(res.getString("AwayTeam")))),
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
