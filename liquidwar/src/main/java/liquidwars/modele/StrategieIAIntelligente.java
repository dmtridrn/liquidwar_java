package liquidwars.modele;

import java.util.List;
import java.util.Optional;

/**
 * Stratégie IA agressive qui cible l'équipe ennemie la plus proche.
 */
public class StrategieIAIntelligente implements Strategie {
    private final Equipe equipePersonnel;
    private final List<Equipe> toutesLesEquipes;
    private final Grille grille;

    /**
     * Crée une stratégie intelligente pour l'équipe spécifiée.
     * 
     * @param perso L'équipe contrôlée par cette IA.
     * @param equipes La liste de toutes les équipes.
     * @param grille La grille du jeu.
     */
    public StrategieIAIntelligente(Equipe perso, List<Equipe> equipes, Grille grille){
        this.equipePersonnel = perso;
        this.toutesLesEquipes = equipes;
        this.grille = grille;
    }

    /**
     * Détermine une nouvelle position en ciblant l'équipe ennemie la plus proche.
     * 
     * @return Les coordonnées vers l'ennemi le plus proche.
     */
    @Override
    public Coordonnees determinerNouveauCurseur() {
        // on cherche l'equipe ennemi la plus proche
        Optional<Equipe> equipeEnnemiProche = Optional.empty(); //optional miam
        double distanceMin = Double.MAX_VALUE;

        for(Equipe equipe : toutesLesEquipes){
            if(equipe.equals((equipePersonnel))){
                continue; // on touche pas à notre team tu connais
            }

            Optional<Coordonnees> centreEquipe = calculerCentreEquipe(equipe);
            if (centreEquipe.isPresent()){
                double distance = calculerDistance(equipePersonnel.getPositionCurseur(), centreEquipe.get());

                if(distance < distanceMin){
                    distanceMin = distance;
                    equipeEnnemiProche = Optional.of(equipe);
                }
            }
        }

        // si on a trouvé une equipe à tabasser
        if(equipeEnnemiProche.isPresent()){
            Coordonnees centreEnnemi = calculerCentreEquipe(equipeEnnemiProche.get()).get();
            return dirigerVers(equipePersonnel.getPositionCurseur(), centreEnnemi);
        }
        

        // sinon on garde notre position actuelle
        return equipePersonnel.getPositionCurseur();    
    }

    /**
     * Calcule le centre de gravité d'une équipe basé sur ses particules.
     * 
     * @param equipe L'équipe dont on calcule le centre.
     * @return Les coordonnées du centre, ou empty si l'équipe est éliminée.
     */
    private Optional<Coordonnees> calculerCentreEquipe(Equipe equipe){
        List<Particule> particules = equipe.getParticules();

        if (particules.isEmpty()){
            return Optional.empty();
        }

        int sommeX = 0;
        int sommeY = 0;

        for(Particule p : particules){
            sommeX += p.getPosition().x();
            sommeY += p.getPosition().y();
        }

        int centreX = sommeX / particules.size();
        int centreY = sommeY / particules.size();


        // on depasse pas de la grille tu connais
        centreX = Math.max(0, Math.min(centreX, grille.getLargeur() - 1));
        centreY = Math.max(0, Math.min(centreY, grille.getHauteur() -1));

        return Optional.of(new Coordonnees(centreX, centreY));
        

    }

    /**
     * Calcule la distance euclidienne entre deux positions.
     * 
     * @param pos1 La première position.
     * @param pos2 La deuxième position.
     * @return La distance entre les deux positions.
     */
    private double calculerDistance(Coordonnees pos1, Coordonnees pos2){
        int diffX = pos1.x() - pos2.x();
        int diffY = pos1.y() - pos2.y();

        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
    
    /**
     * Dirige le curseur vers une cible en évitant les obstacles.
     * 
     * @param depart La position de départ.
     * @param cible La position cible.
     * @return Les coordonnées du prochain mouvement vers la cible.
     */
    private Coordonnees dirigerVers(Coordonnees depart, Coordonnees cible){
        int diffX = cible.x() - depart.x();
        int diffY = cible.y() - depart.y();

        // on se dirige tranquillement vers l'equipe
        int pas = 4;

        //optional pahlala
        Optional<Coordonnees> candidat = Optional.empty();
        int pasActuel = pas;

        while (candidat.isEmpty() && pasActuel <= 500){
            int nouvX = depart.x() + (diffX > 0 ? pasActuel : (diffX < 0 ? -pasActuel : 0));
            int nouvY = depart.y() + (diffY > 0 ? pasActuel : (diffY < 0 ? -pasActuel : 0));
        
            nouvX = Math.max(0, Math.min(nouvX, grille.getLargeur() - 1));
            nouvY = Math.max(0, Math.min(nouvY, grille.getHauteur() - 1));

            Coordonnees temp = new Coordonnees(nouvX, nouvY);

            if(!grille.getCase(temp).estObstacle()){
                candidat = Optional.of(temp);
            }
            pasActuel++;
        }
        //si case libre go sinn ca bouge ap
        return candidat.orElse(depart);
    }

    
}
