package liquidwars.modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StrategieJoueurTest {

    private StrategieJoueur strategie;

    @BeforeEach
    public void setUp() {
        strategie = new StrategieJoueur();
    }

    @Test
    public void testMiseAJourSouris() {
        Coordonnees pos1 = new Coordonnees(10, 20);
        strategie.mettreAJourSouris(pos1);
        assertEquals(pos1, strategie.determinerNouveauCurseur());

        Coordonnees pos2 = new Coordonnees(30, 40);
        strategie.mettreAJourSouris(pos2);
        assertEquals(pos2, strategie.determinerNouveauCurseur());
    }

    @Test
    public void testPositionInitiale() {
        // au début, pas de position définie
        assertNull(strategie.determinerNouveauCurseur());
    }
}