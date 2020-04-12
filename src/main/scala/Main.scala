import models.DiscUtils._
import models.{AI, Board}
import types.Types.Disc

import scala.io.AnsiColor

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

  def joueCoupHumain(plateau: Board, colonne: Int, couleurHumain: Disc): Board =
    b.dropDisc(colonne, couleurHumain)

  /**
    * Ask the user for a column and return it
    *
    * @param b   the connect4 board
    * @param max the max value allowed
    * @return a usable value from the user
    */
  def userInput(b: Board, max: Int): Int = {
    println(s"Colonne (0-${max - 1}) : ")
    try {
      scala.io.StdIn.readInt match {
        case c if b.canDropDisc(c) => c
        case _ => userInput(b, max)
      }
    } catch {
      case e: NumberFormatException =>
        println("Entrée invalide"); userInput(b, max)
    }
  }

  val nbLignes = 6
  val nbColonnes = 7

  /* Import example */
  /*
  val chaine =
  """
  | JRJ RJ
  |JJJRJRR
  """.stripMargin
  var b = importPlateau(chaine)
  */

  var grid = Vector.fill[Option[Disc]](nbColonnes, nbLignes)(None)
  var b = Board(nbLignes, nbColonnes, grid)

  /* Play game */
  while (!b.isWon && !b.isFull) {
    println(b)
    b = joueCoupHumain(b, userInput(b, nbColonnes), HUMAN)
    if (!b.isWon && !b.isFull) b = joueCoupOrdi(b, COMPUTER)
  }
  /* Print final board */
  println(b.getWinnerHighlight)
  print(getColorFromDisc(b.winnerDisc))
  b.winnerDisc match {
    case HUMAN => print("Victoire")
    case COMPUTER => print("Défaite")
  }
  println(AnsiColor.RESET)

  /* Print the exported board */
  println(exportPlateau(b))
}
