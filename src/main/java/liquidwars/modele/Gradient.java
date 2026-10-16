package liquidwars.modele;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Calcule et stocke les distances de chaque case vers une cible.
 * Utilise un BFS pour trouver les chemins les plus courts en tenant compte des obstacles.
 */
public class Gradient {
    private final int[][] distances;
    private final Grille grille;
    private Coordonnees cible;
    private final Queue<Coordonnees> file;

    /**
     * Crée un gradient vers la cible spécifiée.
     * 
     * @param grille La grille du jeu.
     * @param cible La position cible vers laquelle calculer les distances.
     */
    public Gradient(Grille grille, Coordonnees cible){
        this.grille = grille;
        this.cible = cible;
        this.distances = new int[grille.getLargeur()][grille.getHauteur()];
        this.file = new ArrayDeque<>();
        executerBFS();
    }

    /**
     * Recalcule le gradient vers une nouvelle cible.
     * 
     * @param nouvelleCible La nouvelle position cible.
     */
    public void recalculer(Coordonnees nouvelleCible) {
        this.cible = nouvelleCible;
        executerBFS();
    }

    /**
     * Exécute un parcours en largeur (BFS) pour calculer les distances.
     */
    private void executerBFS(){
        //init
        for (int x = 0; x < grille.getLargeur(); x++) {
            for (int y = 0; y < grille.getHauteur(); y++) {
                distances[x][y] = Integer.MAX_VALUE;
            }
        }

        distances[cible.x()][cible.y()] = 0; //point de départ genre le curseur tv

        //file
        file.clear();//mtn on garde la meme file juste on la vide
        file.offer(cible);

        //parcours en biiiig largeur brr
        while(!file.isEmpty()){
            Coordonnees curr = file.poll(); //english enft bog mode
            int distanceCourante = distances[curr.x()][curr.y()];

            //explorer les cases en haut en bas a gauche a droite grace a la super enum mashallah jaime coder
            for (Direction dir : Direction.values()) {
                Coordonnees voisin = dir.appliquer(curr);

                if(estDansLaGrille(voisin)){////ptit check tu vois en bien il sait le kho
                    Case caseVoisine = grille.getCase(voisin);

                    //ca lui donne nvlle dist en bien
                    if(!caseVoisine.estObstacle() && 
                        distances[voisin.x()][voisin.y()] == Integer.MAX_VALUE){//si on a aps encore vu tu vois
                        
                        distances[voisin.x()][voisin.y()] = distanceCourante + 1;

                        file.offer(voisin);
                    }
                }
            }
        }
    }

    /**
     * Vérifie si des coordonnées sont dans les limites de la grille.
     * 
     * @param coord Les coordonnées à vérifier.
     * @return Vrai si les coordonnées sont valides.
     */
    private boolean estDansLaGrille(Coordonnees coord) { //ptit check tu vois en bien il sait le kho
        return coord.x() >= 0 && coord.x() < grille.getLargeur() &&
               coord.y() >= 0 && coord.y() < grille.getHauteur();
    }

    /**
     * Retourne la distance d'une position vers la cible.
     * 
     * @param pos Les coordonnées de la position.
     * @return La distance vers la cible (Integer.MAX_VALUE si inaccessible).
     */
    public int getDistance(Coordonnees pos){
        return distances[pos.x()][pos.y()];
    }
}
