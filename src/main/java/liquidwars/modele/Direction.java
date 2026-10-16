package liquidwars.modele;

/**
 * Les 8 directions possibles pour le déplacement d'une particule.
 */
public enum Direction {
    HAUT(0, -1),
    BAS(0, 1),
    GAUCHE(-1, 0),
    DROITE(1, 0),
    HAUTDROITE(1, -1), 
    HAUTGAUCHE(-1, -1), 
    BASDROITE(1, 1), 
    BASGAUCHE(-1, 1);
    
    private final int dx;
    private final int dy;
    
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    /**
     * Applique le déplacement de cette direction à des coordonnées.
     * 
     * @param coord Les coordonnées de départ.
     * @return Les nouvelles coordonnées après déplacement.
     */
    public Coordonnees appliquer(Coordonnees coord) {
        return new Coordonnees(coord.x() + dx, coord.y() + dy);
    }
}//enum merci le cours de M.Degorre