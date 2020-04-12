package models
import types.Types.Disc
import models.DiscUtils.{HUMAN, COMPUTER}

case class AI(b: Board) {

  /**
    * Return the row of the highest disc in the column
    *
    * @param b the board to check
    * @param c the column
    * @return the row of the highest disc in the column
    */
  def getRowForLastDiscInCol(b: Board, c: Int): Int = {
    /* -1 cause we start at 0 and -1 cause we don't count the last added */
    b.nbRows - 1 - b.grid(c).filter(_.isDefined).length + 1
  }

  /**
    * Get a score for a position depending on the number of discs
    * that are aligned
    *
    * @param nbAligned nb of discs that are aligned
    * @return a score
    */
  def getScore(nbAligned: Int): Int = {
    nbAligned match {
      case 1           => 1
      case 2           => 5
      case 3           => 20
      case n if n >= 4 => 100
      case _           => 0
    }
  }

  /**
    * Very simple 'AI', returns the best column in the ones available
    * according to its own number of aligned items and the number of 
    * aligned items of the opponent to counter him if necessary
    *
    * @param d disc of the current player
    * @return
    */
  def getBestCol(): Int = {
    var res = Array.fill[Int](b.nbCols)(0)
    0 until b.nbCols foreach { c =>
      if (b.canDropDisc(c)) {
        val tmp1 = b.dropDisc(c, COMPUTER)
        val tmp2 = b.dropDisc(c, HUMAN)
        val r = getRowForLastDiscInCol(tmp1, c)
        if (r < b.nbRows) {
          res(c) += getScore(tmp1.nbAlignedWithDiscVert(r, c, COMPUTER)) + getScore(
            tmp2.nbAlignedWithDiscVert(r, c, HUMAN)
          )
          res(c) += getScore(tmp1.nbAlignedWithDiscHorz(r, c, COMPUTER)) + getScore(
            tmp2.nbAlignedWithDiscHorz(r, c, HUMAN)
          )
          res(c) += getScore(tmp1.nbAlignedWithDiscDiagTL(r, c, COMPUTER)) + getScore(
            tmp2.nbAlignedWithDiscDiagTL(r, c, HUMAN)
          )
          res(c) += getScore(tmp1.nbAlignedWithDiscDiagTR(r, c, COMPUTER)) + getScore(
            tmp2.nbAlignedWithDiscDiagTR(r, c, HUMAN)
          )
        }
      }
    }
    println(res.toList)
    res.zipWithIndex.maxBy(_._1)._2
  }

}
