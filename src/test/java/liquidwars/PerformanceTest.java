package liquidwars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import liquidwars.controleur.ControleurPartie;
import liquidwars.modele.*;
import liquidwars.modele.ConfigurationPartie.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerformanceTest {

    private ControleurPartie partie;
    private ExecutorService executor;

    @BeforeEach
    public void setUp() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @AfterEach
    public void tearDown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    public void testPerformance100Iterations() {
        partie = FabriquePartie.creerPartieAvecConfig(
                List.of(new StrategieAleatoireConfig()),
                false, 800, 800);

        long debut = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            partie.updateFrame();
        }

        long fin = System.currentTimeMillis();
        long duree = fin - debut;

        // on vérifie que 100 itérations prennent moins de 10 secondes
        assertTrue(duree < 10000, "100 itérations ont pris " + duree + "ms");
    }

    @Test
    public void testPerformance4Equipes() {
        partie = FabriquePartie.creerPartieAvecConfig(
                List.of(
                        new StrategieAleatoireConfig(),
                        new StrategieInteligenteConfig(),
                        new StrategieFixeConfig()),
                false, 800, 800);

        long debut = System.currentTimeMillis();

        for (int i = 0; i < 50; i++) {
            partie.updateFrame();
        }

        long fin = System.currentTimeMillis();
        long duree = fin - debut;

        assertTrue(duree < 15000, "50 itérations avec 4 équipes ont pris " + duree + "ms");
    }

    @Test
    public void testStabiliteEnergie() {
        partie = FabriquePartie.creerPartieAvecConfig(
                List.of(new StrategieAleatoireConfig()),
                false, 800, 800);

        float energieInitiale = 0f;
        for (Equipe eq : partie.getEquipes()) {
            energieInitiale += eq.getEnergieTotale();
        }

        for (int i = 0; i < 200; i++) {
            partie.updateFrame();
        }

        float energieFinale = 0f;
        for (Equipe eq : partie.getEquipes()) {
            energieFinale += eq.getEnergieTotale();
        }

        // l'énergie totale doit rester stable
        float variation = Math.abs(energieFinale - energieInitiale) / energieInitiale;
        assertTrue(variation < 0.1f, "Variation d'énergie de " + (variation * 100) + "%");
    }

    @Test
    public void testPartieJusquaFin() {
        partie = FabriquePartie.creerPartieAvecConfig(
                List.of(
                        new StrategieAleatoireConfig(),
                        new StrategieAleatoireConfig()),
                false, 800, 800);

        int nbParticulesDebut = 0;
        for (Equipe eq : partie.getEquipes()) {
            nbParticulesDebut += eq.getNombreParticules();
        }

        // on fait tourner 5000 frames
        for (int i = 0; i < 5000; i++) {
            partie.updateFrame();
        }

        // on vérifie que les particules bougent/attaquent .. 
        int nbParticulesApres = 0;
        for (Equipe eq : partie.getEquipes()) {
            nbParticulesApres += eq.getNombreParticules();
        }

        // le nombre total reste constant
        assertEquals(nbParticulesDebut, nbParticulesApres);

        // mais il y a eu du changement entre les équipes
        boolean changement = false;
        for (Equipe eq : partie.getEquipes()) {
            if (eq.getNombreParticules() != nbParticulesDebut / 2) {
                changement = true;
                break;
            }
        }
        assertTrue(changement, "Les particules devraient avoir changé d'équipe");
    }

    @Test
    public void testMemoireParticules() {
        partie = FabriquePartie.creerPartieAvecConfig(
                List.of(new StrategieAleatoireConfig()),
                false, 800, 800);

        int nbParticulesInitial = 0;
        for (Equipe eq : partie.getEquipes()) {
            nbParticulesInitial += eq.getNombreParticules();
        }

        for (int i = 0; i < 100; i++) {
            partie.updateFrame();
        }

        int nbParticulesFinales = 0;
        for (Equipe eq : partie.getEquipes()) {
            nbParticulesFinales += eq.getNombreParticules();
        }

        // le nombre total de particules doit rester constant
        assertEquals(nbParticulesInitial, nbParticulesFinales);
    }
}