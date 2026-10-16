package liquidwars.vue;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

/**
 * Menu de pause affiché par-dessus le jeu.
 * Permet de reprendre la partie, de retourner au menu principal ou de quitter.
 */
public class MenuPause extends StackPane {

    /**
     * Crée le menu de pause avec ses boutons d'action.
     * 
     * @param actionReprendre     L'action à effectuer pour revenir au jeu
     * @param actionMenuPrincipal L'action pour retourner au menu principal
     * @param actionQuitter       L'action pour fermer l'application
     */
    public MenuPause(Runnable actionReprendre, Runnable actionMenuPrincipal, Runnable actionQuitter) {

        // p'tit fond mi-transparent
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        this.setVisible(false);

        VBox boiteBoutons = new VBox(20);
        boiteBoutons.setAlignment(Pos.CENTER);

        boiteBoutons.setMaxSize(300, 300);

        Label titre = new Label("PAUSE");
        titre.setStyle("-fx-text-fill: white;");

        titre.setFont(new Font("Arial", 40));

        Button btnReprendre = creerBouton("Reprendre");
        btnReprendre.setOnAction(e -> actionReprendre.run());

        Button btnMenu = creerBouton("Menu Principal");
        btnMenu.setOnAction(e -> actionMenuPrincipal.run());

        Button btnQuitter = creerBouton("Quitter le jeu");
        btnQuitter.setOnAction(e -> actionQuitter.run());

        boiteBoutons.getChildren().addAll(titre, btnReprendre, btnMenu, btnQuitter);
        this.getChildren().add(boiteBoutons);
    }

    /**
     * Crée un bouton avec le style standard du menu.
     * 
     * @param texte Le label du bouton
     * @return Le bouton configuré
     */
    private Button creerBouton(String texte) {
        Button b = new Button(texte);
        b.setPrefWidth(200);

        return b;
    }

    /**
     * Affiche le menu s'il est caché, ou le cache s'il est visible.
     */
    public void basculerVisibilite() {
        this.setVisible(!this.isVisible());
    }

}