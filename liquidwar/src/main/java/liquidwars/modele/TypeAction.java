package liquidwars.modele;

/**
 * Représente les différents types d'actions qu'une particule peut effectuer.
 */
public sealed interface TypeAction permits TypeAction.Deplacer, TypeAction.Attaquer, TypeAction.Transferer{
  
    /**
     * Action de déplacement vers une case vide.
     */
    record Deplacer(Direction direction) implements TypeAction {}

    /**
     * Action d'attaque contre une particule ennemie.
     */
    record Attaquer(Direction direction) implements TypeAction {}

    /**
     * Action de transfert d'énergie vers une particule alliée.
     */
    record Transferer(Direction direction) implements TypeAction {}


}

