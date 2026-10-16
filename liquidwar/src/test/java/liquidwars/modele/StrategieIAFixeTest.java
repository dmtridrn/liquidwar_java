package liquidwars.modele;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StrategieIAFixeTest {

    @Test
    public void testCibleFixe() {
        Coordonnees cible = new Coordonnees(25, 30);
        StrategieIAFixe strategie = new StrategieIAFixe(cible);

        assertEquals(cible, strategie.determinerNouveauCurseur());
        assertEquals(cible, strategie.determinerNouveauCurseur());
    }

    @Test
    public void testChangementCible() {
        Coordonnees cible1 = new Coordonnees(10, 10);
        Coordonnees cible2 = new Coordonnees(20, 20);

        StrategieIAFixe strategie = new StrategieIAFixe(cible1);
        assertEquals(cible1, strategie.determinerNouveauCurseur());

        strategie.setCible(cible2);
        assertEquals(cible2, strategie.determinerNouveauCurseur());
    }
}