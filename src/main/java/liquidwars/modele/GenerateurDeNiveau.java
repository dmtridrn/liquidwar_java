package liquidwars.modele;

public class GenerateurDeNiveau {

    public static Grille generer(int largeur, int hauteur) {
        Grille grille = new Grille(largeur, hauteur);
        
        int cx = largeur / 2;
        int cy = hauteur / 2;

        double echelle = 5.0; 
        double epaisseurMur = 0.1; 

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                boolean estMur = false;

                // bordures
                if (x < 2 || x >= largeur - 2 || y < 2 || y >= hauteur - 2) {
                    estMur = true;
                } else {
                    double dx = x - cx;
                    double dy = y - cy;
                    double dist = Math.sqrt(dx*dx + dy*dy);

                    // pattern avec sin/cos
                    double couchePrincipale = Math.sin(x / echelle) * Math.cos(y / echelle) + 0.15;
                    double turbulence = Math.sin((x + y) / (echelle * 2));

                    if (couchePrincipale + (turbulence * 0.5) > epaisseurMur) {
                        estMur = true;
                    }

                    // arène centrale
                    if (dist < 40 && dist > 15) estMur = false; 
                    if (dist < 10) estMur = true;

                    // tunnels en croix et diagonale
                    if (Math.abs(dx) < 5 || Math.abs(dy) < 5) estMur = false;
                    if (Math.abs(Math.abs(dx) - Math.abs(dy)) < 8) estMur = false;

                    // zones de spawn dans les coins
                    if ((x < 40 && y < 40) || 
                        (x > largeur - 40 && y < 40) ||
                        (x < 40 && y > hauteur - 40) || 
                        (x > largeur - 40 && y > hauteur - 40)) {
                        estMur = false;
                    }
                }

                if (estMur) {
                    grille.getCase(new Coordonnees(x, y)).marquerObstacle();
                }
            }
        }
        return grille;
    }
}