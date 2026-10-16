package liquidwars.modele;

import java.util.List;

/**
 * Configuration des équipes pour une partie.
 * Permet de définir des joueurs humains ou des IA avec différentes stratégies.
 */
public sealed interface ConfigurationPartie {

    /**
     * Configuration pour un joueur humain.
     */
    record ConfigJoueur() implements ConfigurationPartie {
        public Strategie creer() {
            return new StrategieJoueur();
        }

        public String nom() {
            return "JOUEUR";
        }
    }

    /**
     * Configuration pour une équipe contrôlée par une IA.
     */
    record ConfigIA(TypeStrategie typeStrategie) implements ConfigurationPartie {
        public Strategie creer(Equipe equipe, Grille grille, List<Equipe> equipes) {
            return typeStrategie.creer(equipe, grille, equipes);
        }

        public String nom() {
            return "IA - " + typeStrategie.nom();
        }
    }

    /**
     * Types de stratégies IA disponibles.
     */
    sealed interface TypeStrategie permits StrategieAleatoireConfig, StrategieInteligenteConfig, StrategieTeleportationConfig, StrategieFixeConfig {
        Strategie creer(Equipe equipe, Grille grille, List<Equipe> equipes);

        String nom();
    }

    /**
     * IA qui déplace son curseur de manière aléatoire.
     */
    record StrategieAleatoireConfig() implements TypeStrategie {
        @Override
        public Strategie creer(Equipe equipe, Grille grille, List<Equipe> equipes) {
            return new StrategieIAAleatoire(equipe, grille);
        }

        @Override
        public String nom() {
            return "Aléatoire";
        }
    }

    /**
     * IA agressive qui cible les ennemis.
     */
    record StrategieInteligenteConfig() implements TypeStrategie {
        @Override
        public Strategie creer(Equipe equipe, Grille grille, List<Equipe> equipes) {
            return new StrategieIAIntelligente(equipe, equipes, grille);
        }

        @Override
        public String nom() {
            return "Aggressive";
        }
    }

    /**
     * IA qui garde son curseur fixe.
     */
    record StrategieFixeConfig() implements TypeStrategie {
        @Override
        public Strategie creer(Equipe equipe, Grille grille, List<Equipe> equipes) {
            return new StrategieIAFixe(equipe.getPositionCurseur());
        }

        @Override
        public String nom() {
            return "Fixe";
        }
    }

    /**
     * IA ultra agressive qui téléporte son curseur sur les ennemis.
     */
    record StrategieTeleportationConfig(int intervalFrames) implements TypeStrategie {
        @Override
        public Strategie creer(Equipe equipe, Grille grille, List<Equipe> equipes) {
            return new StrategieIATeleportation(equipe, equipes, grille);
        }

        @Override
        public String nom() {
            return "Ultra Aggressive";
        }
    }
}