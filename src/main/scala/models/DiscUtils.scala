package models

import types.Types.Disc

import scala.io.AnsiColor

object DiscUtils {
  /* Default discs */
  val HUMAN = getDiscOfColor(AnsiColor.BOLD + AnsiColor.YELLOW)
  val COMPUTER = getDiscOfColor(AnsiColor.BOLD + AnsiColor.RED)

  /**
    * Create a disc with a specified color from AnsiColor
    *
    * @param color the color of the disc
    * @return a colored disc
    */
  def getDiscOfColor(color: String): Disc = s"${color}O${AnsiColor.RESET}"

  /**
    * Returns a winner version of the disc for easy differentiation
    *
    * @param disc the original disc
    * @return a blinking version of a disc
    */
  def getWinnerDisc(disc: Disc): Disc = AnsiColor.BLINK + disc

  /**
    * Retrieve the color of the disc
    *
    * @param disc
    * @return the color from the disc
    */
  def getColorFromDisc(disc: Disc): String = disc.stripSuffix(s"O${AnsiColor.RESET}")
}
