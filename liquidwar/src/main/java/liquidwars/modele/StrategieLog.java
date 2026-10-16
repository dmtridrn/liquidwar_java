package liquidwars.modele;

/**
 * Décorateur concret qui espionne et affiche les décisions d'une stratégie.
 * Utile pour le débogage ou pour surveiller le comportement d'une IA en temps réel.
 */
public class StrategieLog extends StrategieDecorateur {
    
    private final String nomEquipe;

    /**
     * Crée un espion pour surveiller une équipe spécifique.
     * * @param strategieDecoree La stratégie à espionner.
     * @param nomEquipe Le nom de l'équipe (pour identifier les logs dans la console).
     */
    public StrategieLog(Strategie strategieDecoree, String nomEquipe) {
        super(strategieDecoree);
        this.nomEquipe = nomEquipe;
    }

    /**
     * Intercepte la décision de la stratégie pour l'afficher dans la console,
     * puis retourne le résultat normalement.
     * * @return La position choisie par la stratégie espionnée.
     */
    @Override
    public Coordonnees determinerNouveauCurseur() {
        //comme d'hab mais avec un print en plus
        Coordonnees cible = super.determinerNouveauCurseur();
        
        if (cible != null) {
            System.out.println("[ESPION] L'équipe " + nomEquipe + " vise " + cible);
        }
        
        return cible;
    }
}