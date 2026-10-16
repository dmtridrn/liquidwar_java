## Compilation et Exécution

### Prérequis
- Java 21
- Gradle 9.0.0

### Compilation
```bash
./gradlew build | gradle build
```

### Exécution
```bash
./gradlew run | gradle run
```

### Tests
```bash
./gradlew test | gradle test
```

## Fonctionnalités Implémentées

- [x] Calcul du gradient par BFS
- [x] Règles de déplacement des particules
- [x] Interface graphique JavaFX
- [x] IA (4 types : Aléatoire, Agressive, Fixe, Ultra-Agressive)
- [x] Multi-joueur local sur la même machine (jusqu'à 4 joueurs, dont 2 humains)
- [x] Parallélisation avec threads virtuels
- [X] Optimisation du gradient (mailles)

## Architecture

### Modèle
- **Grille** : représentation 2D avec obstacles
- **Particule** : entité de base avec énergie
- **Equipe** : regroupe particules et gradient
- **Gradient** : champ de distances calculé par BFS

### Vue
- **VueGrille** : affichage Canvas JavaFX
- **VueJauges** : statistiques en temps réel
- **MenuPrincipal** : sélection du nombre de joueurs et IA

### Contrôleur
- **ControleurPartie** : orchestration globale
- **ControleurJeu** : déplacement des particules

## Choix Techniques

### Threads Virtuels (Java 21)
Utilisation de `Executors.newVirtualThreadPerTaskExecutor()` pour :
- Calcul parallèle des actions IA
- Déplacement parallèle des particules par équipe

### Interfaces scellés
- `TypeAction` : types d'actions possibles (Déplacer, Attaquer, Transferer)
- `ConfigurationPartie` : configuration type-safe des équipes

## Patrons de Conception

- **MVC (Modèle-Vue-Contrôleur)** : Architecture globale du projet séparant les modèles (`Grille`, `Particule`), l'affichage (`VueGrille`) et le contrôleur (`ControleurPartie`)

- **Stratégies** :

- L'interface `Strategie` permet d'interchanger les algorithmes de déplacement (Fixe, Aléatoire, Agressive, Téléportation)
- Utilisation de records scellés (`TypeAction`) pour encapsuler les intentions d'actions des particules

- **Fabrique** : La classe `FabriquePartie` centralise l'instanciation du jeu (création de la grille, placement des équipes ... )

### Synchronisation
- `synchronized` sur les équipes lors du déplacement du curseur
- Lock sur la grille lors de l'exécution des actions

## Tests

Couverture des tests :
- **Modèle** : 10 classes de tests unitaires 
(Case, Configuration, Direction, Equipe, Fabrique, Gradient, Grille, Particule, Strategies (IA Fixe, Joueur) )
- **Contrôleur** : Tests d'intégration (Controleur Jeu et Partie)
- **Simulation** : Tests de scénarios complets
- **Performance** : Tests de perfomances du jeu
