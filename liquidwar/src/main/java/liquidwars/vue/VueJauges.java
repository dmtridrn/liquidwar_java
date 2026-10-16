package liquidwars.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import liquidwars.modele.Equipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Affiche les statistiques de toutes les équipes sous forme de jauges.
 * Chaque jauge montre le nombre de particules et le pourcentage de domination d'une équipe.
 */
public class VueJauges extends VBox {
    
    private static final double LARGEUR_JAUGE = 120;
    private static final double HAUTEUR_BARRE = 20;
    
    private final List<Equipe> equipes;
    private final List<JaugeEquipe> jauges;
    
    /**
     * Initialise le panneau des jauges pour toutes les équipes.
     * 
     * @param equipes La liste des équipes à afficher.
     */
    public VueJauges(List<Equipe> equipes) {
        this.equipes = equipes;
        this.jauges = new ArrayList<>();
        
        setSpacing(15);
        setPadding(new Insets(20, 15, 20, 15));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #2b2b2b;");
        setPrefWidth(LARGEUR_JAUGE + 30);
        
        // titre 
        Label titre = new Label("Équipes");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        getChildren().add(titre);
        
        // une jauge par equipe
        for (int i = 0; i < equipes.size(); i++) {
            JaugeEquipe jauge = new JaugeEquipe(equipes.get(i), i + 1);
            jauges.add(jauge);
            getChildren().add(jauge);
        }
    }
    
    /**
     * Met à jour l'affichage de toutes les jauges avec les dernières statistiques.
     * Calcule les pourcentages en fonction du nombre total de particules.
     */
    public void mettreAJour() {
        //trql pas gourmand
        int totalParticules = 0;
        for (Equipe equipe : equipes) {
            totalParticules += equipe.getNombreParticules();
        }
        
        //update
        for (int i = 0; i < jauges.size(); i++) {
            jauges.get(i).mettreAJour(totalParticules);
        }
    }
    
    /**
     * Représente la jauge d'une équipe avec son numéro, sa couleur et ses statistiques.
     */
    private static class JaugeEquipe extends VBox {
        private final Equipe equipe;
        private final Label labelStats;
        private final Rectangle barreProgression;
        private final Rectangle barreFond;
        
        /**
         * Crée la jauge pour une équipe spécifique.
         * 
         * @param equipe L'équipe à représenter.
         * @param numero Le numéro d'affichage de l'équipe.
         */
        public JaugeEquipe(Equipe equipe, int numero) {
            this.equipe = equipe;
            
            setSpacing(5);
            setAlignment(Pos.CENTER);
            
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);
            
            Rectangle indicateurCouleur = new Rectangle(16, 16);
            indicateurCouleur.setFill(equipe.getCouleur());
            indicateurCouleur.setArcWidth(4);
            indicateurCouleur.setArcHeight(4);
            indicateurCouleur.setStroke(Color.WHITE);
            indicateurCouleur.setStrokeWidth(1);
            
            Label labelNumero = new Label("Équipe " + numero);
            labelNumero.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            
            header.getChildren().addAll(indicateurCouleur, labelNumero);
            
            //label pour écrire
            labelStats = new Label("0 (0%)");
            labelStats.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");
            
            //jauges
            barreFond = new Rectangle(LARGEUR_JAUGE, HAUTEUR_BARRE);
            barreFond.setFill(Color.rgb(60, 60, 60));
            barreFond.setArcWidth(6);
            barreFond.setArcHeight(6);
            
            barreProgression = new Rectangle(0, HAUTEUR_BARRE);
            barreProgression.setFill(equipe.getCouleur());
            barreProgression.setArcWidth(6);
            barreProgression.setArcHeight(6);
            
            // cases genre jcrois
            javafx.scene.layout.StackPane conteneurBarre = new javafx.scene.layout.StackPane();
            conteneurBarre.setAlignment(Pos.CENTER_LEFT);
            conteneurBarre.getChildren().addAll(barreFond, barreProgression);
            
            getChildren().addAll(header, labelStats, conteneurBarre);
        }
        
        /**
         * Met à jour l'affichage de la jauge avec les nouvelles statistiques.
         * 
         * @param totalParticules Le nombre total de particules de toutes les équipes.
         */
        public void mettreAJour(int totalParticules) {
            int nbParticules = equipe.getNombreParticules();
            double pourcentage = totalParticules > 0 
                ? (nbParticules * 100.0 / totalParticules) 
                : 0;
            
            labelStats.setText(String.format("%d (%.1f%%)", nbParticules, pourcentage));
            
            double largeurBarre = (pourcentage / 100.0) * LARGEUR_JAUGE;
            barreProgression.setWidth(Math.max(0, largeurBarre));
        }
    }
}
