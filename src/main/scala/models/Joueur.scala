package models

import types.Types.Disc
import scala.io.AnsiColor

case class Joueur(nom: String, couleur: String, symbole: Char) {
  def getDisc(): Disc = {
    s"${couleur}${symbole}${AnsiColor.RESET}"
  }

  override def toString(): String = {
    s"${couleur}${symbole} : ${nom}${AnsiColor.RESET}"
  }
}