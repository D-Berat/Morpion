package application;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ReglesController {
    
    public void gererFermeture(ActionEvent event) {
    	// permet de fermer uniquement cette fenêtre modale sans quitter le jeu
        Node noeudSource = (Node) event.getSource();
        Stage fenetre = (Stage) noeudSource.getScene().getWindow();
        fenetre.close();
    }
}