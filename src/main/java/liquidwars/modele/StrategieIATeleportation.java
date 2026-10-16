package liquidwars.modele;

import java.util.List;
import java.util.Optional;

/**
 * Stratégie IA ultra agressive qui téléporte son curseur directement sur les ennemis.
 */
public class StrategieIATeleportation implements Strategie {
    private final Equipe equipePersonnel;
    private final List<Equipe> toutesLesEquipes;
    private final Grille grille;
    private Optional<Equipe> cibleActuelle = Optional.empty();

    /**
     * Crée une stratégie de téléportation pour l'équipe spécifiée.
     * 
     * @param equipe L'équipe contrôlée par cette IA.
     * @param equipes La liste de toutes les équipes.
     * @param grille La grille du jeu.
     */
    public StrategieIATeleportation(Equipe equipe, List<Equipe> equipes, Grille grille) {
        this.equipePersonnel = equipe;
        this.toutesLesEquipes = equipes;
        this.grille = grille;
    }

    /**
     * Téléporte le curseur directement sur la position de l'équipe cible.
     * Change de cible si l'actuelle est éliminée.
     * 
     * @return Les coordonnées de la cible ennemie.
     */
    @Override
        public Coordonnees determinerNouveauCurseur() {
            if (cibleActuelle.isEmpty() || cibleActuelle.get().estElimine()) {
                cibleActuelle = trouverNouvelleCible();
            }

            //on se tp sur cible valide
            if (cibleActuelle.isPresent()) {
                Coordonnees positionCible = cibleActuelle.get().getPositionCurseur();
                if (!grille.getCase(positionCible).estObstacle()) {
                    return positionCible;
                }
            }

            return equipePersonnel.getPositionCurseur();
        }

        /**
         * Trouve une nouvelle équipe ennemie à cibler.
         * 
         * @return La prochaine équipe à attaquer, ou empty si aucune disponible.
         */
        private Optional<Equipe> trouverNouvelleCible() {
            // On cherche la première équipe non éliminée qui n'est pas nous
            for (Equipe equipe : toutesLesEquipes) {
                if (!equipe.equals(equipePersonnel) && !equipe.estElimine()) {
                    return Optional.of(equipe);
                }
            }
            return Optional.empty(); // Aucune cible disponible
        }
}