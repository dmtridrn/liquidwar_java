package liquidwars;

import liquidwars.vue.FenetrePrincipale;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principale du jeu Liquid Wars (Futur GOTY)
 * C'est ici que l'application JavaFX démarre et initialise la fenêtre du jeu.
 */
public class LiquidWars extends Application {

    /**
     * Point d'entrée standard de l'application.
     * Lance le thread JavaFX.
     *
     * @param args arguments de la ligne de commande (non utilisés ici)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialisation de l'affichage.
     * Cette méthode instancie la fenêtre principale et lui passe la main.
     *
     * @param stage la fenêtre principale (stage) fournie par JavaFX
     * @throws Exception en cas de problème au lancement
     */
    @Override
    public void start(Stage stage) throws Exception {
        new FenetrePrincipale().start(stage);
    }
}