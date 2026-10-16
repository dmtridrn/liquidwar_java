package liquidwars.modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.paint.Color;

public class CaseTest {

    private Case caseTest;
    private Coordonnees position;

    @BeforeEach
    public void setUp() {
        position = new Coordonnees(5, 10);
        caseTest = new Case(position);
    }

    @Test
    public void testCaseInitiale() {
        assertFalse(caseTest.estObstacle());
        assertTrue(caseTest.estLibre());
        assertFalse(caseTest.contientParticule());
        assertNull(caseTest.getParticule());
    }

    @Test
    public void testAjoutParticule() {
        Equipe eq = new Equipe(Color.RED, position);
        Particule p = new Particule(position, eq, 50f);

        caseTest.ajouterParticule(p);

        assertFalse(caseTest.estLibre());
        assertTrue(caseTest.contientParticule());
        assertEquals(p, caseTest.getParticule());
    }

    @Test
    public void testRetirerParticule() {
        Equipe eq = new Equipe(Color.RED, position);
        Particule p = new Particule(position, eq, 50f);
        caseTest.ajouterParticule(p);

        caseTest.retirerParticule();

        assertTrue(caseTest.estLibre());
        assertFalse(caseTest.contientParticule());
        assertNull(caseTest.getParticule());
    }

    @Test
    public void testMarquerObstacle() {
        caseTest.marquerObstacle();

        assertTrue(caseTest.estObstacle());
        assertFalse(caseTest.estLibre());
    }

    @Test
    public void testObstacleRefuseParticule() {
        caseTest.marquerObstacle();
        Equipe eq = new Equipe(Color.RED, position);
        Particule p = new Particule(position, eq, 50f);

        caseTest.ajouterParticule(p);

        // l'obstacle refuse la particule
        assertNull(caseTest.getParticule());
    }
}