package liquidwars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.paint.Color;
import liquidwars.modele.*;
import liquidwars.controleur.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SimulationTest {

    private Grille grille;
    private ControleurJeu controleur;
    private Equipe eq1;
    private Equipe eq2;
    private ExecutorService executor;

    @BeforeEach
    public void setUp() {
        // on crée l'executor 
        executor = Executors.newVirtualThreadPerTaskExecutor();
        
        // on crée une grande grille et deux équipes
        grille = new Grille(50, 50);
        controleur = new ControleurJeu(grille, executor); 

        eq1 = new Equipe(Color.RED, new Coordonnees(10, 10));
        eq2 = new Equipe(Color.BLUE, new Coordonnees(40, 40));

        eq1.recalculerGradient(grille);
        eq2.recalculerGradient(grille);
    }

    @Test
    public void testScenarioSimple() {
        // on ajoute 20 particules à eq1
        for (int i = 0; i < 20; i++) {
            Coordonnees pos = new Coordonnees(10 + i % 5, 10 + i / 5);
            Particule p = new Particule(pos, eq1, 100f);
            grille.getCase(pos).ajouterParticule(p);
            eq1.ajouterParticule(p);
        }

        int nbInitial = eq1.getNombreParticules();
        float energieInitiale = eq1.getEnergieTotale();

        // on simule 10 frames
        for (int i = 0; i < 10; i++) {
            controleur.deplacerToutesLesParticules(Arrays.asList(eq1, eq2));
        }

        // on vérifie que le nombre de particules et l'énergie sont constantes
        assertEquals(nbInitial, eq1.getNombreParticules());
        float energieFinal = eq1.getEnergieTotale();
        assertTrue(energieFinal <= energieInitiale);
    }

    @Test
    public void testAvecObstacles() {
        // on crée une barrière verticale au milieu
        for (int y = 10; y < 30; y++) {
            grille.getCase(new Coordonnees(25, y)).marquerObstacle();
        }

        eq1.recalculerGradient(grille);
        eq2.recalculerGradient(grille);

        Particule p = new Particule(new Coordonnees(20, 20), eq1, 100f);
        grille.getCase(new Coordonnees(20, 20)).ajouterParticule(p);
        eq1.ajouterParticule(p);

        for (int i = 0; i < 20; i++) {
            controleur.deplacerToutesLesParticules(Arrays.asList(eq1, eq2));
        }

        // on vérifie que la particule existe toujours
        assertEquals(1, eq1.getNombreParticules());
    }

    @Test
    public void testDeuxEquipesMovement() {
        // on ajoute 10 particules à chaque équipe
        for (int i = 0; i < 10; i++) {
            Coordonnees pos1 = new Coordonnees(10, 10 + i);
            Particule p1 = new Particule(pos1, eq1, 100f);
            grille.getCase(pos1).ajouterParticule(p1);
            eq1.ajouterParticule(p1);

            Coordonnees pos2 = new Coordonnees(40, 40 + i);
            Particule p2 = new Particule(pos2, eq2, 100f);
            grille.getCase(pos2).ajouterParticule(p2);
            eq2.ajouterParticule(p2);
        }

        int nbEq1 = eq1.getNombreParticules();
        int nbEq2 = eq2.getNombreParticules();

        for (int i = 0; i < 5; i++) {
            eq1.deplacerCurseur(new Coordonnees(10 + i, 10), grille);
            eq2.deplacerCurseur(new Coordonnees(40 + i, 40), grille);
            controleur.deplacerToutesLesParticules(Arrays.asList(eq1, eq2));
        }

        // on vérifie que les nombres sont constants
        assertEquals(nbEq1, eq1.getNombreParticules());
        assertEquals(nbEq2, eq2.getNombreParticules());
    }
}