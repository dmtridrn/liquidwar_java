package liquidwars.modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.paint.Color;
import java.util.List;
import liquidwars.modele.ConfigurationPartie.StrategieAleatoireConfig;

public class ParticuleTest {

    private Particule particule;
    private Equipe equipe;
    private Coordonnees pos;
    private Grille grille;
    private float ENERGIE_INIT;
    private float ENERGIE_ATQ;

    @BeforeEach
    public void setUp() {
        FabriquePartie.creerPartieAvecConfig(List.of(new StrategieAleatoireConfig()), false, 800, 800);

        ENERGIE_INIT = 100f / FabriquePartie.getNbEquipes();
        ENERGIE_ATQ = 20f / FabriquePartie.getNbEquipes();

        grille = new Grille(20, 20);
        pos = new Coordonnees(5, 5);
        equipe = new Equipe(Color.RED, new Coordonnees(0, 0));
        particule = new Particule(pos, equipe, ENERGIE_INIT);
    }

    @Test
    public void testCreation() {
        assertEquals(pos, particule.getPosition());
        assertEquals(equipe, particule.getEquipe());
        assertEquals(ENERGIE_INIT, particule.getEnergie());
        assertFalse(particule.estMorte());
    }

    @Test
    public void testPerdreEnergie() {
        particule.perdreEnergie(10f);
        assertEquals(ENERGIE_INIT - 10f, particule.getEnergie(), 0.01f);

        particule.perdreEnergie(200f);
        assertTrue(particule.estMorte());
    }

    @Test
    public void testGagnerEnergie() {
        Particule p = new Particule(pos, equipe, 10f);
        p.gagnerEnergie(15f);
        assertEquals(25f, p.getEnergie(), 0.01f);

        p.gagnerEnergie(100f);
        assertEquals(100f, p.getEnergie());
    }

    @Test
    public void testAttaqueBasique() {
        Equipe eq2 = new Equipe(Color.BLUE, new Coordonnees(10, 10));
        Particule ennemi = new Particule(new Coordonnees(6, 5), eq2, ENERGIE_INIT);

        grille.getCase(pos).ajouterParticule(particule);
        grille.getCase(ennemi.getPosition()).ajouterParticule(ennemi);

        particule.attaquer(ennemi, grille);

        assertTrue(particule.getEnergie() > ENERGIE_INIT);
        assertEquals(ENERGIE_INIT - ENERGIE_ATQ, ennemi.getEnergie(), 0.01f);
    }

    @Test
    public void testAttaqueMortelle() {
        Equipe eq2 = new Equipe(Color.BLUE, new Coordonnees(10, 10));
        Particule ennemi = new Particule(new Coordonnees(6, 5), eq2, ENERGIE_ATQ);
        eq2.ajouterParticule(ennemi);

        grille.getCase(pos).ajouterParticule(particule);
        grille.getCase(ennemi.getPosition()).ajouterParticule(ennemi);

        boolean mort = particule.attaquer(ennemi, grille);

        assertTrue(mort);
        assertTrue(ennemi.estMorte());
    }

    @Test
    public void testTransfertEnergie() {
        Particule p1 = new Particule(pos, equipe, 50f);
        Particule p2 = new Particule(new Coordonnees(6, 5), equipe, 30f);

        p1.transfererEnergie(p2);

        assertEquals(40f, p1.getEnergie(), 0.01f);
        assertEquals(40f, p2.getEnergie(), 0.01f);
    }

    @Test
    public void testConversion() {
        Equipe eq2 = new Equipe(Color.BLUE, new Coordonnees(10, 10));
        equipe.ajouterParticule(particule);

        particule.convertirVers(eq2);

        assertEquals(eq2, particule.getEquipe());
        assertEquals(0, equipe.getNombreParticules());
        assertEquals(1, eq2.getNombreParticules());
    }

    @Test
    public void testConservationEnergie() {
        Equipe eq2 = new Equipe(Color.BLUE, new Coordonnees(15, 15));
        Particule ennemi = new Particule(new Coordonnees(15, 15), eq2, ENERGIE_INIT);

        grille.getCase(pos).ajouterParticule(particule);
        grille.getCase(ennemi.getPosition()).ajouterParticule(ennemi);

        float total = particule.getEnergie() + ennemi.getEnergie();
        particule.attaquer(ennemi, grille);

        assertEquals(total, particule.getEnergie() + ennemi.getEnergie(), 0.01f);
    }

    @Test
    public void testRedistributionAllies() {
        Particule attaquant = new Particule(pos, equipe, 100f);
        Equipe eq2 = new Equipe(Color.BLUE, new Coordonnees(10, 10));
        Particule ennemi = new Particule(new Coordonnees(6, 5), eq2, ENERGIE_INIT);

        Particule allie = new Particule(Direction.HAUT.appliquer(pos), equipe, 50f);

        grille.getCase(pos).ajouterParticule(attaquant);
        grille.getCase(ennemi.getPosition()).ajouterParticule(ennemi);
        grille.getCase(allie.getPosition()).ajouterParticule(allie);
        equipe.ajouterParticule(allie);

        float total = attaquant.getEnergie() + ennemi.getEnergie() + allie.getEnergie();
        attaquant.attaquer(ennemi, grille);

        assertEquals(100f, attaquant.getEnergie(), 0.01f);
        assertTrue(allie.getEnergie() > 50f);
        assertEquals(total, attaquant.getEnergie() + ennemi.getEnergie() + allie.getEnergie(), 0.01f);
    }
}