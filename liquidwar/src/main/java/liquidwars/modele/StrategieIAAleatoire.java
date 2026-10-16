package liquidwars.modele;

import java.util.Optional;
import java.util.Random;

/**
 * Stratégie IA qui déplace le curseur de manière aléatoire.
 */
public class StrategieIAAleatoire implements Strategie{
    private final Equipe equipe;
    private final Grille grille;
    private final Random random;
    private static final int DEPLACEMENT_MAX = 50; // distance max on commence simple tu coco

    /**
     * Crée une stratégie aléatoire pour l'équipe spécifiée.
     * 
     * @param equipe L'équipe contrôlée par cette IA.
     * @param grille La grille du jeu.
     */
    public StrategieIAAleatoire(Equipe equipe, Grille grille) {
        this.equipe = equipe;
        this.grille = grille;
        this.random = new Random();
    }   

    /**
     * Détermine une nouvelle position aléatoire pour le curseur.
     * Évite les obstacles et reste dans les limites de la grille.
     * 
     * @return Les coordonnées de la nouvelle cible.
     */
    @Override
    public Coordonnees determinerNouveauCurseur(){
        Coordonnees positionActuelle = equipe.getPositionCurseur();
        Optional<Coordonnees> nouvelleCible = Optional.empty();
        int tentatives = 0;
        
        // on essaie de trouver une case libre
        while (nouvelleCible.isEmpty() && tentatives < 20) {
            int dx = random.nextInt(2 * DEPLACEMENT_MAX + 1) - DEPLACEMENT_MAX; 
            int dy = random.nextInt(2 * DEPLACEMENT_MAX + 1) - DEPLACEMENT_MAX;

            int nouvX = Math.max(0, Math.min(grille.getLargeur() - 1, positionActuelle.x() + dx));
            int nouvY = Math.max(0, Math.min(grille.getHauteur() - 1, positionActuelle.y() + dy));

            Coordonnees candidat = new Coordonnees(nouvX, nouvY);
            
            // on vérifie que ce n'est pas un obstacle
            if (!grille.getCase(candidat).estObstacle()) {
                nouvelleCible = Optional.of(candidat);
            }
            
            tentatives++;
        }
        
        return nouvelleCible.orElse(positionActuelle);
    }


}
