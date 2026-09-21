package application;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MorpionController {

	@FXML private Label labelTour;
	@FXML private Label labelScoreJoueur1;
	@FXML private Label labelScoreJoueur2;
	@FXML private GridPane grilleJeu;

	@FXML private String nomJ1 = "Joueur 1";
	@FXML private Color couleurJ1 = Color.BLACK;
	@FXML private String nomJ2 = "Joueur 2";
    @FXML private Color couleurJ2 = Color.BLACK;

    // matrice 3x3 pour gérer la logique du jeu en arrière-plan
    @FXML private char[][] donneesPlateau = new char[3][3];
    @FXML private char joueurActuel = 'X';
    @FXML private boolean jeuActif = true;
    
    @FXML private int scoreJoueur1 = 0;
    @FXML private int scoreJoueur2 = 0;

    public void initialize() {
        reinitialiserPlateauLogique();
    }

    public void initialiserDonnees(String p1Nom, Color c1, String p2Nom, Color c2, String premier) {
        initialiserDonnees(p1Nom, c1, 0, p2Nom, c2, 0, premier);
    }
    // surcharge de la méthode pour la revanche (conservation des scores)
    public void initialiserDonnees(String p1Nom, Color c1, int s1, String p2Nom, Color c2, int s2, String premier) {
        this.nomJ1 = p1Nom;
        this.couleurJ1 = c1;
        this.scoreJoueur1 = s1;
        
        this.nomJ2 = p2Nom;
        this.couleurJ2 = c2;
        this.scoreJoueur2 = s2;

        if ("Joueur 2".equals(premier)) {
            joueurActuel = 'O';
        } else if ("Aléatoire".equals(premier)) {
            joueurActuel = Math.random() < 0.5 ? 'X' : 'O';
        } else {
            joueurActuel = 'X';
        }

        mettreAJourAffichageTour();
        mettreAJourAffichageScore();
    }

    public void gererClicCase(ActionEvent event) {
        if (!jeuActif) return;

        Button boutonClique = (Button) event.getSource();
        // on récupère les coordonnées de la case stockées dans l'UserData du FXML
        String donneesUtilisateur = (String) boutonClique.getUserData();

        if (donneesUtilisateur == null) return;

        String[] coordonnees = donneesUtilisateur.split(",");
        int ligne = Integer.parseInt(coordonnees[0]);
        int colonne = Integer.parseInt(coordonnees[1]);
        
        // si la case n'est pas vide, on ne fait rien
        if (donneesPlateau[ligne][colonne] != '\0') return;

        // mise à jour de la logique et de l'interface
        donneesPlateau[ligne][colonne] = joueurActuel;
        boutonClique.setText(String.valueOf(joueurActuel));
        boutonClique.setTextFill(joueurActuel == 'X' ? couleurJ1 : couleurJ2);

        // vérification de la condition de victoire
        if (verifierVictoire(ligne, colonne)) {
            jeuActif = false;
            mettreAJourScores(); 
            String nomGagnant = (joueurActuel == 'X') ? nomJ1 : nomJ2;
            Color couleurGagnant = (joueurActuel == 'X') ? couleurJ1 : couleurJ2;
            
            labelTour.setText("Victoire de " + nomGagnant);
            labelTour.setTextFill(couleurGagnant);
            
            declencherEcranFin("Victoire de " + nomGagnant + " !", couleurGagnant);
            return;
        }
        
        // vérification du match nul si la grille est pleine	
        if (estMatchNul()) {
            jeuActif = false;
            labelTour.setText("Match Nul");
            labelTour.setTextFill(Color.BLACK);
            declencherEcranFin("Match Nul !", Color.BLACK);
            return;
        }

        changerJoueur();
    }

    private void declencherEcranFin(String messageResultat, Color couleurResultat) {
    	// petite pause de 1,5 seconde pour que le joueurs voient l'alignement gagnant
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> {
            try {
                FXMLLoader chargeur = new FXMLLoader(getClass().getResource("fin.fxml"));
                Parent racine = chargeur.load();
                
                FinController controleurFin = chargeur.getController();
                controleurFin.initialiserDonnees(nomJ1, couleurJ1, scoreJoueur1, nomJ2, couleurJ2, scoreJoueur2, messageResultat, couleurResultat);
                
                Stage fenetre = (Stage) labelTour.getScene().getWindow();
                fenetre.setScene(new Scene(racine));
                fenetre.setTitle("Morpion - Fin de partie");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pause.play();
    }

    public void gererReinitialisation(ActionEvent event) {
        reinitialiserPlateauLogique();
        jeuActif = true;
        joueurActuel = 'X';
        mettreAJourAffichageTour();

        // on nettoie visuellement tous les boutons de la grille
        for (Node noeud : grilleJeu.getChildren()) {
            if (noeud instanceof Button) {
                Button btn = (Button) noeud;
                btn.setText("");
                btn.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: transparent; -fx-font-size: 70px;");
            }
        }
    }

    public void gererQuitter(ActionEvent event) {
        System.exit(0);
    }

    public void gererRegles(ActionEvent event) {
        try {
            FXMLLoader chargeur = new FXMLLoader(getClass().getResource("regles.fxml"));
            Parent racine = chargeur.load();
            
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

    public void reinitialiserPlateauLogique() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                donneesPlateau[i][j] = '\0';
            }
        }
    }

    public void mettreAJourAffichageTour() {
        String nomActuel = (joueurActuel == 'X') ? nomJ1 : nomJ2;
        labelTour.setText("Tour de : " + nomActuel);
        labelTour.setTextFill(joueurActuel == 'X' ? couleurJ1 : couleurJ2);
    }
    
    public void mettreAJourAffichageScore() {
        labelScoreJoueur1.setText(nomJ1 + " (X) : " + scoreJoueur1);
        labelScoreJoueur2.setText(nomJ2 + " (O) : " + scoreJoueur2);
    }
    
    public void mettreAJourScores() {
        if (joueurActuel == 'X') scoreJoueur1++;
        else scoreJoueur2++;
        mettreAJourAffichageScore();
    }

    public void changerJoueur() {
        joueurActuel = (joueurActuel == 'X') ? 'O' : 'X';
        mettreAJourAffichageTour();
    }

    public boolean verifierVictoire(int ligne, int colonne) {
    	// test de la ligne
        if (donneesPlateau[ligne][0] == joueurActuel && donneesPlateau[ligne][1] == joueurActuel && donneesPlateau[ligne][2] == joueurActuel) {
            mettreEnSurbrillanceCasesGagnantes(ligne, 0, ligne, 1, ligne, 2);
            return true;
        }
    	// test de la colonne
        if (donneesPlateau[0][colonne] == joueurActuel && donneesPlateau[1][colonne] == joueurActuel && donneesPlateau[2][colonne] == joueurActuel) {
            mettreEnSurbrillanceCasesGagnantes(0, colonne, 1, colonne, 2, colonne);
            return true;
        }
    	// test de la diagonale principale
        if (ligne == colonne && donneesPlateau[0][0] == joueurActuel && donneesPlateau[1][1] == joueurActuel && donneesPlateau[2][2] == joueurActuel) {
            mettreEnSurbrillanceCasesGagnantes(0, 0, 1, 1, 2, 2);
            return true;
        }
    	// test de la diagonale secondaire
        if (ligne + colonne == 2 && donneesPlateau[0][2] == joueurActuel && donneesPlateau[1][1] == joueurActuel && donneesPlateau[2][0] == joueurActuel) {
            mettreEnSurbrillanceCasesGagnantes(0, 2, 1, 1, 2, 0);
            return true;
        }
        return false;
    }

    public void mettreEnSurbrillanceCasesGagnantes(int l1, int c1, int l2, int c2, int l3, int c3) {
        // mettre les 3 cases gagnantes en vert
    	String styleSurbrillance = "-fx-background-color: #90EE90; -fx-border-color: black; -fx-border-width: 2; -fx-font-size: 70px;";
        obtenirBoutonParCoordonnees(l1, c1).setStyle(styleSurbrillance);
        obtenirBoutonParCoordonnees(l2, c2).setStyle(styleSurbrillance);
        obtenirBoutonParCoordonnees(l3, c3).setStyle(styleSurbrillance);
    }

    public Button obtenirBoutonParCoordonnees(int ligne, int colonne) {
        String coordonneesCibles = ligne + "," + colonne;
        for (Node noeud : grilleJeu.getChildren()) {
            if (noeud instanceof Button && coordonneesCibles.equals(noeud.getUserData())) {
                return (Button) noeud;
            }
        }
        return null;
    }

    public boolean estMatchNul() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (donneesPlateau[i][j] == '\0') return false;
            }
        }
        return true;
    }
}