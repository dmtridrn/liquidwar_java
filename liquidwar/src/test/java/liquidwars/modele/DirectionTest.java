package liquidwars.modele;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DirectionTest {

    @Test
    public void testDirectionHaut() {
        Coordonnees depart = new Coordonnees(5, 5);
        Coordonnees arrivee = Direction.HAUT.appliquer(depart);
        assertEquals(new Coordonnees(5, 4), arrivee);
    }

    @Test
    public void testDirectionBas() {
        Coordonnees depart = new Coordonnees(5, 5);
        Coordonnees arrivee = Direction.BAS.appliquer(depart);
        assertEquals(new Coordonnees(5, 6), arrivee);
    }

    @Test
    public void testDirectionGauche() {
        Coordonnees depart = new Coordonnees(5, 5);
        Coordonnees arrivee = Direction.GAUCHE.appliquer(depart);
        assertEquals(new Coordonnees(4, 5), arrivee);
    }

    @Test
    public void testDirectionDroite() {
        Coordonnees depart = new Coordonnees(5, 5);
        Coordonnees arrivee = Direction.DROITE.appliquer(depart);
        assertEquals(new Coordonnees(6, 5), arrivee);
    }

    @Test
    public void testDiagonales() {
        Coordonnees depart = new Coordonnees(10, 10);

        assertEquals(new Coordonnees(11, 9), Direction.HAUTDROITE.appliquer(depart));
        assertEquals(new Coordonnees(9, 9), Direction.HAUTGAUCHE.appliquer(depart));
        assertEquals(new Coordonnees(11, 11), Direction.BASDROITE.appliquer(depart));
        assertEquals(new Coordonnees(9, 11), Direction.BASGAUCHE.appliquer(depart));
    }

    @Test
    public void testToutesLesDirections() {
        // y'a 8 directions au total
        assertEquals(8, Direction.values().length);
    }
}