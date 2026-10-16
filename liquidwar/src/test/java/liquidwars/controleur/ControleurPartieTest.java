package liquidwars.controleur;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import liquidwars.modele.*;
import liquidwars.modele.ConfigurationPartie.*;
import java.util.List;

public class ControleurPartieTest {

    @Test
    public void testFinDePartieQuandUneEquipeGagne() {
        ControleurPartie partie = FabriquePartie.creerPartieAvecConfig(
                List.of(new StrategieAleatoireConfig()),
                false,
                800, // largeur
                600); // hauteur

        // on simule l'élimination d'une équipe
        Equipe eq1 = partie.getEquipes().get(0);
        Equipe eq2 = partie.getEquipes().get(1);

        // on retire toutes les particules de eq2
        List<Particule> particules = List.copyOf(eq2.getParticules());
        for (Particule p : particules) {
            eq2.retirerParticule(p);
        }

        // onn vérifie que eq2 est éliminée
        assertTrue(eq2.estElimine());
        assertFalse(eq1.estElimine());
    }

    @Test
    public void testClassementOrdonne() {
        ControleurPartie partie = FabriquePartie.creerPartieAvecConfig(
                List.of(new StrategieAleatoireConfig()),
                false,
                800, // largeur
                600); // hauteur

        List<Equipe> classement = partie.getClassement();

        // le classement doit être trié par nombre de particules
        for (int i = 0; i < classement.size() - 1; i++) {
            assertTrue(
                    classement.get(i).getNombreParticules() >= classement.get(i + 1).getNombreParticules());
        }
    }
}