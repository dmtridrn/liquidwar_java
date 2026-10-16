package liquidwars.modele;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.paint.Color;
import liquidwars.modele.ConfigurationPartie.*;
import java.util.ArrayList;

public class ConfigurationPartieTest {

    @Test
    public void testConfigJoueur() {
        ConfigJoueur config = new ConfigJoueur();
        assertEquals("JOUEUR", config.nom());

        Strategie strat = config.creer();
        assertNotNull(strat);
        assertTrue(strat instanceof StrategieJoueur);
    }

    @Test
    public void testConfigIAAleatoire() {
        TypeStrategie type = new StrategieAleatoireConfig();
        ConfigIA config = new ConfigIA(type);

        assertEquals("IA - Aléatoire", config.nom());

        Grille grille = new Grille(50, 50);
        Equipe equipe = new Equipe(Color.RED, new Coordonnees(10, 10));

        Strategie strat = config.creer(equipe, grille, new ArrayList<>());
        assertNotNull(strat);
        assertTrue(strat instanceof StrategieIAAleatoire);
    }

    @Test
    public void testConfigIAIntelligente() {
        TypeStrategie type = new StrategieInteligenteConfig();
        assertEquals("Aggressive", type.nom());

        Grille grille = new Grille(50, 50);
        Equipe equipe = new Equipe(Color.BLUE, new Coordonnees(20, 20));

        Strategie strat = type.creer(equipe, grille, new ArrayList<>());
        assertTrue(strat instanceof StrategieIAIntelligente);
    }

    @Test
    public void testTypesIADisponibles() {
        // on vérifie qu'il y a bien plusieurs types d'IA
        assertFalse(FabriquePartie.TYPES_IA_DISPONIBLES.isEmpty());
        assertTrue(FabriquePartie.TYPES_IA_DISPONIBLES.size() >= 3);
    }
}