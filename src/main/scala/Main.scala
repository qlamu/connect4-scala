import models.{Joueur, Board, AI}
import models.DiscUtils._
import types.Types.Disc
import scala.io.AnsiColor
import scala.util.Random

object ConnectFour extends App {

  /**
    * Create a board from a multiline string
    * J : Human player
    * R : Computer player
    *
    * @param chaine a multiline string containing R and J
    * @return a filled board
    */
  def importPlateau(chaine: String): Board = {
    val lignes = chaine.split("\n").toSeq.filter(_ != "")
    val grid = Vector.fill[Option[Disc]](nbColonnes, nbLignes)(None)
    var b = Board(6, 7, grid)

    lignes.reverse foreach { l =>
      l.toList.zipWithIndex.foreach({ item =>
        item._1 match {
          case 'J' => b = b.dropDisc(item._2, HUMAN)
          case 'R' => b = b.dropDisc(item._2, COMPUTER)
          case _ => 
        }
      })
    }

    b
  }

  def exportPlateau(plateau: Board): String = plateau.export()

  def joueCoupOrdi(plateau: Board, couleurOrdi: Disc): Board = {
    val computerPlayer = AI(plateau)

    val colonne = computerPlayer.getBestCol()
    plateau.dropDisc(colonne, couleurOrdi)
  }

  def joueCoupHumain(plateau: Board,colonne: Int,couleurHumain: Disc): Board =
    b.dropDisc(colonne, couleurHumain)

  def userInput(p: Board, max: Int): Int = {
    println(s"Colonne (0-${max - 1}) : ")
    try {
    scala.io.StdIn.readInt match {
      case c if b.canDropDisc(c) => c
      case _                     => userInput(p, max)
    }
   } catch {
      case e: NumberFormatException =>
        println("Entrée invalide"); userInput(p, max)
    }
  }

  val nbLignes = 6
  val nbColonnes = 7

  val chaine =
  """
  | JRJ RJ
  |JJJRJRR
  """.stripMargin
  var b = importPlateau(chaine)

  //var grid = Vector.fill[Option[Disc]](nbColonnes, nbLignes)(None)
  //var b = Board(nbLignes, nbColonnes, grid)



  while (!b.isWon && !b.isFull) {
    println(b)
    b = joueCoupHumain(b, userInput(b, nbColonnes), HUMAN)
    if (!b.isWon && !b.isFull) b = joueCoupOrdi(b, COMPUTER)
  }
  println(b.getWinnerHighlight)

  print(getColorFromDisc(b.winnerDisc))
  b.winnerDisc match {
    case HUMAN    => print("Victoire")
    case COMPUTER => print("Défaite")
  }
  println(AnsiColor.RESET)

  println(exportPlateau(b))
}
