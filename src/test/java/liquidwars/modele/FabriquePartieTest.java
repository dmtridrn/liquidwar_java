package liquidwars.modele;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import liquidwars.controleur.ControleurPartie;
import liquidwars.modele.ConfigurationPartie.*;
import java.util.List;

public class FabriquePartieTest {

    @Test
    public void testCreationPartie2Joueurs() {
        ControleurPartie partie = FabriquePartie.creerPartieAvecConfig(
                List.of(new StrategieAleatoireConfig()),
                false, 800, 600); 

        assertEquals(2, partie.getEquipes().size());
        assertEquals(2, FabriquePartie.getNbEquipes());
    }

    @Test
    public void testCreationPartie4Joueurs() {
        ControleurPartie partie = FabriquePartie.creerPartieAvecConfig(
                List.of(
                        new StrategieAleatoireConfig(),
                        new StrategieInteligenteConfig(),
                        new StrategieFixeConfig()),
                false, 800, 600);

        assertEquals(4, partie.getEquipes().size());
    }

    @Test
    public void testInitialisationParticules() {
        ControleurPartie partie = FabriquePartie.creerPartieAvecConfig(
                List.of(new StrategieAleatoireConfig()),
                false, 800, 600); 

        // chaque équipe doit avoir 1225 particules (35x35)
        for (Equipe equipe : partie.getEquipes()) {
            assertEquals(1225, equipe.getNombreParticules());
        }
    }
}