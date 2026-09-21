package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FinController {

	@FXML private Label labelResultat;
	@FXML private Label labelScoreJ1;
	@FXML private Label labelScoreJ2;

	@FXML private String nomJ1;
	@FXML private Color couleurJ1;
	@FXML private int scoreJ1;
    
	@FXML private String nomJ2;
	@FXML private Color couleurJ2;
	@FXML private int scoreJ2;

	// récupération des données depuis le contrôleur du jeu
    public void initialiserDonnees(String p1Nom, Color c1, int s1, String p2Nom, Color c2, int s2, String texteResultat, Color couleurResultat) {
        this.nomJ1 = p1Nom;
        this.couleurJ1 = c1;
        this.scoreJ1 = s1;
        this.nomJ2 = p2Nom;
        this.couleurJ2 = c2;
        this.scoreJ2 = s2;

        labelResultat.setText(texteResultat);
        if (couleurResultat != null) {
            labelResultat.setTextFill(couleurResultat);
        }

        // mise à jour du tableau de scores
        labelScoreJ1.setText(nomJ1 + " (X) : " + scoreJ1 + " victoires");
        labelScoreJ2.setText(nomJ2 + " (O) : " + scoreJ2 + " victoires");
    }

    public void gererRejouer(ActionEvent event) {
        try {
            FXMLLoader chargeur = new FXMLLoader(getClass().getResource("morpion.fxml"));
            Parent racine = chargeur.load();

            MorpionController controleurJeu = chargeur.getController();
            controleurJeu.initialiserDonnees(nomJ1, couleurJ1, scoreJ1, nomJ2, couleurJ2, scoreJ2, "Aléatoire");

            Stage fenetre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            fenetre.setScene(new Scene(racine));
            fenetre.setTitle("Morpion - Partie en cours");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gererNouvellePartie(ActionEvent event) {
    	// retour à la page de configuration (efface les scores)
        try {
            FXMLLoader chargeur = new FXMLLoader(getClass().getResource("config.fxml"));
            Parent racine = chargeur.load();

            Stage fenetre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            fenetre.setScene(new Scene(racine));
            fenetre.setTitle("Morpion - Configuration");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gererQuitter(ActionEvent event) {
        System.exit(0);
    }
}