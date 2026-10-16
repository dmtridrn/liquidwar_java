package liquidwars.controleur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.paint.Color;
import liquidwars.modele.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControleurJeuTest {

    private ControleurJeu controleur;
    private Grille grille;
    private Equipe eq1, eq2;
    private ExecutorService executor;

    @BeforeEach
    public void setUp() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
        grille = new Grille(20, 20);
        controleur = new ControleurJeu(grille, executor);

        eq1 = new Equipe(Color.RED, new Coordonnees(5, 5));
        eq2 = new Equipe(Color.BLUE, new Coordonnees(15, 15));
        eq1.recalculerGradient(grille);
        eq2.recalculerGradient(grille);
    }

    @Test
    public void testControleurCreation() {
        assertNotNull(controleur);
    }

    @Test
    public void testDeplacerSansParticules() {
        controleur.deplacerToutesLesParticules(Arrays.asList(eq1, eq2));
        assertEquals(0, eq1.getNombreParticules());
        assertEquals(0, eq2.getNombreParticules());
    }

    @Test
    public void testDeplacementVersGradient() {
        Coordonnees pos = new Coordonnees(5, 6);
        Particule p = new Particule(pos, eq1, 100f);
        grille.getCase(pos).ajouterParticule(p);
        eq1.ajouterParticule(p);

        int dist = eq1.getDistanceGradient(pos);
        controleur.deplacerToutesLesParticules(Arrays.asList(eq1, eq2));
        assertTrue(eq1.getDistanceGradient(p.getPosition()) <= dist);
    }

    @Test
    public void testNombreParticulesConstant() {
        for (int i = 0; i < 10; i++) {
            Coordonnees pos = new Coordonnees(5 + i, 5);
            Particule p = new Particule(pos, eq1, 100f);
            grille.getCase(pos).ajouterParticule(p);
            eq1.ajouterParticule(p);
        }

        int nb = eq1.getNombreParticules();
        for (int i = 0; i < 5; i++) {
            controleur.deplacerToutesLesParticules(Arrays.asList(eq1, eq2));
        }
        assertEquals(nb, eq1.getNombreParticules());
    }

    @Test
    public void testBlocageParObstacles() {
        Coordonnees pos = new Coordonnees(5, 5);
        Particule p = new Particule(pos, eq1, 100f);
        grille.getCase(pos).ajouterParticule(p);
        eq1.ajouterParticule(p);

        for (Direction dir : Direction.values()) {
            Coordonnees v = dir.appliquer(pos);
            if (v.x() >= 0 && v.x() < 20 && v.y() >= 0 && v.y() < 20) {
                grille.getCase(v).marquerObstacle();
            }
        }

        controleur.deplacerToutesLesParticules(Arrays.asList(eq1, eq2));
        assertEquals(pos, p.getPosition());
    }

    @Test
    public void testEquipesIndependantes() {
        Particule p1 = new Particule(new Coordonnees(5, 5), eq1, 100f);
        Particule p2 = new Particule(new Coordonnees(15, 15), eq2, 100f);

        grille.getCase(p1.getPosition()).ajouterParticule(p1);
        grille.getCase(p2.getPosition()).ajouterParticule(p2);
        eq1.ajouterParticule(p1);
        eq2.ajouterParticule(p2);

        controleur.deplacerToutesLesParticules(Arrays.asList(eq1, eq2));
        assertEquals(1, eq1.getNombreParticules());
        assertEquals(1, eq2.getNombreParticules());
    }

    @Test
    public void testVolEnergie() {
        Particule p1 = new Particule(new Coordonnees(3, 3), eq1, 50f);
        Particule p2 = new Particule(new Coordonnees(3, 4), eq2, 50f);

        eq1.ajouterParticule(p1);
        eq2.ajouterParticule(p2);
        grille.getCase(p1.getPosition()).ajouterParticule(p1);
        grille.getCase(p2.getPosition()).ajouterParticule(p2);

        p2.perdreEnergie(20f);
        p1.gagnerEnergie(20f);

        assertEquals(30f, p2.getEnergie(), 0.01f);
        assertEquals(70f, p1.getEnergie(), 0.01f);
    }

    @Test
    public void testConversionApresAttaque() {
        Particule p = new Particule(new Coordonnees(4, 4), eq2, 15f);
        eq2.ajouterParticule(p);

        p.perdreEnergie(20f);
        assertTrue(p.estMorte());

        eq2.retirerParticule(p);
        p.setEquipe(eq1);
        eq1.ajouterParticule(p);
        p.gagnerEnergie(30f);

        assertEquals(eq1, p.getEquipe());
        assertEquals(30f, p.getEnergie(), 0.01f);
    }
}