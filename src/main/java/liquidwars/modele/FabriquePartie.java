package liquidwars.modele;

import javafx.scene.paint.Color;
import liquidwars.controleur.ControleurPartie;
import liquidwars.modele.ConfigurationPartie.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Fabrique pour créer et initialiser une partie complète.
 * Configure les équipes, leurs positions et leurs stratégies.
 */
public class FabriquePartie {
    private static int nb_equipes;


    // types d'ia disponibles
    public static final List<TypeStrategie> TYPES_IA_DISPONIBLES = List.of(
        new StrategieAleatoireConfig(),
        new StrategieInteligenteConfig(),
        new StrategieFixeConfig(),
        new StrategieTeleportationConfig(60)
    );


    /**
     * Crée une partie avec la configuration spécifiée.
     * 
     * @param typesIA Les types d'IA pour les équipes non-joueurs.
     * @param deuxVraisJoueurs Vrai si deux joueurs humains participent.
     * @param largeur La largeur de la grille.
     * @param hauteur La hauteur de la grille.
     * @return Le contrôleur de la partie initialisée.
     */
    // nouveau créer partie (avec les config poto)
    public static ControleurPartie creerPartieAvecConfig(List<TypeStrategie> typesIA, boolean deuxVraisJoueurs, int largeur, int hauteur) {
        int nbVraisJoueurs = deuxVraisJoueurs ? 2 : 1;
        nb_equipes = typesIA.size() + nbVraisJoueurs ;// on oublie pas le vrai joueur

        Grille grille = GenerateurDeNiveau.generer(largeur, hauteur);
        List<Equipe> equipes = new ArrayList<>();
        List<Strategie> strategies = new ArrayList<>();
        
        // configuration des équipes avec coordonnées
        Color[] couleurs = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        
        int l = grille.getLargeur();
        int h = grille.getHauteur();
        int marge = 5; 
        
        int[][] positionsCurseur = {
            {marge, marge},             // coin haut gauche
            {l - marge - 1, marge},     // haut droit
            {marge, h - marge - 1},     //bas gauche
            {l - marge - 1, h - marge - 1}
        };

        // 1er étape : on crée toutes les équipes
        for (int i = 0 ; i< nb_equipes ; i++){
            Equipe equipe = new Equipe(couleurs[i], 
                new Coordonnees(positionsCurseur[i][0], positionsCurseur[i][1])
            );
            equipes.add(equipe);
            initialiserEquipe(grille, equipe, positionsCurseur[i][0], positionsCurseur[i][1]);
        }
        
        // 2ème étape : on rajoute les stratégies
        strategies.add(new StrategieJoueur()); // joueur 1
        if (deuxVraisJoueurs){
            strategies.add(new StrategieJoueur()); // joueur 2
        }

        int indiceEquipeIA = nbVraisJoueurs;
        for (int i = 0; i < typesIA.size(); i++) {
            Strategie strategieBase = typesIA.get(i).creer(equipes.get(indiceEquipeIA), grille, equipes);
            Strategie strategie = new StrategieLog(strategieBase, "IA_" + i);
            strategies.add(strategie);
            indiceEquipeIA ++;
        }
        
        return new ControleurPartie(grille, equipes, strategies, deuxVraisJoueurs);
    }

    /**
     * Place les particules initiales d'une équipe autour de sa position de départ.
     * Utilise un BFS pour trouver les cases libres les plus proches.
     * 
     * @param grille La grille du jeu.
     * @param equipe L'équipe à initialiser.
     * @param startX La coordonnée X de départ.
     * @param startY La coordonnée Y de départ.
     */
    private static void initialiserEquipe(Grille grille, Equipe equipe, int startX, int startY) {
        final int largeurZone = 25;
        final int hauteurZone = 25;
        final int TARGET = largeurZone * hauteurZone;
        int places = 0;


        // mtn on fait un bfs pour trouver la case libre la plus proche
        for (int dx = 0; dx < largeurZone && places < TARGET; dx++) {
            for (int dy = 0; dy < hauteurZone && places < TARGET; dy++) {
                Coordonnees origine = new Coordonnees(startX + dx, startY + dy);
                Coordonnees libre = trouverCaseLibreBFS(grille, origine);
                if (libre != null) {
                    Case c = grille.getCase(libre);
                    Particule p = new Particule(libre, equipe, 100f / nb_equipes);
                    c.ajouterParticule(p);
                    equipe.ajouterParticule(p);
                    places++;
                }
            }
        }

        if (places < TARGET) {
            throw new IllegalStateException("Impossible de placer toutes les particules : pas assez de cases libres.");
        }

        equipe.melangerParticules();
        equipe.recalculerGradient(grille);
    }

    /**
     * Cherche la case libre la plus proche d'une origine en utilisant un BFS.
     * 
     * @param grille La grille du jeu.
     * @param origine La position de départ de la recherche.
     * @return Les coordonnées de la case libre la plus proche, ou null si aucune trouvée.
     */
    private static Coordonnees trouverCaseLibreBFS(Grille grille, Coordonnees origine) {
        int largeur = grille.getLargeur();
        int hauteur = grille.getHauteur();
        boolean[][] visite = new boolean[largeur][hauteur];
        Queue<Coordonnees> file = new LinkedList<>();

        int sx = origine.x();
        int sy = origine.y();
        if (sx < 0 || sy < 0 || sx >= largeur || sy >= hauteur) {
            sx = Math.max(0, Math.min(sx, largeur - 1));
            sy = Math.max(0, Math.min(sy, hauteur - 1));
        }
        file.offer(new Coordonnees(sx, sy));

        while (!file.isEmpty()) {
            Coordonnees cour = file.poll();
            int x = cour.x();
            int y = cour.y();
            if (x < 0 || y < 0 || x >= largeur || y >= hauteur) continue;
            if (visite[x][y]) continue;
            visite[x][y] = true;

            Case c = grille.getCase(cour);
            if (!c.estObstacle() && !c.contientParticule()) {
                return cour;
            }

            for (Direction dir : Direction.values()) {
                Coordonnees voisin = dir.appliquer(cour);
                int vx = voisin.x();
                int vy = voisin.y();
                if (vx >= 0 && vy >= 0 && vx < largeur && vy < hauteur && !visite[vx][vy]) {
                    file.offer(voisin);
                }
            }
        }

        return null;
    }

    /**
     * Retourne le nombre d'équipes dans la partie actuelle.
     * 
     * @return Le nombre d'équipes.
     */
    public static int getNbEquipes(){
        return nb_equipes;
    }
}