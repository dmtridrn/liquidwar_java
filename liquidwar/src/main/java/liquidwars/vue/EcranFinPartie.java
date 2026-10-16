package liquidwars.vue;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import liquidwars.modele.Equipe;

/**
 * Gère l'affichage de l'écran de fin de partie.
 * Présente le vainqueur, le classement final et permet de rejouer ou quitter.
 */
public class EcranFinPartie {

    private final Stage stage;
    private final Equipe gagnant;
    private final List<Equipe> classement;
    private final long tempsDebut;

    /**
     * Initialise l'écran de fin avec les résultats de la partie.
     * 
     * @param stage      La fenêtre principale
     * @param gagnant    L'équipe qui a gagné
     * @param classement La liste des équipes ordonnée par classement
     * @param tempsDebut Le moment du début de la partie (pour calculer la survie)
     */
    public EcranFinPartie(Stage stage, Equipe gagnant, List<Equipe> classement, long tempsDebut) {
        this.stage = stage;
        this.gagnant = gagnant;
        this.classement = classement;
        this.tempsDebut = tempsDebut;
    }

    /**
     * Construit et affiche la scène de fin de partie.
     * Affiche le vainqueur, le détail du classement (particules, temps de survie)
     * et les boutons d'action.
     */
    public void afficher() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);

        // le p'tit titre pour annoncer la fin
        Text titre = new Text("PARTIE TERMINÉE !");
        titre.setFont(new Font(60));
        titre.setFill(Color.BLACK);

        // l'equipe des winners
        Text texteGagnant = new Text("Vainqueur : Équipe");
        texteGagnant.setFont(new Font(30));
        texteGagnant.setFill(gagnant.getCouleur());

        // le classement tu connais
        VBox classementBox = new VBox(15);
        classementBox.setAlignment(Pos.CENTER);

        Text titreClassement = new Text("Classement Final");
        titreClassement.setFont(new Font(25));
        titreClassement.setFill(Color.BLACK);
        classementBox.getChildren().add(titreClassement);

        for (int i = 0; i < classement.size(); i++) {
            Equipe equipe = classement.get(i);

            // calculer le temps de survie
            String tempsTexte;
            if (equipe.estElimine()) {
                long tempsSurvie = equipe.getTempsElimination() - tempsDebut;
                long secondes = tempsSurvie / 1000;
                long minutes = secondes / 60;
                secondes = secondes % 60;
                tempsTexte = String.format("%dm %ds", minutes, secondes);
            } else {
                tempsTexte = "Survivant";
            }

            Text ligne = new Text(
                    (i + 1) + ". Équipe - " + equipe.getNombreParticules() + " particules - " + tempsTexte);
            ligne.setFont(new Font(20));
            ligne.setFill(equipe.getCouleur());
            classementBox.getChildren().add(ligne);
        }

        // les petits boutons
        Button btnRejouer = new Button("REJOUER");
        btnRejouer.setStyle("-fx-font-size: 20px; -fx-padding: 15 40;");
        btnRejouer.setOnAction(e -> {
            MenuPrincipal menu = new MenuPrincipal(stage);
            menu.afficher();
        });

        Button btnQuitter = new Button("QUITTER");
        btnQuitter.setStyle("-fx-font-size: 20px; -fx-padding: 15 40;");
        btnQuitter.setOnAction(e -> stage.close());

        root.getChildren().addAll(titre, texteGagnant, classementBox, btnRejouer, btnQuitter);

        Scene scene = new Scene(root, 1000, 800);
        stage.setScene(scene);
    }

}