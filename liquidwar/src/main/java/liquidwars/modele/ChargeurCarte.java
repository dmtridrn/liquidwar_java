package liquidwars.modele;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Charge une grille depuis un fichier texte.
 * Les caractères '#' représentent les obstacles.
 */
public class ChargeurCarte {

    /**
     * Charge une grille depuis un fichier dans les ressources.
     * 
     * @param nomFichier Le nom du fichier de carte (dans /cartes/).
     * @return La grille chargée, ou une grille par défaut en cas d'erreur.
     */
    public static Grille chargerCarte(String nomFichier) {
        try {
            String chemin = "/cartes/" + nomFichier;
            InputStream is = ChargeurCarte.class.getResourceAsStream(chemin);
            
            if (is == null) {
                throw new RuntimeException("pas de carte : " + chemin);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            List<String> lignes = new ArrayList<>();
            String ligne;
            //on lit tout
            while ((ligne = reader.readLine()) != null) {
                lignes.add(ligne);
            }
            
            if (lignes.isEmpty()) {
                throw new RuntimeException("La carte est vide !");
            }

            int hauteur = lignes.size();
            int largeur = lignes.get(0).length();
            Grille grille = new Grille(largeur, hauteur);

            for (int y = 0; y < hauteur; y++) {
                String l = lignes.get(y);
                for (int x = 0; x < Math.min(l.length(), largeur); x++) {
                    char c = l.charAt(x);
                    if (c == '#') {
                        grille.getCase(new Coordonnees(x, y)).marquerObstacle();
                    }
                }
            }
            return grille;

        } catch (Exception e) {
            System.err.println("erreur chargement carte : " + e.getMessage());
            e.printStackTrace();
            return new Grille(50, 50); 
        }
    }
}