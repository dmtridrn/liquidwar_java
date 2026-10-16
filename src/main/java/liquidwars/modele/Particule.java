package liquidwars.modele;

import java.util.Random;

/**
 * Représente une particule appartenant à une équipe.
 * Gère son énergie, ses déplacements et ses interactions avec d'autres particules.
 */
public class Particule {
    private Coordonnees position;
    private Equipe equipe;
    private float energie;

    // Constantes fixes
    private static final float ENERGIE_MAX = 100f;
    private static final float ENERGIE_MIN = 0f;
    private static final float ENERGIE_TRANSFERT = 10f;

    private static final Random random = new Random();

    /**
     * Crée une particule avec une position, une équipe et une énergie initiale.
     * 
     * @param position La position de la particule.
     * @param equipe L'équipe à laquelle appartient la particule.
     * @param energie L'énergie initiale de la particule.
     */
    public Particule(Coordonnees position, Equipe equipe, float energie) {
        this.position = position;
        this.equipe = equipe;
        this.energie = energie;
    }

    // laa petite aide pour récupérer l'énergie d'attaque dynamiquement
    private float getEnergieAttaqueMax() {
        return 20f / FabriquePartie.getNbEquipes();
    }

    /**
     * Fait perdre de l'énergie à la particule.
     * 
     * @param montant La quantité d'énergie à perdre.
     */
    public void perdreEnergie(float montant) {
        this.energie = Math.max(ENERGIE_MIN, this.energie - montant);
    }

    /**
     * Fait gagner de l'énergie à la particule (limitée au maximum).
     * 
     * @param montant La quantité d'énergie à gagner.
     */
    public void gagnerEnergie(float montant) {
        this.energie = Math.min(ENERGIE_MAX, this.energie + montant);
    }

    /**
     * Vérifie si la particule est morte (énergie à zéro).
     * 
     * @return Vrai si la particule n'a plus d'énergie.
     */
    public boolean estMorte() {
        return this.energie == ENERGIE_MIN;
    }

    /**
     * Attaque une particule ennemie et récupère son énergie.
     * L'énergie excédentaire est redistribuée aux alliés.
     * 
     * @param cible La particule à attaquer.
     * @param grille La grille du jeu.
     * @return Vrai si la cible est morte suite à l'attaque.
     */
    public boolean attaquer(Particule cible, Grille grille) {
        if (cible.getEquipe() == this.equipe) {
            return false;
        }

        // Utilisation de la méthode dynamique au lieu de la constante statique
        float energieAttaque = getEnergieAttaqueMax();

        float energieDisponible = Math.min(energieAttaque, cible.getEnergie());
        float capaciteRestante = ENERGIE_MAX - this.getEnergie();
        float energieRecuperee = Math.min(energieDisponible, capaciteRestante);
        float energieExcedent = energieDisponible - energieRecuperee;

        float energieRedistribuee = 0f;
        if (energieExcedent > 0) {
            energieRedistribuee = redistribuerEnergieAuxAllies(energieExcedent, grille);
        }

        float energieTotalementPrelevee = energieRecuperee + energieRedistribuee;
        cible.perdreEnergie(energieTotalementPrelevee);
        this.gagnerEnergie(energieRecuperee);

        return cible.estMorte();
    }

    private float redistribuerEnergieAuxAllies(float energieADistribuer, Grille grille) {
        float energieDistribuee = 0f;

        for (Direction dir : Direction.values()) {
            Coordonnees voisin = dir.appliquer(this.position);

            if (!grille.estDansLesLimites(voisin))
                continue;

            Case caseVoisine = grille.getCase(voisin);
            if (caseVoisine.contientParticule()) {
                Particule allie = caseVoisine.getParticule();
                if (allie.getEquipe() == this.equipe && allie.getEnergie() < ENERGIE_MAX) {
                    float energieDonnee = Math.min(energieADistribuer - energieDistribuee,
                            ENERGIE_MAX - allie.getEnergie());
                    allie.gagnerEnergie(energieDonnee);
                    energieDistribuee += energieDonnee;

                    if (energieDistribuee >= energieADistribuer)
                        break;
                }
            }
        }

        return energieDistribuee;
    }

    /**
     * Transfère de l'énergie vers une particule alliée.
     * 
     * @param receveur La particule qui recevra l'énergie.
     */
    public void transfererEnergie(Particule receveur) {
        if (receveur.getEquipe() != this.equipe) {
            return;
        }

        if (this.energie > 15f &&
                receveur.energie < ENERGIE_MAX &&
                receveur.energie < this.energie) {

            float energieDonnee = Math.min(
                    ENERGIE_TRANSFERT,
                    Math.min(
                            this.energie - 15f,
                            ENERGIE_MAX - receveur.energie));

            this.perdreEnergie(energieDonnee);
            receveur.gagnerEnergie(energieDonnee);
        }
    }

    /**
     * Convertit la particule vers une nouvelle équipe.
     * 
     * @param nouvelleEquipe L'équipe vers laquelle convertir la particule.
     */
    public void convertirVers(Equipe nouvelleEquipe) {
        if (this.equipe == nouvelleEquipe) {
            return;
        }
        this.equipe.retirerParticule(this);
        this.equipe = nouvelleEquipe;
        nouvelleEquipe.ajouterParticule(this);
    }

    public Equipe getEquipe() {
        return this.equipe;
    }

    public float getEnergie() {
        return this.energie;
    }

    /**
     * Détermine la meilleure action à effectuer en fonction du gradient.
     * 
     * @param grille La grille du jeu.
     * @param gradient Le gradient vers l'objectif.
     * @return L'action à effectuer, ou null si aucune action possible.
     */
    public ActionParticule determinerAction(Grille grille, Gradient gradient) {
        int distanceCourante = gradient.getDistance(this.position);

        // les variables
        Direction directionPrincipale = null;
        Direction bonneDirection = null;
        Direction directionAcceptable = null;
        Direction directionAleatoire = null;
        Direction directionPrincipaleEnnemi = null;
        Direction bonneDirectionEnnemi = null;
        Direction directionPrincipaleAmi = null;
        int distanceMin = Integer.MAX_VALUE;

        // un seul parcours 
        for (Direction dir : Direction.values()) {
            Coordonnees voisin = dir.appliquer(this.position);

            if (!grille.estDansLesLimites(voisin)) {
                continue;
            }

            Case caseVoisine = grille.getCase(voisin);
            if (caseVoisine.estObstacle()) {
                continue;
            }

            int distanceVoisin = gradient.getDistance(voisin);

            if (caseVoisine.estLibre()) {
                // on traite les cases libres
                if (directionAleatoire == null && random.nextBoolean()) {
                    directionAleatoire = dir;
                }

                if (distanceVoisin < distanceMin) {
                    distanceMin = distanceVoisin;
                    directionPrincipale = dir;
                } else if (distanceVoisin < distanceCourante && bonneDirection == null) {
                    bonneDirection = dir;
                } else if (distanceVoisin == distanceCourante && directionAcceptable == null) {
                    directionAcceptable = dir;
                }

            } else if (caseVoisine.contientParticule()) {
                // on traite les cases occupées
                Particule autreParticule = caseVoisine.getParticule();
                boolean estEnnemi = autreParticule.getEquipe() != this.equipe;

                if (estEnnemi) {
                    if (distanceVoisin < distanceMin && distanceVoisin != Integer.MAX_VALUE) {
                        directionPrincipaleEnnemi = dir;
                    } else if (distanceVoisin < distanceCourante && bonneDirectionEnnemi == null
                            && distanceVoisin != Integer.MAX_VALUE) {
                        bonneDirectionEnnemi = dir;
                    }
                } else {
                    if (directionPrincipaleAmi == null) {
                        directionPrincipaleAmi = dir;
                    }
                }
            }
        }

        // on fait les retours des directions
        return choisirActionOptimale(
                directionPrincipale, bonneDirection, directionAcceptable,
                directionPrincipaleEnnemi, bonneDirectionEnnemi, directionPrincipaleAmi,
                directionAleatoire, grille);
    }


    private ActionParticule choisirActionOptimale(
            Direction principale, Direction bonne, Direction acceptable,
            Direction principaleEnnemi, Direction bonneEnnemi, Direction principaleAmi,
            Direction aleatoire, Grille grille) {

        if (principale != null) {
            Direction directionVariee = ajouterVariation(principale, grille);
            return new ActionParticule(directionVariee, new TypeAction.Deplacer(directionVariee));
        }

        if (bonne != null) {
            Direction directionVariee = ajouterVariation(bonne, grille);
            return new ActionParticule(directionVariee, new TypeAction.Deplacer(directionVariee));
        }

        if (acceptable != null) {
            return new ActionParticule(acceptable, new TypeAction.Deplacer(acceptable));
        }

        if (principaleEnnemi != null) {
            return new ActionParticule(principaleEnnemi, new TypeAction.Attaquer(principaleEnnemi));
        }

        if (bonneEnnemi != null) {
            return new ActionParticule(bonneEnnemi, new TypeAction.Attaquer(bonneEnnemi));
        }

        if (principaleAmi != null) {
            return new ActionParticule(principaleAmi, new TypeAction.Transferer(principaleAmi));
        }

        if (aleatoire != null && random.nextInt(10) < 5) {
            return new ActionParticule(aleatoire, new TypeAction.Deplacer(aleatoire));
        }

        return null;
    }

    private Direction ajouterVariation(Direction directionPrincipale, Grille grille) {
        if (random.nextInt(100) < 80) {
            return directionPrincipale;
        }

        Direction[] variantes = obtenirVariantes(directionPrincipale);

        for (Direction variante : variantes) {
            Coordonnees nouvellePos = variante.appliquer(this.position);

            if (nouvellePos.x() >= 0 && nouvellePos.x() < grille.getLargeur() &&
                    nouvellePos.y() >= 0 && nouvellePos.y() < grille.getHauteur()) {

                Case caseVariante = grille.getCase(nouvellePos);
                if (caseVariante.estLibre()) {
                    return variante;
                }
            }
        }

        return directionPrincipale;
    }

    private Direction[] obtenirVariantes(Direction dir) {
        return switch (dir) {
            case HAUT -> new Direction[] { Direction.HAUTGAUCHE, Direction.HAUTDROITE };
            case BAS -> new Direction[] { Direction.BASGAUCHE, Direction.BASDROITE };
            case GAUCHE -> new Direction[] { Direction.HAUTGAUCHE, Direction.BASGAUCHE };
            case DROITE -> new Direction[] { Direction.HAUTDROITE, Direction.BASDROITE };
            case HAUTDROITE -> new Direction[] { Direction.HAUT, Direction.DROITE };
            case HAUTGAUCHE -> new Direction[] { Direction.HAUT, Direction.GAUCHE };
            case BASDROITE -> new Direction[] { Direction.BAS, Direction.DROITE };
            case BASGAUCHE -> new Direction[] { Direction.BAS, Direction.GAUCHE };
        };
    }

    public void setEquipe(Equipe nouvelleEquipe) {
        this.equipe = nouvelleEquipe;
    }

    public Coordonnees getPosition() {
        return this.position;
    }

    public void setPosition(Coordonnees nouvellePosition) {
        this.position = nouvellePosition;
    }

    /**
     * Représente une action décidée par une particule avec sa direction et son type.
     */
    public record ActionParticule(Direction direction, TypeAction type) {
    }
}