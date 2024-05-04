# Rapport de Projet

## I.Parties Traités:

### 1. Fonctionalités de bases
Toutes les fonctionalités de bases on été implémentés:
<ul>
<li>Deplacement et tirs dans toutes les directions.</li>
<li>Initialisation des cabines avec des passagers , braquage et déplacement du Marshall et fuite.</li>
<li>Panneau de commandes à deux états (planification et éxécution).</li>
<li>Les bandits touchés par un tir lache un de leur butin.</li>
</ul>

### 2. Fonctionalités du Modèle: (Expression Libre et autres)
<ul>
<li>Les bandits ont une précision et peuvent donc rater leur tir ; un tir raté touchera un passager au hasard et lui fera lâcher son butin. Le passager est considéré comme mort.</li>
<li>Ajout d'une nouvelle action, Frappe, qui est une attaque au corps à corps ne consommant pas de munitions et qui ne peut pas échouer.</li>
<li>Le mode deux joueurs, comme dans le vrai jeu Colt-Express, se joue avec 4 bandits (2 par joueur).</li>
<li>Le mode solo se joue contre un bot, comme dans une partie à quatre joueurs (donc avec 3 bots).</li>
<li>Un bot peut contrôler des bandits avec différents types de comportements.</li>
<li>Les butins "dropés" sur le toit peuvent tomber du train avec une certaine probabilité.</li>
<li>Si un bandit entre dans le wagon du marshall, il est visé par celui-ci et fuit, même si ce n'est pas la fin du tour.</li>
<li>Braquer la locomotive quand le magot est disponible entraîne le vol automatique de celui-ci.</li>
<li>Un bandit blessé par un tir ou une frappe perd 1 HP par blessure, et en conséquence une action, sachant qu'il ne peut descendre en dessous de 2 HP.</li>
</ul>

### 3.Fonctionalités du Graphique:
<ul>
<li>Il y a un menu principal pour récupérer les paramètres de jeu.</li>
<li>La séquence d'ordres donnée par le joueur est affichée au fur et à mesure de la planification.</li>
<li>Il est possible de revenir sur ces choix durant la planification.</li>
<li>Le compte rendu des actions exécutées est affiché au fur et à mesure avec un délai à l'affichage.</li>
<li>Ajout d'un effet visuel quand un bandit est touché : le sprite devient rouge pendant 200 ms et redevient normal.</li>
<li>La locomotive contient un coffre-fort qui change visuellement suivant si le magot a été volé ou pas.</li>
<li>La vue affiche 3 cabines + la locomotive au maximum. Sur des parties à plus de 4 bandits, il faut déplacer "la caméra" à droite ou à gauche pour voir l'intégralité du train, ce déplacement est possible tout le temps, même à l'exécution des actions.</li>
<li>La vue est mise à jour à chaque action exécutée pour plus de dynamisme.</li>
<li>Ajout d'un podium à la fin de la partie avec possibilité de revenir au menu principal pour lancer une autre partie.</li>
<li>Chaque bouton du panneau de commandes est associé à un raccourci clavier.</li>
</ul>


## II.Architecture du code:
### Modèle:
La modélisation du jeu s'oriente sur trois axes principaux : le train, les personnes qui s'y trouvent et les différents butins récupérables. À cela s'ajoute un ensemble de classes qui permettent une meilleure interaction entre les éléments du modèle, mais aussi avec la vue, qui sont :
`Action` , `Joueur`, `Partie`.

#### 1. Train
Le `Train` est modélisé par une array de *`Wagon`*  qui sont de deux types : `Cabine` et `Locomotive`.

Un *`Wagon`* est composé d'une série de LinkedList qui réprésentent:
<ul>
<li>Les butins dropé sur le toit.</li>
<li>Les bandits se trouvant sur le toit.</li>
<li>Les Personnes se trouvant à l'interieur du Wagon.</li>
<li>Les butins dropé sur le sol.</li>
</ul>

En rétrospective, il aurait été préférable de créer une classe `PartieDuTrain` avec les sous-classes `Cabine`, `Locomotive` et `Toit`, avec le train défini comme une matrice de `PartieDuTrain`. Nous avons réalisé cela en passant à la partie graphique après avoir codé une grosse partie du modèle et avons donc fait le choix de rester sur l'architecture de base.

La `Locomotive` contient le **magot** (qui est unique au train) et a comme attribut un booléen qui indique sa disponibilité.

Le `Train` a également un acces directe au marshall en son sein via un attribut dedié , cela permet d'avoir rapidement sa position à chaque instant de la partie pour mieux gérer l'évenement de fuite des bandits entre manche.


#### 2. Personne et Interfaces associés

Une *`Personne`* peut être soit : un `Bandit` (Humain ou Bot) , un `Passager` ou un `Marshall`. Il est à noter qu'une *`Personne`* n'a pas acces a `Train` seuls les `Joueur` y ont accès. Ici , il faut voir les  *`Personne`* comme des pions sur le plateau ; ce ne sont pas les entités qui jouent au jeu à proprement parler.

`Bandit` et `Passager` implémente l'interface `Hitable`
`Bandit` et `Marshall` implémente l'interface `Movable`

La classe `Passager` n'est fondamentalment pas nécéssaire pour implémenté le jeu telle que présenté dans le sujet, on l'a fait au début parce que ça nous donnait plus de liberté pour implémenté plus de fonctionalités: résistance au braquage, des passagers qui se servent sur les butins dropés au sol , donner au marshall la possibilité de rendre les butins aux passagers, bref plein d'idée pour étoffer un peu le gameplay même si en l'état actuelle on en tire pas vraiment partie hormis le fait que cela facilite l'affichage des sprites.

La classe `Bandit` représente les agents principaux du modèle, comme ils implémente les deux interfaces ci-dessus ils peuvent se déplacer dans le train et être visé par des tires et peut effectuer les différentes actions du jeu: tir, déplacement , frappe , braquage.
La classe `Marshall` implémente `Movable` est peut donc se déplacer comme le bandit.<br><br>
Les *`Bandit_bot`* heritent tous de bandits et sont de 3 types: `Goblin` , `BloodThisrty` et `Random`, ceux-ci ont acces à toutes les actions que peut effectuer un bandit mais ont égalament une vision globale du train et ont un comportement spécifique.

#### 3. Action
La classe *`Action`* sert de conteneur pour les différentes actions que peut effectuer un bandit, toutes ses classes filles implémente la méthode `executer()` qui permet d'executer la dite action peut importe son type. Une *`Action`* a comme attribut le `Train` et l'`acteur` qui est un type générique (cela permetterait par exemple de rendre le marshall jouable).
Les classes filles sont: `Tir`, `Deplacement`, `Braquage`, `Frappe` dont le constructeur se résume à intialiser le train, l'acteur et éventuellement une direction et ou la fonction `executer()` appelle la bonne méthode dans le code de l'acteur.

#### 4. Joueur et Bot
La classe `Joueur` n'est la que pour donner la possibilité à un joueur de manipuler plusieurs bandits en même temps avec `List<Bandit> pions` et faire des parties "par équipes".L'inspiration venant du jeu de base ou une partie à deux joueurs se joue à 2 bandits chacun.On a néanmoins fait le choix de restraindre cette possibilité là pour des parties à 3 joueurs ou plus parce que le temps d'attente entre les tours de planification devienderait relativement lent.<br>
Joueur implémente l'interface `Comparable` suivant la valeur totale des butins amassés par ses bandits ce qui permet de trier plus simplement les collections de joueurs.



La classe `Bot` hérite de joueur et représente une IA dans le mode singlePlayer elle est quasiment identique à `Joueur` à ceci près  que ses pions sont uniquement des instances de classes filles de *`Bandit_Bot`*.

#### 5.Partie
Classe principale qui englobe toutes les composantes du modèle et qui interagit avec la vue. Elle contient l'ensemble des parametres de jeu, le `Train` et une array de `Joueur` et une serie d'attributs utiles soit pour le déroulement de la partie ou pour la communication avec la vue.
Un attribut important est `private Action[][] matrice_action;`, cette matrice est de taille *nombre_de_bandits\*DEFAULT HP* chaque ligne est associé à un bandit, celui-ci lors de la planification remplit sa ligne avec les actions qu'il va mener. Lors de l'execution la matrice est executé en colonne , on a donc toutes les premières actions qui vont s'éxecuter , les deuxièmes etc. Cela est implémenté dans `executer_matrice()`.<br>
La gestion du passage vers le joueur suivant/pions suivant/manche suivante et gérer par les fonctions `getNextPion()` de `Joueur` et `getNextJoueur()`.<br>
La classe contient également un main pour une verion du jeu totalement textuelle dans le terminale avec la méthode `run()` qui gère le déroulement  de la partie pour ce type d'affichage.<br>

#### 6.Butin et Direction
Deux classes de type enum étoffé d'un constructeur ce qui permet de récupérer des information sur ceux-ci comme la *valeur* ou le *sprite* pour les `Butins` et une valeur entière pour dir qui facilite l'arithmétique sur les positions.

### Vue:
La Vue va se décomposant en deux parties:
<ul>
<li> Un menu principale ou l'utilisateur peut initialiser les parametres de jeu gérer dans <code>VueInput</code>.</li>
<li>L'affichage concret du jeu qui à son tour se décompose en:</li>
    <ul>
    <li><code>VuePlateau</code>: Affiche le train et les différentes personnes à son bord</li>
    <li><code>VueCommande</code>: gère le panneau de commande avec les différent boutons qui permettent d'interagir avec le modèle mais aussi l'affichage de message contextuelle lors de l'éxecution.</li>
    </ul>
</ul>

#### 1. CVue
C'est la classe principale pour l'affichage graphique, contient le main qui lance le mode graphique (le menu principale est affiché en premier), contient un attribut pour chaque composante décrites ci-dessus et une méthode `switchToGame()` pour passer du menu principale au jeu.
#### 2. VueInput
Représente le menu principale du jeu ou l'utilisateur peut changer la valeur de 6 parametres: `NB_JOUEURS`, `NB_MANCHES`,`DEFAULT_HP`,`NB_MUNITIONS`, `DEFAULT_PRECISION` , `NEVROSITE_MARSHALL`.<br>
A la saisie du nombre de joueurs une grille de champs de texte apparait pour entrer les noms.
Le Bouton **PLAY** s'active quand tous les parametres sont initialisé à des valeurs valides.Ce dernier appelle `switchToGame()` qui créer une instance de `Partie`,`VueCommandes` et `VuePlateau`.
#### 3. VueCommandes
<span id = "anchor">Panneau</span> de commande, se situe en bas de l'écran, se décompose en:
<ul>
<li>HUD: affiche le numéro du joueur courant , le nom du bandit qu'il joue ainsi que ses stats (HP,argent,munitions).</li>
<li>Botons: qui a un <code>CardLayout</code> et donc contient deux `JPanel` un pour les boutons d'action, l'autre pour les boutons de directions et bascule de l'un à l'autre si besoin.</li>
<li>Prompt: Lors de la planification, affiche les actions qui ont été planifié par le bandit actuelle puis affiche les messages contextuelles lors de l'éxecution </li>
</ul>

**Note**: La grande majorité des `ActionListeners` sont implémenté par des lambdas expressions seuls les boutons d'action pour `Tir` et `Deplacement` ont un controleur à proprement parler.
#### 4. Vue Plateau
`JPanel` affiche le train en haut de l'écran. Contient les constantes liées aux dessins des différents éléments (dimensions, sprites, etc.) <br>. L'affichage de seulement quatre wagons au maximum sur une longueur d'écran (et l'ajout du déplacement de caméra) permet de garder une fenêtre lisible, même si le train devient très long.

#### 5. Podium
A la fin de la partie (lorsqu'on a atteint le nombre de manche imparti), `VueCommande` crée une instance de `Podium` qui à son initialisation lance une boite de dialogue ou sera visible le premier pion de chaque joueur avec leur score respectif (somme totale de butins sur tous les bandits) trié par ordre décroissant avec des podium pour les trois premiers.<br>
Un bouton *Menu Principale* permet de revnir au menu principale et de relancer une partie.
### Controleur:
Comme décrit dans <a href = #anchor>VueCommande</a>, celui-ci est utilisé que pour les actions qui nécéssite la spécification d'une direction étant donné que leur création se fait en deux temps: choix de tir ou de déplacement puis choix de la direction on a donc:<br>
<code><i>ControleurAction</i></code>: Classe abstraite qui gère le passage des boutons d'action au boutons de déplacement.Et deux classes filles: <code>ControleurTir, ControleurMouvement </code> qui set `actionChoisie` de partie à la bonne valeur (0->Deplacement, 1->Tir) avant de passer au panneau de directions pour set `directionChoisie`.

### Tests:


## III. Difficultés Rencontrés non éliminées
Arriver à gérer l'affichage le plus finement possible a sans doute été la principale difficulté rencontrée. Nous avons implémenté des solutions partielles, avec par exemple la méthode `update(String str)` qui permet déja une mise à jour spéciale de `VueCommande` lors du passage à l'exécution, il nous semble difficile de ne redessiner que ce qui a été modifié dans la fenêtre, sauf si`Personne` héritait d'`Observable` ou si nous ajoutions une quantité astronomique de booléens pour suivre les modifications, ce que nous avons essayé d'éviter de faire, sans grand succès<br>
Le choix de faire "coulisser" la caméra de droite à gauche pour `VuePlateau` compliquait également les choses, étant donné que cela ajoutait de "nouvelles raisons" de redessiner l'intégralité de la fenêtre.<br>
Ceci est aussi liéé à une compréhension plus que moyenne du fonctionnement de `repaint()` dans swing qui d'apres sa <a href ="https://docs.oracle.com/javase/8/docs/api/java/awt/Component.html#repaint--">doc</a> devrait immédiatement appeler `paintComponent()` ou `update()` alors que dans le code de `Conway.java` du cours (ou dans le nôtre) nous avons, par exemple, un appel un repaint() dans paintComponent(), qui, étrangement, ne crée pas de boucle infinie. Si on l'enlève, cela empêche la fenêtre de se mettre correctement à jour par moments.

## IV. Emprunt
Dans notre code il y a certaines fonctions qui ont comme commentaire `//emprunt` c'est plus simple que de dire qu'elles sont le résultat de nombreuses recherches dans des forums obscurs et d'adaptations à notre code. Cette indication est principalement présente dans la partie graphique, étant donné que c'était nouveau pour nous, il nous a donc fallu apprendre très vite sur le tas.<br>
Le seul "réel" emprunt qu'on a fait sans quasiment rien modifier est la classe `RoundedButton` qui nous permet de dessiner des boutons plus customisable.

## V.Répartition des tâches
Au début, nous visions un jeu classique, comme ceux des jeux de société. Cependant, en relisant le cahier des charges, nous avons compris que ce n'était pas l'attendu. Nous avons donc scindé le code en deux : un mode initial et un mode spécial avec différents bandits. Rayan a pris en charge les fonctions clés et tout l'affichage graphique, une contribution significative. Samy a récupéré les sprites. Antoine a été crucial pour la classe "passager", effectuant la majorité des tests, et a aussi apporté beaucoup à la classe "bandit". Il a également développé un mode solo pour offrir une option aux utilisateurs seuls. Quant aux tests, Samy s'en est occupé, sauf pour la classe "personne", gérée par Antoine.
