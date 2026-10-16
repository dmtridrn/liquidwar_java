package liquidwars.modele;

/**
 * Représente une case de la grille.
 * Peut contenir une particule ou être un obstacle.
 */
public class Case {
    private final Coordonnees position;
    private boolean estObstacle = false;
    private Particule particule = null;
    
    /**
     * Crée une case à la position donnée.
     * 
     * @param position Les coordonnées de la case.
     */
    public Case(Coordonnees position){
        this.position = position ;
    }

    /**
     * Retourne la particule présente sur cette case.
     * 
     * @return La particule ou null si la case est vide.
     */
    public Particule getParticule(){
        return this.particule;
    }

    /**
     * Vérifie si la case est un obstacle.
     * 
     * @return Vrai si la case est un obstacle.
     */
    public boolean estObstacle(){
        return this.estObstacle;
    }

    /**
     * Vérifie si la case est libre (pas d'obstacle ni de particule).
     * 
     * @return Vrai si la case est libre.
     */
    public boolean estLibre(){
        return !this.estObstacle && this.particule == null ;
    }

    /**
     * Place une particule sur cette case si elle n'est pas un obstacle.
     * 
     * @param p La particule à ajouter.
     */
    public void ajouterParticule(Particule p){
        if (!this.estObstacle && p != null){
            this.particule = p;
        } 
    }

    /**
     * Retire la particule présente sur cette case.
     */
    public void retirerParticule(){
        this.particule = null;
    }

    /**
     * Vérifie si la case contient une particule.
     * 
     * @return Vrai si une particule est présente.
     */
    public boolean contientParticule(){
        return this.particule != null;
    }

    /**
     * Retourne les coordonnées de cette case.
     * 
     * @return La position de la case.
     */
    public Coordonnees getPosition(){
        return this.position ;
    }

    /**
     * Marque cette case comme un obstacle.
     */
    public void marquerObstacle() {
       this.estObstacle = true;
    }
}