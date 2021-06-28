/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.imdb;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimili"
    private Button btnSimili; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimulazione"
    private Button btnSimulazione; // Value injected by FXMLLoader

    @FXML // fx:id="boxGenere"
    private ComboBox<String> boxGenere; // Value injected by FXMLLoader

    @FXML // fx:id="boxAttore"
    private ComboBox<Actor> boxAttore; // Value injected by FXMLLoader

    @FXML // fx:id="txtGiorni"
    private TextField txtGiorni; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doAttoriSimili(ActionEvent event) {
    	
    	
    	if(boxAttore.getValue()!=null) {
    		
    		this.txtResult.setText("ATTORI SIMILI A: "+this.boxAttore.getValue()+"\n\n");
    		
    		for(Actor a : this.model.attoriSimili(this.boxAttore.getValue())) {
    			this.txtResult.appendText(a+"\n");
    		}
    	}

    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	if(boxGenere.getValue()!=null) {
    		this.model.creaGrafo(boxGenere.getValue());
    		
    	} else {
    		txtResult.setText("Devi prima selezionare un genere.");
    		return;
    	}
    	
    	int vertici = this.model.getGrafo().vertexSet().size();
    	int archi = this.model.getGrafo().edgeSet().size();
    	
    	txtResult.setText("Grafo creato:\n#Vertici: "+vertici+"\n#Archi: "+archi);
    	
    	List<Actor> attori = new ArrayList<>(model.getGrafo().vertexSet());
    	Collections.sort(attori);
    	this.boxAttore.getItems().clear();
    	this.boxAttore.getItems().addAll(attori);
    }

    @FXML
    void doSimulazione(ActionEvent event) {
    	
    	int n = Integer.parseInt(this.txtGiorni.getText());
    	
    	this.model.simula(n);
    	
    	this.txtResult.setText("Attori intervistati:\n\n");
    	
    	for(Actor a : this.model.getAttoriIntervistati()) {
    		this.txtResult.appendText(a+"\n");
    	}
    	
    	this.txtResult.appendText("\nNumero di giorni di pausa: "+model.getTotPause());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimili != null : "fx:id=\"btnSimili\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimulazione != null : "fx:id=\"btnSimulazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxGenere != null : "fx:id=\"boxGenere\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxAttore != null : "fx:id=\"boxAttore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtGiorni != null : "fx:id=\"txtGiorni\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	this.boxGenere.getItems().addAll(model.getGenres());
    }
}
