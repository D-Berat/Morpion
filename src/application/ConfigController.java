package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfigController {

    @FXML private TextField champJoueur1;
    @FXML private TextField champJoueur2;
    @FXML private ColorPicker selecteurCouleur1;
    @FXML private ColorPicker selecteurCouleur2;
    @FXML private ComboBox<String> choixPremierJoueur;

    public void initialize() { 
    	// initialisation des valeurs par défaut pour éviter les erreurs (NullPointerException)
        selecteurCouleur1.setValue(Color.BLUE);
        selecteurCouleur2.setValue(Color.RED);
        
        choixPremierJoueur.getItems().addAll("Joueur 1", "Joueur 2", "Aléatoire");
        choixPremierJoueur.getSelectionModel().selectFirst();
    }

    public void gererLancementJeu(ActionEvent event) {
    	// opérateur ternaire : si le champ est vide, on attribue un pseudo par défaut
        String nomJ1 = champJoueur1.getText().isEmpty() ? "Joueur 1" : champJoueur1.getText();
        String nomJ2 = champJoueur2.getText().isEmpty() ? "Joueur 2" : champJoueur2.getText();
        Color couleur1 = selecteurCouleur1.getValue();
        Color couleur2 = selecteurCouleur2.getValue();
        String premierJoueur = choixPremierJoueur.getValue();

        try {
            FXMLLoader chargeur = new FXMLLoader(getClass().getResource("morpion.fxml"));
            Parent racine = chargeur.load();
            
            // on récupère le contrôleur de la page de jeu pour lui envoyer nos données
            MorpionController controleurJeu = chargeur.getController();
            controleurJeu.initialiserDonnees(nomJ1, couleur1, nomJ2, couleur2, premierJoueur);

            // on change de scène sur la fenêtre actuelle
            Stage fenetre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            fenetre.setScene(new Scene(racine));
            fenetre.setTitle("Morpion - Partie en cours");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gererRegles(ActionEvent event) {
        try {
            FXMLLoader chargeur = new FXMLLoader(getClass().getResource("regles.fxml"));
            Parent racine = chargeur.load();

            // création d'une fenêtre modale
            Stage fenetreRegles = new Stage();
            fenetreRegles.initModality(Modality.APPLICATION_MODAL);
            fenetreRegles.setTitle("Règles du jeu");
            fenetreRegles.setScene(new Scene(racine));
            fenetreRegles.setResizable(false);
            fenetreRegles.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}