package liquidwars.controleur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import liquidwars.modele.*;

/**
 * Gere la logique physique du jeu : deplacements des particules et resolutions
 * des conflits.
 * Cette classe s'occupe de faire avancer la simulation frame par frame en
 * utilisant
 * le multithreading pour les calculs des differentes equipes.
 */
public class ControleurJeu {

    private final Grille grille;
    private final Object lockGrille = new Object(); // le petit lock pour sécuriser la grille
    private final ExecutorService executorParticules; // s/o controleurPartie

    /**
     * Cree le controleur avec la grille de jeu et le gestionnaire de threads.
     * 
     * @param grille   La carte du jeu.
     * @param executor L'executeur pour paralleliser le traitement des equipes.
     */
    public ControleurJeu(Grille grille, ExecutorService executor) {
        this.grille = grille;
        this.executorParticules = executor;
    }

    /**
     * Met a jour toutes les particules pour ce tour de jeu.
     * 
     * Le traitement se fait en parallele pour chaque equipe :
     * 1. On determine d'abord l'action voulue par la particule (lecture du
     * gradient).
     * 2. On execute ensuite l'action de maniere synchronisee sur la grille.
     * 
     * Cette methode attend que toutes les equipes aient fini avant de rendre lamain.
     * 
     * @param equipes Liste des equipes en jeu.
     */
    public void deplacerToutesLesParticules(List<Equipe> equipes) {

        if (executorParticules.isShutdown()) {
            return; // on fait rien si l'executor est fermé
        }

        List<CompletableFuture<Void>> futures = equipes.stream()
                .map(equipe -> CompletableFuture.runAsync(() -> {
                    Gradient gradient = equipe.getGradient();
                    List<Particule> snapshot = new ArrayList<>(equipe.getParticules());

                    for (Particule p : snapshot) {
                        // on vérifie null au cas où
                        if (p == null)
                            continue;

                        Particule.ActionParticule action = p.determinerAction(grille, gradient);
                        if (action != null) {
                            synchronized (lockGrille) {
                                executerAction(p, action);
                            }
                        }
                    }
                }, executorParticules))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * Realise concretement l'action sur la grille.
     * Gere les 3 cas possibles : deplacement simple vers une case vide,
     * attaque sur un ennemi (avec conversion possible), ou transfert d'energie vers
     * un allié.
     *
     * @param p      La particule qui agit.
     * @param action L'action a effectuer contenant la direction et le type.
     */
    private void executerAction(Particule p, Particule.ActionParticule action) {
        try {
            Coordonnees nouvellePosition = action.direction().appliquer(p.getPosition());
            Case caseActuelle = grille.getCase(p.getPosition());
            Case nouvelleCase = grille.getCase(nouvellePosition);

            switch (action.type()) {
                case TypeAction.Deplacer(Direction dir) -> {
                    if (nouvelleCase.estLibre()) {
                        caseActuelle.retirerParticule();
                        nouvelleCase.ajouterParticule(p);
                        p.setPosition(nouvellePosition);
                    }
                }

                case TypeAction.Attaquer(Direction dir) -> {
                    if (nouvelleCase.contientParticule()) {
                        Particule cible = nouvelleCase.getParticule();
                        boolean cibleConvertie = p.attaquer(cible, grille);

                        if (cibleConvertie) {
                            cible.convertirVers(p.getEquipe());
                        }
                    }
                }

                case TypeAction.Transferer(Direction dir) -> {
                    if (nouvelleCase.contientParticule()) {
                        Particule ami = nouvelleCase.getParticule();
                        p.transfererEnergie(ami);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println(" erreur action ignorée pour la particule à " +
                    p.getPosition() + ": " + e.getMessage());
        }
    }
}