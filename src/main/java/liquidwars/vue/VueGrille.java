package liquidwars.vue;

import liquidwars.modele.*;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.canvas.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Gère l'affichage graphique de la grille de jeu.
 * Affiche les particules, les obstacles et gère les entrées des joueurs (souris et clavier).
 */
public class VueGrille extends Canvas{
    private final double tailleCase;
    private Grille grille;
    private Equipe equipeAffichee;//temporaire pour test tqt bb
    private Coordonnees positionSouris;
    private Coordonnees positionClavier;
    private boolean deuxVraisJoueurs = false;
    private int nbEquipes = 1;
    // pour tracker les touches enfoncées en même temps bg
    private Set<KeyCode> touchesAppuyees = new HashSet<>();
    
    /**
     * Initialise la vue de la grille avec ses dimensions.
     * Configure les gestionnaires d'événements pour la souris et le focus.
     * 
     * @param grille La grille du jeu à afficher.
     * @param tailleCase La taille en pixels de chaque case.
     */
    public VueGrille (Grille grille, double tailleCase){
        this.grille = grille;
        this.tailleCase = tailleCase;

        setWidth(grille.getLargeur() * tailleCase);
        setHeight(grille.getHauteur() * tailleCase);

        this.equipeAffichee = null;
        this.positionClavier = new Coordonnees(100, 10); // Pos j2 (à changer plus tard)
        this.setOnMouseMoved(this:: gererMouvementSouris);
        this.setFocusTraversable(true);
        this.setOnMouseClicked(e -> this.requestFocus());
    }

    /**
     * Définit la grille à afficher.
     * 
     * @param grille La nouvelle grille à afficher.
     */
    public void setGrille(Grille grille){
        this.grille = grille;
    }

    /**
     * Définit l'équipe dont on affiche le gradient.
     * 
     * @param equipe L'équipe à afficher.
     */
    public void afficherGradient(Equipe equipe) {
        this.equipeAffichee = equipe;
    }

    /**
     * Définit le nombre total d'équipes pour le calcul de l'énergie.
     * 
     * @param nb Le nombre d'équipes dans la partie.
     */
    public void setNbEquipes(int nb){
        this.nbEquipes = nb;
    }

    /**
     * Dessine l'intégralité de la grille (cases, particules et obstacles).
     */
    public void dessiner(){
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(0, 0, getWidth(), getHeight());

        for(int x = 0; x< grille.getLargeur(); x++){
            for(int y = 0; y < grille.getHauteur(); y++){
                Case c = grille.getCase(new Coordonnees(x, y));
                dessinerCase(gc, c);
            }
        }
    }
    
    /**
     * Dessine une case individuelle en fonction de son contenu.
     * Adapte la couleur des particules selon leur énergie.
     * 
     * @param gc Le contexte graphique pour dessiner.
     * @param c La case à dessiner.
     */
    public void dessinerCase(GraphicsContext gc, Case c) {
        double posX = c.getPosition().x() * tailleCase;
        double posY = c.getPosition().y() * tailleCase;

        double tailleReelle = tailleCase + 0.5;

        if (c.estObstacle()) {
            gc.setFill(Color.GRAY);
            gc.fillRect(posX, posY, tailleReelle, tailleReelle);
        } else if (c.contientParticule()) {
            Particule p = c.getParticule();
            Color couleurEquipe = p.getEquipe().getCouleur();

            // plus foncé si elle perd de l'énergie
            Color couleurFinale = interpolerCouleur(couleurEquipe, p.getEnergie());
           
            gc.setFill(couleurFinale);
            gc.fillRect(posX, posY, tailleReelle, tailleReelle);
        }
    }

    /**
     * Calcule la couleur d'une particule en fonction de son énergie.
     * Plus l'énergie est faible, plus la couleur est sombre.
     * 
     * @param couleurEquipe La couleur de base de l'équipe.
     * @param energie L'énergie actuelle de la particule.
     * @return La couleur ajustée selon l'énergie.
     */
    private Color interpolerCouleur(Color couleurEquipe, float energie) {
        float energieMax = 100f / nbEquipes;
        double ratio = Math.max(0, Math.min(1, energie / energieMax));

        // on réduit la luminosité avec un  minimum de 0.4 pour avoir avec du noir complet
        double facteurLuminosite = 0.4 + (0.6 * ratio);

        // fonction direct dans javafx
        return couleurEquipe.deriveColor(0, 1.0, facteurLuminosite, 1.0);
    }

    /**
     * Gère le mouvement de la souris sur la grille.
     * Met à jour la position cible du joueur 1.
     * 
     * @param event L'événement de mouvement de la souris.
     */
    private void gererMouvementSouris(MouseEvent event){
        if (equipeAffichee != null){
            int caseX = (int) (event.getX() / tailleCase);
            int caseY = (int) (event.getY() / tailleCase);

            if(caseX >= 0 && caseX < grille.getLargeur() &&
                caseY >= 0 && caseY < grille.getHauteur() ){ 
                
                Coordonnees nouvellePos = new Coordonnees(caseX, caseY);
                
                if (!grille.getCase(nouvellePos).estObstacle()) {
                    positionSouris = nouvellePos;
                }
            }
        }
    }

    /**
     * Enregistre l'appui d'une touche du clavier pour le joueur 2.
     * 
     * @param event L'événement d'appui de touche.
     */
    //tracker quand on appuie sur une touche
    public void gererAppuiTouche(KeyEvent event) {
        if (!deuxVraisJoueurs)
            return;
        touchesAppuyees.add(event.getCode());
        event.consume();
    }

    /**
     * Enregistre le relâchement d'une touche du clavier pour le joueur 2.
     * 
     * @param event L'événement de relâchement de touche.
     */
    // tracker quand on relâche une touche
    public void gererRelacheTouche(KeyEvent event) {
        if (!deuxVraisJoueurs)
            return;
        touchesAppuyees.remove(event.getCode());
        event.consume();
    }

    /**
     * Traite les déplacements du joueur 2 via les touches ZQSD.
     * Gère les mouvements diagonaux et vérifie les obstacles.
     */
    // pour le j2, il joue clavier le poto
    public void gererMouvementClavier(){
        if (!deuxVraisJoueurs) return;

        int nouvX = positionClavier.x();
        int nouvY = positionClavier.y();

        // on verifie toutes les touches en même temps
        if (touchesAppuyees.contains(KeyCode.Z)) {
            nouvY -= 1;
        }
        if (touchesAppuyees.contains(KeyCode.S)) {
            nouvY += 1;
        }
        if (touchesAppuyees.contains(KeyCode.Q)) {
            nouvX -= 1;
        }
        if (touchesAppuyees.contains(KeyCode.D)) {
            nouvX += 1;
        }

        //on vérifie les limites
        nouvX = Math.max(0, Math.min(nouvX, grille.getLargeur() - 1));
        nouvY = Math.max(0, Math.min(nouvY, grille.getHauteur() - 1));

        Coordonnees nouvellePos = new Coordonnees(nouvX, nouvY);

        if (!grille.getCase(nouvellePos).estObstacle()){
            positionClavier = nouvellePos;
        }

    }

    /**
     * Active ou désactive le mode deux joueurs humains.
     * 
     * @param valeur Vrai si deux joueurs humains sont présents.
     */
    public void setDeuxVraisJoueurs(boolean valeur){
        this.deuxVraisJoueurs = valeur;
    }

    /**
     * Récupère la position actuelle de la souris (joueur 1).
     * 
     * @return Les coordonnées de la souris sur la grille.
     */
    public Coordonnees getPositionSouris() {
        return positionSouris;
    }

    /**
     * Récupère la position actuelle du curseur clavier (joueur 2).
     * 
     * @return Les coordonnées du curseur clavier sur la grille.
     */
    public Coordonnees getPositionClavier() {
        return positionClavier;
    }
}