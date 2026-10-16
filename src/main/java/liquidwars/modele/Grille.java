package liquidwars.modele;

/**
 * Représente la grille de jeu contenant toutes les cases.
 */
public class Grille {
    private final Case[][] grille;
    private final int largeur;
    private final int hauteur;

    /**
     * Crée une grille avec les dimensions spécifiées.
     * 
     * @param largeur La largeur de la grille.
     * @param hauteur La hauteur de la grille.
     */
    public Grille(int largeur, int hauteur){
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.grille = new Case[largeur][hauteur];
        for(int x=0; x< largeur; x++){
            for(int y =0; y< hauteur; y++){
             
                this.grille[x][y] = new Case(new Coordonnees(x, y));
            }
        }
    }

    /**
     * Retourne la largeur de la grille.
     * 
     * @return La largeur.
     */
    public int getLargeur(){
        return this.largeur;
    }

    /**
     * Retourne la hauteur de la grille.
     * 
     * @return La hauteur.
     */
    public int getHauteur(){
        return this.hauteur;
    }

    /**
     * Vérifie si des coordonnées sont dans les limites de la grille.
     * 
     * @param coord Les coordonnées à vérifier.
     * @return Vrai si les coordonnées sont valides.
     */
    public boolean estDansLesLimites(Coordonnees coord) {
        return coord.x() >= 0 && coord.x() < largeur &&
               coord.y() >= 0 && coord.y() < hauteur;
    }

    /**
     * Retourne la case aux coordonnées spécifiées.
     * 
     * @param coordonnees Les coordonnées de la case.
     * @return La case demandée.
     * @throws IllegalArgumentException Si les coordonnées sont hors limites.
     */
    public Case getCase(Coordonnees coordonnees) {
        if (!estDansLesLimites(coordonnees)) {
            throw new IllegalArgumentException(
                "Coordonnées invalides: (" + coordonnees.x() + ", " + coordonnees.y() + "). " +
                "Limites: [0-" + (largeur-1) + ", 0-" + (hauteur-1) + "]"
            );
        }
        return grille[coordonnees.x()][coordonnees.y()];
    }
}
