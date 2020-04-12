package models

import models.DiscUtils.{COMPUTER, HUMAN}
import types.Types.Disc

case class Board(
                  nbRows: Int,
                  nbCols: Int,
                  grid: Vector[Vector[Option[Disc]]]
                ) {
  var winnerCoord: Vector[(Int, Int)] = Vector.empty
  var winnerDisc: Disc = ""

  /**
    * Check if nodes are availables on a specified column
    *
    * @param c the column of the grid to check
    * @return if there is space in the column
    */
  def canDropDisc(c: Int): Boolean =
    (0 until nbCols contains c) && grid(c).contains(None)

  /**
    * Add a disc in a specified column in the grid
    *
    * @param c the column where to drop the disc
    * @param d the disc to drop
    * @return
    */
  def dropDisc(c: Int, d: Disc): Board = {
    val r = nbRows - 1 - grid(c).filter(_.isDefined).length
    val newGrid = grid.updated(c, grid(c).updated(r, Option(d)))
    Board(nbRows, nbCols, newGrid)
  }

  /**
    * Check every disc on the grid to check if the game is won
    *
    * @return if the game is won
    */
  def isWon(): Boolean = {
    0 until nbCols foreach { c =>
      0 until nbRows foreach { r =>
        val d = grid(c)(r)
        if (d.isDefined && (
          nbAlignedWithDiscVert(r, c, d.get) >= 4 ||
            nbAlignedWithDiscHorz(r, c, d.get) >= 4 ||
            nbAlignedWithDiscDiagTL(r, c, d.get) >= 4 ||
            nbAlignedWithDiscDiagTR(r, c, d.get) >= 4
          )) {
          winnerCoord :+= (c, r)
          winnerDisc = d.get
        }
      }
    }
    winnerCoord.nonEmpty
  }

  /**
    * Highlight the winning line, used at the end of the game
    *
    * @return the board with highlighted winning line
    */
  def getWinnerHighlight(): Board = {
    var newGrid = grid
    val w_disc = DiscUtils.getWinnerDisc(winnerDisc)
    winnerCoord foreach { coord =>
      newGrid = newGrid.updated(coord._1, newGrid(coord._1).updated(coord._2, Option(w_disc)))
    }
    Board(nbRows, nbCols, newGrid)
  }

  lazy val isFull: Boolean = grid.filter(r => r.contains(None)).isEmpty

  /**
    * Recursive function to check the number of discs of a specified color
    * that are aligned on a specified line.
    *
    * @param r     initial row of a disc
    * @param c     initial column of a disc
    * @param d     disc to count
    * @param stepR the displacement on the row axis at every iteration
    * @param stepC the displacement on the column axis at every iteration
    * @return the number of aligned discs with the one initially supplied,
    *         itself included
    */
  private def nbAlignedWithDisc(
                                 r: Int,
                                 c: Int,
                                 d: Disc,
                                 stepR: Int,
                                 stepC: Int
                               ): Int = {
    if (r >= nbRows || r < 0 || c >= nbCols || c < 0 || grid(c)(r).isEmpty) 0
    else {
      grid(c)(r) match {
        case Some(x) if x == d =>
          1 + nbAlignedWithDisc(r + stepR, c + stepC, d, stepR, stepC)
        case _ => 0
      }
    }
  }

  def nbAlignedWithDiscVert(r: Int, c: Int, d: Disc): Int =
    nbAlignedWithDisc(r, c, d, 1, 0) + nbAlignedWithDisc(r, c, d, -1, 0) - 1

  def nbAlignedWithDiscHorz(r: Int, c: Int, d: Disc): Int =
    nbAlignedWithDisc(r, c, d, 0, 1) + nbAlignedWithDisc(r, c, d, 0, -1) - 1

  def nbAlignedWithDiscDiagTL(r: Int, c: Int, d: Disc): Int =
    nbAlignedWithDisc(r, c, d, 1, 1) + nbAlignedWithDisc(r, c, d, -1, -1) - 1

  def nbAlignedWithDiscDiagTR(r: Int, c: Int, d: Disc): Int =
    nbAlignedWithDisc(r, c, d, 1, -1) + nbAlignedWithDisc(r, c, d, -1, 1) - 1

  /**
    * Export the grid in minimal text :
    * "R" for a red disc
    * "J" for a yellow disc
    * " " for no disc
    * an empty line for every empty rows in the grid
    *
    * @return the grid as a multiline string
    */
  def export(): String = {
    var res = ""
    0 until nbRows foreach { r =>
      0 until nbCols foreach { c =>
        res += (grid(c)(r) match {
          case Some(HUMAN) => "J"
          case Some(COMPUTER) => "R"
          case _ => " "
        })
      }
      res += "\n"
    }
    res
  }

  override def toString(): String = {
    var res = "┌"
    1 until nbCols * 2 foreach { _ => res += "─" }
    res += "┐\n"
    0 until nbRows foreach { l =>
      0 until nbCols foreach { c => res += "│" + grid(c)(l).getOrElse(" ") }
      res += "│\n"
    }
    res += "└"
    1 until nbCols * 2 foreach { _ => res += "─" }
    res += "┘\n"
    0 until nbCols foreach { x => res += s" ${x}" }
    res += "\n"
    res
  }

}
