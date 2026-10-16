package liquidwars.modele;

/**
 * Stratégie IA qui garde le curseur à une position fixe.
 */
public class StrategieIAFixe implements Strategie{

    private Coordonnees cible;

    /**
     * Crée une stratégie fixe avec une position définie.
     * 
     * @param cible La position fixe du curseur.
     */
    public StrategieIAFixe(Coordonnees cible) {
        this.cible = cible;
    }

    /**
     * Retourne toujours la même position fixe.
     * 
     * @return Les coordonnées de la cible fixe.
     */
    @Override
    public Coordonnees determinerNouveauCurseur() {
        return cible;
    }

    /**
     * Modifie la position fixe du curseur.
     * 
     * @param nouvelleCible La nouvelle position fixe.
     */
    public void setCible(Coordonnees nouvelleCible) {
        this.cible = nouvelleCible;
    }
    
}
