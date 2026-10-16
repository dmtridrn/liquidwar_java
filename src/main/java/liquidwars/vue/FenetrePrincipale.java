package liquidwars.vue;

import java.util.List;
import javafx.geometry.Rectangle2D;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.*;
import liquidwars.controleur.ControleurPartie;
import liquidwars.modele.*;
import liquidwars.modele.ConfigurationPartie.TypeStrategie;

/**
 * Classe principale de l'application gérant la fenêtre de jeu et la boucle
 * temporelle.
 *
 * Elle orchestre l'affichage des différentes scènes (Menu, Jeu, Fin) et assure
 * la mise à jour constante du jeu via un AnimationTimer.
 */
public class FenetrePrincipale extends Application {

    private ControleurPartie controleur;
    private VueGrille vueGrille;
    private VueJauges vueJauges;

    // attributs pour le menu pause
    private MenuPause menuPause;
    private boolean jeuEnPause = false;
    private AnimationTimer boucleJeu;

    /**
     * Point d'entrée de l'application JavaFX.
     * Affiche le menu principal au lancement.
     * 
     * @param stage La fenêtre principale.
     */
    @Override
    public void start(Stage stage) {
        MenuPrincipal menu = new MenuPrincipal(stage);
        menu.afficher();

        stage.setTitle("Liquid Wars");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Initialise et lance une partie en fonction de la configuration choisie.
     *
     * Cette méthode configure la taille de la grille selon la résolution de
     * l'écran,
     * initialise le contrôleur et les vues, et met en place la gestion des entrées
     * utilisateur.
     * 
     * @param stage            La fenêtre principale.
     * @param typesIA          La liste des stratégies pour les IA.
     * @param deuxVraisJoueurs Vrai si deux humains jouent, Faux sinon.
     */
    public void demarrerJeuAvecConfig(Stage stage, List<TypeStrategie> typesIA, boolean deuxVraisJoueurs) {
        // on récupére la taille de l'écran de l'utilisateur
        Rectangle2D ecran = Screen.getPrimary().getVisualBounds();
        double largeurEcran = ecran.getWidth();
        double hauteurEcran = ecran.getHeight();

        // on configure la fenetre pour qu'elle prenne tout l'espace
        stage.setX(ecran.getMinX());
        stage.setY(ecran.getMinY());
        stage.setWidth(largeurEcran);
        stage.setHeight(hauteurEcran);
        stage.setMaximized(true); // plein ecran fenetré

        /// on calcule la taille dispo en elevant la barre des jauges
        double largeurDispo = largeurEcran - 220;
        double hauteurDispo = hauteurEcran - 40;

        int resolutionGrain = 6;
        int largeurGrille = (int) (largeurDispo / resolutionGrain);
        int hauteurGrille = (int) (hauteurDispo / resolutionGrain);

        
        this.controleur = FabriquePartie.creerPartieAvecConfig(typesIA, deuxVraisJoueurs, largeurGrille, hauteurGrille);
        vueGrille = new VueGrille(controleur.getGrille(), resolutionGrain);

        vueGrille.afficherGradient(controleur.getEquipeJoueur());
        vueGrille.dessiner(); // Pon dessine our éviter l'écran blanc
        vueGrille.setNbEquipes(FabriquePartie.getNbEquipes());
        vueGrille.setDeuxVraisJoueurs(deuxVraisJoueurs);

        vueJauges = new VueJauges(controleur.getEquipes());

        BorderPane conteneurJeu = new BorderPane();
        conteneurJeu.setCenter(vueGrille); // grille au centre
        conteneurJeu.setRight(vueJauges); // stats à droite

        // on crée le menu pause avec des lambdas expressions
        menuPause = new MenuPause(
                () -> basculerPause(), // 1 : reprendre le jeu

                () -> { // 2 : retour au menu principal
                    if (boucleJeu != null)
                        boucleJeu.stop();
                    if (controleur != null)
                        controleur.arreter();
                    MenuPrincipal menu = new MenuPrincipal(stage);
                    menu.afficher();
                },
                () -> { // 3 : quitter le jeu
                    if (controleur != null)
                        controleur.arreter();
                    stage.close();
                });

        // on utilse un StackPane pour que le menu pause soit par-dessus
        StackPane racine = new StackPane();
        racine.getChildren().addAll(conteneurJeu, menuPause);

        // On crée la scène avec le emnu et la taille de l'écran
        Scene scene = new Scene(racine, largeurEcran, hauteurEcran);

        // gestion de la touche echap et option deux joueurs
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                basculerPause();
            } else if (!jeuEnPause && deuxVraisJoueurs) {
                vueGrille.gererAppuiTouche(e);
            }
        });

        // on relache les touches ssi on est pas en pause
        scene.setOnKeyReleased(e -> {
            if (!jeuEnPause && deuxVraisJoueurs) {
                vueGrille.gererRelacheTouche(e);
            }
        });

        stage.setScene(scene);
        vueGrille.requestFocus();

        stage.setOnCloseRequest(e -> {
            if (controleur != null)
                controleur.arreter();
            System.exit(0); // On s'assure que tout se ferme bien
        });

        demarrerBoucleDeJeu(deuxVraisJoueurs);
    }

    /**
     * Lance la boucle principale du jeu (AnimationTimer).
     *
     * Cette boucle met à jour le contrôleur et redessine la vue à intervalle
     * régulier tout en gérant les entrées clavier/souris.
     * 
     * @param deuxVraisJoueurs Indique si un deuxième joueur humain est présent.
     */
    private void demarrerBoucleDeJeu(boolean deuxVraisJoueurs) {
        boucleJeu = new AnimationTimer() {
            // récupère la stratégie du joueur pour mettre à jour la souris
            StrategieJoueur strategieJoueur1 = (StrategieJoueur) controleur.getStrategieJoueur();
            StrategieJoueur strategieJoueur2 = deuxVraisJoueurs
                    ? (StrategieJoueur) controleur.getStrategieJoueur(1)
                    : null;

            private long dernierTemps = 0;

            @Override
            public void handle(long maintenant) {
                if (jeuEnPause)
                    return; // fige le jeu sans casser le thread

                // on vérifie si la partie est terminée
                if (controleur.estTerminee()) {
                    this.stop(); // arrêter la boucle
                    afficherEcranFin();
                    return;
                }

                if (maintenant - dernierTemps >= 16_666_666) { // 20ms = 50 fps

                    // 1. Transmettre l'input du joueur au contrôleur
                    vueGrille.gererMouvementClavier();

                    // Joueur 1 (souris)
                    Coordonnees posSouris = vueGrille.getPositionSouris();
                    if (posSouris != null && strategieJoueur1 != null) {
                        strategieJoueur1.mettreAJourSouris(posSouris);
                    }

                    // Joueur 2 (clavier)
                    if (strategieJoueur2 != null) {
                        Coordonnees posClavier = vueGrille.getPositionClavier();
                        if (posClavier != null) {
                            strategieJoueur2.mettreAJourSouris(posClavier);
                        }
                    }

                    // 2. Le contrôleur met à jour le jeu
                    controleur.updateFrame();

                    // 3. La vue affiche le nouvel état
                    vueGrille.dessiner();
                    vueJauges.mettreAJour();

                    dernierTemps = maintenant;
                }
            }
        };
        boucleJeu.start();
    }

    /**
     * Met le jeu en pause ou le reprend.
     * Affiche ou masque le menu de pause.
     */
    private void basculerPause() {
        jeuEnPause = !jeuEnPause;
        menuPause.basculerVisibilite();

        if (controleur != null) {
            controleur.setPause(jeuEnPause);
        }

        if (!jeuEnPause) {
            vueGrille.requestFocus();
        }
    }

    /**
     * Change la scène pour afficher l'écran de fin de partie.
     * Récupère les données finales (gagnant, classement) auprès du contrôleur.
     */
    private void afficherEcranFin() {
        Equipe gagnant = controleur.getGagnant();
        List<Equipe> classement = controleur.getClassement();
        long tempsDebut = controleur.getTempsDebut();

        Stage stage = (Stage) vueGrille.getScene().getWindow();
        EcranFinPartie ecranFin = new EcranFinPartie(stage, gagnant, classement, tempsDebut);
        ecranFin.afficher();
    }

}