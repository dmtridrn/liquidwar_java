package liquidwars.modele;

public interface Strategie {
    /**
     * ptite fonction pour la nouvelle position du curseur pour cette frame
     * @return la nouvelle position, ou null pour ne pas bouger tun connais
     */
    Coordonnees determinerNouveauCurseur();
}   
