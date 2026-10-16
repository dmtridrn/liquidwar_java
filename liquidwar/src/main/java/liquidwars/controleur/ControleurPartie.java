package liquidwars.controleur;

import liquidwars.modele.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Gere le deroulement global d'une partie.
 * S'occupe de la gestion du temps (boucle de jeu), de la gestion des
 * joueurs (humains et IAs) et de verification des conditions de victoire.
 */
public class ControleurPartie {
    private final Grille grille;
    private final List<Equipe> equipes;
    private final List<Strategie> strategies;
    private final ControleurJeu controleurJeu;
    private int frameCounter;
    private static final int FRAMES_IA = 30;
    private final ExecutorService executorPartage;
    private final boolean deuxVraisJoueurs;
    private boolean partieTerminee = false;
    private long tempsDebut;
    private long momentPause;

    /**
     * Creation d'une nouvelle partie.
     * Le controleur initialise ici son propre ExecutorService base sur des threads
     * virtuels
     * pour gerer la concurrence entre les IAs et la physique du jeu via
     * ControleurJeu.
     *
     * @param grille           La carte du jeu.
     * @param equipes          Liste des equipes participantes.
     * @param strategies       Strategies associees a chaque equipe (dans le meme
     *                         ordre).
     * @param deuxVraisJoueurs Indique si le deuxieme joueur est humain ou IA.
     */
    public ControleurPartie(Grille grille, List<Equipe> equipes, List<Strategie> strategies, boolean deuxVraisJoueurs) {
        this.grille = grille;
        this.equipes = new ArrayList<>(equipes);
        this.strategies = new ArrayList<>(strategies);
        this.frameCounter = 0;
        this.deuxVraisJoueurs = deuxVraisJoueurs;
        this.executorPartage = Executors.newVirtualThreadPerTaskExecutor();
        this.controleurJeu = new ControleurJeu(grille, executorPartage);
        this.tempsDebut = System.currentTimeMillis();
    }

    /**
     * Methode principale appelee a chaque tick de la boucle de jeu.
     * Met a jour la position des curseurs (joueurs et IA) puis demande au
     * ControleurJeu
     * de deplacer les particules. Verifie enfin si la partie est fini.
     */
    public void updateFrame() {
        frameCounter++;

        // pour déplacer les curseurs (ex : joueur et IA joue les 30 frames)
        deplacerCurseurs();

        // déplacement particules
        controleurJeu.deplacerToutesLesParticules(equipes);

        // on vérifie si la partie est terminé
        verifierFinPartie();
    }

    /**
     * Gere le deplacement des cibles pour toutes les equipes.
     * Les humains deplace a chaque frame.
     * Les IAs deplace selon leur frequence de rafraichissement (toutes les
     * FRAMES_IA frames),
     * sauf pour la teleportation qui a un rythme specifique.
     * Utilise des taches asynchrones pour ne pas bloquer le thread principal par
     * les calculs d'IA.
     */
    private void deplacerCurseurs() {
        // joueur 1 (souris) bouge toujours
        deplacerCurseur(0);

        // si y'a joueur 2 (clavier), il bouge aussi toujours
        if (deuxVraisJoueurs) {
            deplacerCurseur(1);
        }

        List<Future<Void>> futuresIA = new ArrayList<>();

        // on commence à l'indice 1 ou 2 selon si y'a 2 joueurs humains
        int indiceDebutIA = deuxVraisJoueurs ? 2 : 1;

        for (int i = indiceDebutIA; i < equipes.size(); i++) {
            final int index = i;
            Strategie strat = strategies.get(index);

            boolean doitBouger = false;
            if (strat instanceof StrategieIATeleportation && frameCounter % 120 == 0) {
                doitBouger = true;
            } else if (!(strat instanceof StrategieIATeleportation) && frameCounter % FRAMES_IA == 0) {
                doitBouger = true;
            }

            if (doitBouger) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    Coordonnees nouvellePos = strategies.get(index).determinerNouveauCurseur();

                    // ca lock ouuuu
                    if (nouvellePos != null) {
                        Equipe equipe = equipes.get(index);
                        synchronized (equipe) {
                            // deplacerCurseur() ne recalculera que si la position change
                            equipe.deplacerCurseur(nouvellePos, grille);
                        }
                    }
                }, executorPartage);
                futuresIA.add(future);
            }
        }

        // on attends tout le monde
        if (!futuresIA.isEmpty()) {
            try {
                CompletableFuture.allOf(futuresIA.toArray(new CompletableFuture[0])).join();
            } catch (Exception e) {
                System.err.println("Erreur lors du calcul IA: " + e.getMessage());
            }
        }
    }

    /**
     * Applique le mouvement pour une equipe specifique.
     * 
     * @param index L'index de l'equipe a mettre a jour.
     */
    private void deplacerCurseur(int index) {
        Coordonnees nouvellePos = strategies.get(index).determinerNouveauCurseur();
        if (nouvellePos != null) {
            Equipe equipe = equipes.get(index);
            synchronized (equipe) {
                equipe.deplacerCurseur(nouvellePos, grille);
            }
        }
    }

    /**
     * Permet la mise a jour de la cible du joueur humain via les entrees souris.
     * Methode thread-safe pour eviter les conflits avec la boucle de jeu.
     * 
     * @param nouvellePosition La nouvelle coordonnee ciblee par la souris.
     */
    public void deplacerCurseurJoueur(Coordonnees nouvellePosition) {
        if (equipes.isEmpty())
            return;
        Equipe equipe = equipes.get(0);
        synchronized (equipe) {
            equipe.deplacerCurseur(nouvellePosition, grille);
        }
    }

    /**
     * Verifie si les conditions de fin de partie sont remplies.
     * La partie s'arrete s'il reste une seule equipe (ou zero) en vie.
     */
    private void verifierFinPartie() {
        // stream prime merci les tp
        long equipesEnVie = equipes.stream().filter(e -> !e.estElimine()).count();

        if (equipesEnVie <= 1) {
            partieTerminee = true;
        }
    }

    public boolean estTerminee() {
        return partieTerminee;
    }

    public Equipe getGagnant() {
        return equipes.stream().filter(e -> !e.estElimine()).findFirst().orElse(null);
    }

    /**
     * Calcule le classement final.
     * Les equipes sont triees :
     * 1. Les survivants devant les elimines.
     * 2. Parmi les survivants, par nombre de particules.
     * 3. Parmi les elimines, par temps de survie.
     * 
     * @return Liste ordonnee des equipes.
     */
    public List<Equipe> getClassement() {
        return equipes.stream()
                .sorted((e1, e2) -> {
                    // les équipes encore en vie sont devant
                    if (!e1.estElimine() && e2.estElimine())
                        return -1;
                    if (e1.estElimine() && !e2.estElimine())
                        return 1;

                    // si les 2 sont en vie, on compare par nb de particules
                    if (!e1.estElimine() && !e2.estElimine()) {
                        return Integer.compare(e2.getNombreParticules(), e1.getNombreParticules());
                    }

                    // si les 2 sont mortes, celle qui a survécu le plus longtemps est devant
                    return Long.compare(e2.getTempsElimination(), e1.getTempsElimination());
                })
                .toList();
    }

    /**
     * Gere la mise en pause pour ajuster le chrono de la partie.
     * 
     * @param enPause true pour mettre en pause, false pour reprendre.
     */
    public void setPause(boolean enPause) {
        if (enPause) {
            // on note le temps quand on a mis pause
            momentPause = System.currentTimeMillis();
        } else {
            // quand on revient on cacule la durée de la sieste
            long dureeSieste = System.currentTimeMillis() - momentPause;
            tempsDebut += dureeSieste;
        }
    }

    public long getTempsDebut() {
        return tempsDebut;
    }

    // getters
    public Grille getGrille() {
        return grille;
    }

    public List<Equipe> getEquipes() {
        return Collections.unmodifiableList(equipes);
    }

    public Equipe getEquipeJoueur() {
        return equipes.isEmpty() ? null : equipes.get(0);
    }

    public Strategie getStrategieJoueur() {
        return strategies.isEmpty() ? null : strategies.get(0);
    }

    public Strategie getStrategieJoueur(int index) { // pour le 2ème joueur
        if (index >= 0 && index < strategies.size()) {
            return strategies.get(index);
        }
        return null;
    }

    /**
     * Nettoie les ressources, notamment en arretant proprement l'ExecutorService.
     */
    public void arreter() {
        executorPartage.shutdown();
        try {
            if (!executorPartage.awaitTermination(2, TimeUnit.SECONDS)) {
                executorPartage.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorPartage.shutdownNow();
            Thread.currentThread().interrupt(); // faut couvrir ses arrières
        }
    }
}