package liquidwars.modele;

/**
 * Stratégie pour un joueur humain.
 * Le curseur suit la position de la souris ou du clavier.
 */
// la classe pour les joueurs classique pas les bots 
public class StrategieJoueur implements Strategie {
    private volatile Coordonnees positionSouris;

    /**
     * Retourne la dernière position enregistrée du curseur.
     * 
     * @return Les coordonnées du curseur.
     */
    @Override
    public Coordonnees determinerNouveauCurseur(){
        return positionSouris;
    }

    /**
     * Met à jour la position du curseur suite à une entrée du joueur.
     * 
     * @param pos La nouvelle position du curseur.
     */
    public void mettreAJourSouris(Coordonnees pos){
        this.positionSouris = pos;
    }
    
}
