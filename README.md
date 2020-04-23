# Projet Scala : Puissance 4

**Environnement**  : Ubuntu 18.04  |  Scala 2.11.12  |  sbt 1.3.2

## Fonctions minimum requises

**importPlateau** : L'import de plateau fonctionne pour les plateaux de taille standard, 6x7. La fonction retourne un plateau de jeu. Les chaines de caractères doivent être proprement formées avec des `|` et l'utilisation de `stripmargin`, exemple :

```scala
val chaine =
  """
  | JRJ RJ
  |JJJRJRR
  """.stripMargin
var b = importPlateau(chaine)
```

L'ajout des disques se fait comme s'ils avaient été joués, c'est à dire qu'une chaine avec un disque au dessus d'un espace vide retournera une grille corrigée. Si la chaine est incorrecte (trop de lignes ou de colonnes) la fonction affiche un message d'erreur et retourne une grille vide.

**exportPlateau** : L'export du plateau fonctionne pour n'importe quelle taille de plateau, et est implémentée dans la classe `Board` représentant le plateau, les disques du joueur sont représentés par `J` et ceux de l'ordinateur par `R`. La chaîne exportée contient toutes les lignes, y compris les lignes vides. Pour obtenir cette sortie, on se contente de parcourir la grille et pour chaque case d'ajouter 'J', 'R' ou ' ' selon ce qu'elle contient.

**joueCoupOrdi** : Dans cette fonction, on commence par créer un objet de type `AI` puis on récupère la colonne à jouer en appelant un algorithme de la classe, ici le seul présent est `getBestCol` décrit plus bas. Avec cette implémentation, on pourrait aisément écrire plusieurs algorithmes pour l'ordinateur et demander à l'utilisateur lequel utiliser en ajoutant un paramètre à la fonction.

**joueCoupHumain** : La fonction elle même est très simple, elle prends en arguments un plateau, une colonne et un disque et se contente d'ajouter le disque au plateau dans la colonne spécifiée, c'est un simple alias de la fonction `dropDisc` de la classe `Board` qui va calculer la ligne à laquelle ajouter le disque puis retourner un nouveau plateau avec la grille mise à jour. Dans la boucle principale du programme, cette fonction est appelée avec `userInput` qui récupère un numéro de colonne depuis l'entrée utilisateur.

La fonction `userInput` est un match sur `scala.io.StdIn.readInt`, si on peut ajouter un disque dans la colonne spécifiée, la colonne est retournée sinon on appelle `userInput` à nouveau.

## Fonctionnement général

L'intégralité des fonctions à l'exception de `importPlateau`  fonctionnent pour n'importe quelle taille de grille, on noteras toutefois quelques soucis d'alignement des numéro de colonnes dans la fonction `Board.toString`. Le joueur est représenté par un disque jaune et l'ordinateur par un disque rouge. Ils jouent tour à tour jusqu'à ce que la partie soit gagnée ou que la grille soit pleine.

### Utilisation

**Exécutable JAR** : Le programme se lance comme suit : `scala connect4.jar`, la partie commence tout de suite, il vous sera demandé une colonne, puis l'ordinateur joue, etc... Le programme ayant été généré avec et pour Scala 2.11.12, il est possible qu'il ne fonctionne pas avec toutes les versions de Scala.

**SBT** : Depuis le répertoire du projet (au même niveau que le fichier build.sbt), lancer `sbt` puis `run` dans la console de sbt.

**Note pour SBT** : Pour une raison inconnue, l'entrée d'une colonne dans `userInput` ne s’arrête jamais dans sbt, même après un `ctrl+c`, ce problème n'est pas présent dans la version packagée.

### Les éléments du jeu

Dans le code, les variables nommées r et c représentent toujours respectivement ligne et colonne.

**Disc** : C'est le type utilisé pour les disques, qui est un alias de `String`, pour gérer la couleur des disques il a été choisit d'utiliser `scala.io.AnsiColor`, une bibliothèque d'alias pour les codes couleur ANSI.

**DiscUtils** : Les deux disques par défaut `HUMAN` et `COMPUTER` ainsi que quelques fonctions permettant de faciliter la manipulation des disques :

- `getDiscOfColor(color: String): Disc` : retourne un nouveau disque ayant la couleur spécifiée.

- `getWinnerDisc(disc: Disc): Disc` : retourne le disque entré en paramètre avec une surcouche visuelle pour mieux le différencier des autres.

- `getColorFromDisc(disc: Disc): String` : retourne la couleur d'un disque.

**Board** : C'est la classe définissant le plateau, elle s'initialise avec un nombre de colonnes `nbRows`, un nombre de lignes `nbCols` et une grille de type `Vector[Vector[Option[Disc]]]`elle contient toutes les fonctions nécessaires au déroulement du jeu :

- `canDropDisc(c: Int): Boolean` : vérifie si on peut ajouter ou non un disque à la colonne `c`.

- `dropDisc(c: Int, d: Disc): Board` : ajoute un disque `d` à la colonne `c` et retourne un nouveau plateau de jeu contenant les nouveaux changements.

- `lazy val isWon: Boolean` : vérifie pour tous les disques si ils sont alignés avec au moins trois autres disques de même couleur. Si c'est le cas, on ajoute les coordonnées de ce disque à la liste des disques gagnant `winnerCoord`, et on définit le disque gagnant sur cette couleur. Puis on retourne si cette liste de coordonnées est vide. Cette liste servira pas la suivre à mettre en valeur la droite gagnante en fin de partie. 

- `lazy val getWinnerHighlight: Board` : modifie tous les disques de la liste `winnerCoord` avec `DiscUtils.getWinnerDisc` et retourne un nouveau plateau mis à jour.

- `lazy val isFull: Boolean` : Variable calculée à la demande pour savoir si la grille est pleine ou non. 

- `nbAlignedWithDisc(r: Int, c: Int, d: Disc, stepR: Int, stepC: Int)` : Calcule le nombre de disque alignés avec un disque initial d ayant pour coordonnées (c, r) sur une droite ayant les coefficients de déplacement stepR (ligne) et stepC (colonne).
  
  - `nbAlignedWithDiscVert(r: Int, c: Int, d: Disc)` :   alias de `nbAlignedWithDisc` pour une droite verticale
  
  - `nbAlignedWithDiscHorz` : idem droite horizontale
  
  - `nbAlignedWithDiscDiagTL` : idem première diagonale (depuis haut gauche)
  
  - `nbAlignedWithDiscDiagTR` : idem seconde diagonale (depuis haut droite)

- `lazy val export: String` : retourne la grille sous forme de chaine de caractère multiligne, avec 'J' pour un disque du joueur, 'R' pour l'ordinateur ou ' ' pour une case vide.

- `def toString(): String` : pour afficher la grille, le numéro des colonnes est aussi affiché sous chacune d'elles.

Quelques variable lazy ont ici été préférées à des fonctions car on ne peut pas mettre à jour le plateau, donc les valeurs retournées par ces variables ne changent jamais après avoir été calculées.

**AI** : C'est la classe pour le joueur ordinateur, elle contient tout ce dont à besoin l'ordinateur pour choisir une colonne à jouer :

- `getRowForLastDiscInCol(b: Board, c: Int): Int` : retourne l'index dans la colonne c pour le dernier disque joué.

- `getScore(nbAligned: Int): Int` : retourne un score pondéré par le nombre de disques alignés.

- `getBestCol(): Int` : simple IA retournant la meilleure colonne à jouer sans prendre en compte les tours au delà de l'actuel.

## IA

L'IA implémentée est très simple, il n'y a pas de notion de profondeur, seul le niveau actuel est considéré.
Dans cette IA, on crée un tableau de scores de taille égale au nombre de colonnes du plateau, et chaque élément du tableau contient le score attribué à une colonne. Le score est pondéré en fonction du nombres de disques alignés lorsqu'on ajoute un nouveau disque à une colonne, plus il y a de disque alignés, plus la position est intéressante. Le score dépends du disque `COMPUTER` mais aussi du disque `HUMAN` au même niveau, afin de pouvoir contrer efficacement le joueur réel lorsqu'il tente d'aligner ses disques.
Compte tenu de sa simplicité, l'IA se débrouille plutôt bien mais ses comportements sont prévisibles, et c'est la dessus que doit jouer le joueur.