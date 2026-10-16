package liquidwars.modele;

/**
 * Classe abstraite implémentant le patron Décorateur pour les stratégies.
 * Elle permet d'envelopper une stratégie existante pour lui ajouter des fonctionnalités
 * dynamiquement sans modifier son code.
 */
public abstract class StrategieDecorateur implements Strategie {
    
    /** La stratégie décorée qui effectue le vrai travail de décision. */
    protected final Strategie strategieDecoree;

    /**
     * Crée un nouveau décorateur autour d'une stratégie existante.
     * * @param strategieDecoree La stratégie de base à laquelle on ajoute des fonctionnalités.
     */
    public StrategieDecorateur(Strategie strategieDecoree) {
        this.strategieDecoree = strategieDecoree;
    }

    /**
     * Délègue la décision du curseur à la stratégie décorée.
     * Les sous-classes peuvent surcharger cette méthode pour ajouter leur comportement.
     * * @return La nouvelle position décidée par la stratégie décorée.
     */
    @Override
    public Coordonnees determinerNouveauCurseur() {
        return strategieDecoree.determinerNouveauCurseur();
    }
}