package liquidwars.vue;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import liquidwars.modele.FabriquePartie;
import liquidwars.modele.ConfigurationPartie.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'affichage et la logique du menu principal du jeu.
 *
 * Cette classe permet à l'utilisateur de choisir le nombre de joueurs (2 à 4),
 * de configurer les types de joueurs (Humains ou IA avec différentes
 * stratégies) et de lancer la partie une fois la configuration validée.
 */
public class MenuPrincipal {
    private Stage stage;

    /**
     * Initialise le menu principal.
     * 
     * @param stage La fenêtre principale de l'application où le menu sera affiché.
     */
    public MenuPrincipal(Stage stage) {
        this.stage = stage;
    }

    /**
     * Affiche l'écran d'accueil du menu principal.
     *
     * Présente le titre du jeu et les boutons pour sélectionner le nombre de joueurs
     * ou quitter l'application.
     */
    public void afficher() {
        VBox menuBox = new VBox(30);
        menuBox.setAlignment(Pos.CENTER);

        Text titre = new Text("LIQUID WARS");
        titre.setFont(new Font(60));

        // le p'tit label pour le nb de joueurs
        Text sousTitre = new Text("Choix du nombre de joueurs et de leurs stratégies : ");
        sousTitre.setFont(new Font(20));

        HBox boutonsNbJoueurs = new HBox(20);
        boutonsNbJoueurs.setAlignment(Pos.CENTER);

        // les boutons pour choisir le nb de joueurs
        for (int i = 2; i <= 4; i++) {
            int nbJoueurs = i;
            Button btn = new Button(nbJoueurs + " joueurs");
            btn.setOnAction(e -> afficherSelectionIA(nbJoueurs));
            boutonsNbJoueurs.getChildren().add(btn);
        }

        Button btnQuitter = new Button("QUITTER");
        btnQuitter.setOnAction(e -> stage.close());

        menuBox.getChildren().addAll(titre, sousTitre, boutonsNbJoueurs, btnQuitter);

        Scene sceneMenu = new Scene(menuBox, 1000, 800);
        stage.setScene(sceneMenu);
    }

    /**
     * Affiche l'écran de configuration des stratégies pour les joueurs sélectionnés.
     *
     * Permet de choisir si le joueur 2 est un humain (clavier) et de sélectionner
     * les stratégies des IA pour les autres joueurs.
     * 
     * @param nbJoueurs Le nombre total de joueurs sélectionné précédemment.
     */
    private void afficherSelectionIA(int nbJoueurs) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);

        Text titre = new Text("SÉLECTION DES IA");
        titre.setFont(new Font(50));

        Text info = new Text("Joueur 1 : HUMAIN (souris)");
        info.setFont(new Font(18));

        CheckBox deuxJoueursHumains = new CheckBox("Joueur 2 : HUMAIN (clavier ZQSD)");
        deuxJoueursHumains.setFont(new Font(18));

        VBox selectionIAs = new VBox(20);
        selectionIAs.setAlignment(Pos.CENTER);

        List<TypeStrategie> selectionActuelle = new ArrayList<>();
        List<Button[]> tousLesBoutons = new ArrayList<>();

        // quand on check/uncheck joueur 2 humain, on rafraîchit juste la zone des IAs
        deuxJoueursHumains.selectedProperty().addListener((obs, oldVal, newVal) -> {
            selectionIAs.getChildren().clear();
            mettreAJourSelectionIAs(selectionIAs, nbJoueurs, deuxJoueursHumains,
                    selectionActuelle, tousLesBoutons);
        });

        // on setup la zone des IAs au démarrage
        mettreAJourSelectionIAs(selectionIAs, nbJoueurs, deuxJoueursHumains,
                selectionActuelle, tousLesBoutons);

        Button btnValider = new Button("JOUER");
        btnValider.setOnAction(e -> {
            boolean deuxHumains = deuxJoueursHumains.isSelected();
            int nbIARequis = deuxHumains ? nbJoueurs - 2 : nbJoueurs - 1;

            // vérifier qu'on a bien sélectionné tous les IAs
            if (selectionActuelle.size() == nbIARequis &&
                    selectionActuelle.stream().noneMatch(s -> s == null)) {
                lancerJeuAvecConfiguration(selectionActuelle, deuxHumains);
            }
        });

        Button btnRetour = new Button("RETOUR");
        btnRetour.setOnAction(e -> afficher());

        root.getChildren().addAll(titre, info, deuxJoueursHumains, selectionIAs, btnValider, btnRetour);

        Scene scene = new Scene(root, 1000, 800);
        stage.setScene(scene);
    }

    /**
     * Met à jour dynamiquement la liste des sélecteurs de stratégie.
     *
     * Reconstruit les boutons de choix de stratégie en fonction du nombre de
     * joueurs
     * et de l'état de la case "Joueur 2 Humain".
     * 
     * @param selectionIAs       Le conteneur VBox où afficher les choix.
     * @param nbJoueurs          Le nombre total de joueurs.
     * @param deuxJoueursHumains La CheckBox indiquant si le joueur 2 est humain.
     * @param selectionActuelle  La liste stockant les stratégies choisies.
     * @param tousLesBoutons     La liste pour garder une référence sur les boutons
     *                           (pour la gestion du style).
     */
    private void mettreAJourSelectionIAs(VBox selectionIAs, int nbJoueurs,
            CheckBox deuxJoueursHumains,
            List<TypeStrategie> selectionActuelle,
            List<Button[]> tousLesBoutons) {
        tousLesBoutons.clear();

        // pour chaque joueur (sauf le 1er qui est toujours humain)
        for (int i = 1; i < nbJoueurs; i++) {
            final int index = i;

            // déterminer le texte du joueur
            String texteJoueur;
            if (deuxJoueursHumains.isSelected() && i == 1) {
                texteJoueur = "Joueur 2 (HUMAIN - clavier ZQSD)";
            } else {
                texteJoueur = "Joueur " + (i + 1) + " (IA)";
            }

            Text texteIA = new Text(texteJoueur);
            texteIA.setFont(new Font(20));

            // si c'est joueur 2 et humain, pas besoin de boutons IA
            if (deuxJoueursHumains.isSelected() && i == 1) {
                selectionIAs.getChildren().add(texteIA);
                continue;
            }

            HBox ligne = new HBox(15);
            ligne.setAlignment(Pos.CENTER);

            // on crée les boutons pour chaque type d'IA disponible
            Button[] boutons = new Button[FabriquePartie.TYPES_IA_DISPONIBLES.size()];

            for (int j = 0; j < FabriquePartie.TYPES_IA_DISPONIBLES.size(); j++) {
                TypeStrategie type = FabriquePartie.TYPES_IA_DISPONIBLES.get(j);
                Button btn = new Button(type.nom());

                btn.setOnAction(e -> {
                    // l'index pour stocker la sélection (différent si joueur 2 humain)
                    int indexSelection = deuxJoueursHumains.isSelected() ? index - 2 : index - 1;

                    // ajouter des slots vides si besoin
                    while (selectionActuelle.size() <= indexSelection) {
                        selectionActuelle.add(null);
                    }
                    selectionActuelle.set(indexSelection, type);

                    // déselectionner les autres boutons et sélectionner celui-ci
                    for (Button b : boutons) {
                        b.setStyle("");
                    }
                    btn.setStyle("-fx-font-weight: bold;");
                });

                boutons[j] = btn;
                ligne.getChildren().add(btn);
            }

            tousLesBoutons.add(boutons);

            VBox iaBox = new VBox(10);
            iaBox.setAlignment(Pos.CENTER);
            iaBox.getChildren().addAll(texteIA, ligne);
            selectionIAs.getChildren().add(iaBox);
        }
    }

    /**
     * Lance le jeu via la fenêtre principale avec la configuration établie.
     * 
     * @param typesIA            La liste des stratégies sélectionnées pour les IA.
     * @param deuxJoueursHumains Indique si le mode 2 joueurs humains est activé.
     */
    private void lancerJeuAvecConfiguration(List<TypeStrategie> typesIA, boolean deuxJoueursHumains) {
        FenetrePrincipale fenetrePrincipale = new FenetrePrincipale();
        fenetrePrincipale.demarrerJeuAvecConfig(stage, typesIA, deuxJoueursHumains);
    }
}