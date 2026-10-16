package liquidwars.modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GrilleTest {

    private Grille grille;

    @BeforeEach
    public void setUp() {
        grille = new Grille(100, 80);
    }

    @Test
    public void testDimensionsGrille() {
        assertEquals(100, grille.getLargeur());
        assertEquals(80, grille.getHauteur());
    }

    @Test
    public void testCoordonneesDansLimites() {
        assertTrue(grille.estDansLesLimites(new Coordonnees(0, 0)));
        assertTrue(grille.estDansLesLimites(new Coordonnees(99, 79)));
        assertFalse(grille.estDansLesLimites(new Coordonnees(-1, 0)));
        assertFalse(grille.estDansLesLimites(new Coordonnees(100, 0)));
        assertFalse(grille.estDansLesLimites(new Coordonnees(0, 80)));
    }

    @Test
    public void testGetCaseValide() {
        Coordonnees coord = new Coordonnees(50, 40);
        Case c = grille.getCase(coord);
        assertNotNull(c);
        assertEquals(coord, c.getPosition());
    }

    @Test
    public void testGetCaseInvalide() {
        assertThrows(IllegalArgumentException.class,
                () -> grille.getCase(new Coordonnees(-1, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> grille.getCase(new Coordonnees(100, 50)));
    }

    @Test
    public void testCasesInitialementLibres() {
        for (int x = 0; x < grille.getLargeur(); x++) {
            for (int y = 0; y < grille.getHauteur(); y++) {
                Case c = grille.getCase(new Coordonnees(x, y));
                assertTrue(c.estLibre());
                assertFalse(c.estObstacle());
            }
        }
    }
}