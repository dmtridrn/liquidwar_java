package liquidwars.modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.paint.Color;

public class EquipeTest {

    private Equipe equipe;
    private Grille grille;
    private Coordonnees posCurseur;

    @BeforeEach
    public void setUp() {
        // on crée une équipe rouge avec curseur en 5,5
        posCurseur = new Coordonnees(5, 5);
        equipe = new Equipe(Color.RED, posCurseur);
        grille = new Grille(10, 10);
    }

    @Test
    public void testEquipeCreation() {
        // on vérifie que l'équipe est créée correctement
        assertEquals(Color.RED, equipe.getCouleur());
        assertEquals(0, equipe.getNombreParticules());
        assertEquals(0f, equipe.getEnergieTotale());
    }

    @Test
    public void testAjouterParticule() {
        // on ajoute une particule et on compte
        Particule p = new Particule(new Coordonnees(0, 0), equipe, 100f);
        equipe.ajouterParticule(p);
        assertEquals(1, equipe.getNombreParticules());
    }

    @Test
    public void testAjouterPlusieursParticules() {
        // on ajoute 5 particules
        for (int i = 0; i < 5; i++) {
            Particule p = new Particule(new Coordonnees(i, i), equipe, 100f);
            equipe.ajouterParticule(p);
        }
        assertEquals(5, equipe.getNombreParticules());
    }

    @Test
    public void testRetirerParticule() {
        // on ajoute puis retire une particule
        Particule p = new Particule(new Coordonnees(0, 0), equipe, 100f);
        equipe.ajouterParticule(p);
        assertEquals(1, equipe.getNombreParticules());

        equipe.retirerParticule(p);
        assertEquals(0, equipe.getNombreParticules());
    }

    @Test
    public void testEnergieTotale() {
        // on ajoute deux particules avec énergies différentes
        equipe.ajouterParticule(new Particule(new Coordonnees(0, 0), equipe, 100f));
        equipe.ajouterParticule(new Particule(new Coordonnees(1, 1), equipe, 50f));
        assertEquals(150f, equipe.getEnergieTotale());
    }

    @Test
    public void testRecalculerGradient() {
        // on recalcule le gradient et on vérifie
        equipe.recalculerGradient(grille);
        Gradient grad = equipe.getGradient();
        assertNotNull(grad);
        assertEquals(0, grad.getDistance(posCurseur));
    }

    @Test
    public void testDeplacerCurseur() {
        // on déplace le curseur et on vérifie
        Coordonnees nouvellePos = new Coordonnees(8, 8);
        equipe.deplacerCurseur(nouvellePos, grille);

        Gradient grad = equipe.getGradient();
        assertEquals(0, grad.getDistance(nouvellePos));
    }

    @Test
    public void testGetDistanceGradient() {
        // on vérifie la distance au gradient
        equipe.recalculerGradient(grille);
        int dist = equipe.getDistanceGradient(new Coordonnees(5, 5));
        assertEquals(0, dist);
    }

    @Test
    public void testGetDistanceGradientSansInitialisation() {
        // on accède à la distance sans initialiser le gradient
        Equipe e = new Equipe(Color.BLUE, new Coordonnees(0, 0));
        assertEquals(Integer.MAX_VALUE, e.getDistanceGradient(new Coordonnees(5, 5)));
    }
}