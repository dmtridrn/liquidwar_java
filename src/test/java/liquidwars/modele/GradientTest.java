package liquidwars.modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GradientTest {

    private Grille grille;
    private Gradient gradient;
    private Coordonnees cible;

    @BeforeEach
    public void setUp() {
        // on crée une grille et un gradient avec cible au centre
        grille = new Grille(10, 10);
        cible = new Coordonnees(5, 5);
        gradient = new Gradient(grille, cible);
    }

    @Test
    public void testGradientCibleDistance0() {
        // on vérifie que la distance à la cible est 0
        assertEquals(0, gradient.getDistance(cible));
    }

    @Test
    public void testGradientVoisin1() {
        // on vérifie que les voisins ont distance 1
        assertEquals(1, gradient.getDistance(new Coordonnees(5, 4)));
        assertEquals(1, gradient.getDistance(new Coordonnees(5, 6)));
        assertEquals(1, gradient.getDistance(new Coordonnees(4, 5)));
        assertEquals(1, gradient.getDistance(new Coordonnees(6, 5)));
    }

    @Test
    public void testGradientVoisinDiagonal() {
        // on vérifie que les voisins diagonaux ont distance 1
        assertEquals(1, gradient.getDistance(new Coordonnees(4, 4)));
        assertEquals(1, gradient.getDistance(new Coordonnees(6, 6)));
    }

    @Test
    public void testGradientCoinOppose() {
        // on vérifie qu'on peut atteindre le coin opposé
        Gradient g = new Gradient(grille, new Coordonnees(0, 0));
        int distance = g.getDistance(new Coordonnees(9, 9));
        assertTrue(distance > 0 && distance != Integer.MAX_VALUE);
    }

    @Test
    public void testGradientAvecObstacle() {
        // on ajoute un obstacle et on vérifie qu'il bloque
        grille.getCase(new Coordonnees(5, 4)).marquerObstacle();
        Gradient g = new Gradient(grille, cible);

        // l'obstacle a distance infinie
        assertEquals(Integer.MAX_VALUE, g.getDistance(new Coordonnees(5, 4)));
        // on peut toujours atteindre la cible
        assertEquals(0, g.getDistance(cible));
        // on peut atteindre d'autres cases
        assertEquals(1, g.getDistance(new Coordonnees(5, 6)));
    }

    @Test
    public void testGradientMurBloqueur() {
        // on crée un mur vertical pour bloquer
        for (int y = 0; y < 10; y++) {
            grille.getCase(new Coordonnees(5, y)).marquerObstacle();
        }

        Gradient gDroite = new Gradient(grille, new Coordonnees(6, 5));
        // les cases à gauche du mur ne doivent pas être atteintes
        assertEquals(Integer.MAX_VALUE, gDroite.getDistance(new Coordonnees(4, 5)));
    }

    @Test
    public void testGradientTousCoinsCible0() {
        // on vérifie que seule la cible a distance 0
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (x == 5 && y == 5) {
                    assertEquals(0, gradient.getDistance(new Coordonnees(x, y)));
                } else {
                    assertTrue(gradient.getDistance(new Coordonnees(x, y)) > 0 ||
                            gradient.getDistance(new Coordonnees(x, y)) == Integer.MAX_VALUE);
                }
            }
        }
    }
}