package liquidwars.modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.paint.Color;

/**
 * Représente une équipe dans le jeu.
 * Gère sa couleur, son curseur, ses particules et son gradient.
 */
public class Equipe {
    private final Color couleur;
    private Coordonnees position_curseur;
    private List<Particule> particules; 
    private Gradient gradient;
    private long tempsElimination = -1; // -1 -> pas encore éliminé

    /**
     * Crée une équipe avec sa couleur et la position initiale de son curseur.
     * 
     * @param couleur La couleur de l'équipe.
     * @param position_curseur La position initiale du curseur.
     */
    public Equipe(Color couleur, Coordonnees position_curseur){
        this.couleur = couleur;
        this.position_curseur = position_curseur;
        this.particules = new ArrayList<>();
    }

    /**
     * Recalcule le gradient vers le curseur de l'équipe.
     * 
     * @param grille La grille du jeu.
     */
    public void recalculerGradient(Grille grille) {
        if (this.gradient == null) {
            this.gradient = new Gradient(grille, this.position_curseur);
        } else {
            this.gradient.recalculer(this.position_curseur);
        }
    }

    /**
     * Déplace le curseur de l'équipe et recalcule le gradient.
     * 
     * @param nouvellePosition La nouvelle position du curseur.
     * @param grille La grille du jeu.
     */
    public synchronized void deplacerCurseur(Coordonnees nouvellePosition, Grille grille) {
        if (nouvellePosition == null) {
            throw new IllegalArgumentException("La nouvelle position ne peut pas être null");
        }
        if (grille == null) {
            throw new IllegalArgumentException("La grille ne peut pas être null");
        }

        if (nouvellePosition == null || nouvellePosition.equals(this.position_curseur)) {
            return; // Pas de changement -> on recalcule pas
        }

        this.position_curseur = nouvellePosition;
        recalculerGradient(grille);
    }

    /**
     * Retourne la distance du gradient à une position donnée.
     * 
     * @param pos Les coordonnées à vérifier.
     * @return La distance vers le curseur.
     */
    public int getDistanceGradient(Coordonnees pos) {
        if (gradient == null) {
            return Integer.MAX_VALUE;
        }
        return gradient.getDistance(pos);
    }

    /**
     * Ajoute une particule à l'équipe.
     * 
     * @param p La particule à ajouter.
     */
    public synchronized void ajouterParticule(Particule p){
        if (p == null) {
            throw new IllegalArgumentException("La particule ne peut pas être null");
        }
        particules.add(p);
    }

    /**
     * Mélange aléatoirement l'ordre des particules de l'équipe.
     */
    public synchronized void melangerParticules() {
        Collections.shuffle(this.particules);
    }

    /**
     * Retire une particule de l'équipe.
     * Sauvegarde le temps d'élimination si l'équipe n'a plus de particules.
     * 
     * @param p La particule à retirer.
     */
    public synchronized void retirerParticule(Particule p){
        particules.remove(p);

        // si l'équipe vient de mourir, on sauvegarde le temps
        if (particules.isEmpty() && tempsElimination == -1) {
            tempsElimination = System.currentTimeMillis();
        }
        
    }

    /**
     * Retourne le moment où l'équipe a été éliminée.
     * 
     * @return Le timestamp d'élimination ou -1 si non éliminée.
     */
    public long getTempsElimination() {
        return tempsElimination;
    }

    /**
     * Retourne le nombre de particules de l'équipe.
     * 
     * @return Le nombre de particules.
     */
    public synchronized int getNombreParticules() {
        return particules.size();
    }

    /**
     * Retourne la liste des particules de l'équipe.
     * 
     * @return Une liste non modifiable des particules.
     */
    public synchronized List<Particule> getParticules(){
        return Collections.unmodifiableList(this.particules);
    }
    
    /**
     * Calcule l'énergie totale de toutes les particules de l'équipe.
     * 
     * @return L'énergie totale.
     */
    public synchronized float getEnergieTotale(){
        return (float) particules.stream().mapToDouble(p -> p.getEnergie()).sum();
    }

    /**
     * Retourne la couleur de l'équipe.
     * 
     * @return La couleur de l'équipe.
     */
    public Color getCouleur() {
        return couleur ;
    }

    /**
     * Retourne le gradient de l'équipe.
     * 
     * @return Le gradient vers le curseur.
     */
    public Gradient getGradient(){
        return this.gradient;
    }

    /**
     * Retourne la position actuelle du curseur de l'équipe.
     * 
     * @return Les coordonnées du curseur.
     */
    public Coordonnees getPositionCurseur() {
        return position_curseur;
    }

    /**
     * Vérifie si l'équipe est éliminée (plus de particules).
     * 
     * @return Vrai si l'équipe n'a plus de particules.
     */
    public boolean estElimine(){
        if(this.particules.isEmpty()){
            System.out.println("éliminé");
        }
        return this.particules.isEmpty();
    }
}