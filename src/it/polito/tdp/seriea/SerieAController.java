/**
 * Controller Class for 'SerieA.fxml' 
 */

package it.polito.tdp.seriea;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;

import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class SerieAController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxSeason"
    private ChoiceBox<Season> boxSeason; // Value injected by FXMLLoader

    @FXML // fx:id="boxTeam"
    private ChoiceBox<Team> boxTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

	private Model model;

    @FXML
    void handleCarica(ActionEvent event) {
    	
    	Season season = boxSeason.getValue() ;
    	
    	if(season==null) {
    		txtResult.appendText("ERRORE: seleziona una stagione\n");
    		return ;
    	}
    	
    	model.caricaPartite(season) ;
    	
    	boxTeam.getItems().clear();
    	boxTeam.getItems().addAll(model.getCurrentTeams()) ;
    	Collections.sort(boxTeam.getItems(), new Comparator<Team>() {

			@Override
			public int compare(Team o1, Team o2) {
				return o1.getTeam().compareTo(o1.getTeam());
			}
		}) ;
    			
    	txtResult.appendText(String.format("Risultati stagione %s\n", season.getDescription()));
    	for(Team t: model.getClassifica()) {
    		txtResult.appendText(String.format("%s %d\n", t.getTeam(), t.getPunti()));
    	}

    }

    @FXML
    void handleDomino(ActionEvent event) {
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxSeason != null : "fx:id=\"boxSeason\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert boxTeam != null : "fx:id=\"boxTeam\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";
    }

	public void setModel(Model model) {
		this.model = model ;
		
		boxSeason.getItems().addAll(model.getSeasons()) ;
	}
}
