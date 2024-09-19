package dinosaur.game.view

import dinosaur.game.model.Person
import dinosaur.game.MainGame
import scalafxml.core.macros.sfxml
import scalafx.scene.control.Label

@sfxml
class ScoreBoardController(
                            private val firstPlaceUsername: Label,
                            private val firstPlaceScore: Label,
                            private val secondPlaceUsername: Label,
                            private val secondPlaceScore: Label,
                            private val thirdPlaceUsername: Label,
                            private val thirdPlaceScore: Label
                          ) {

  showTopScores()

  def getMainGame(): Unit = {
    MainGame.showMainGame()
  }

  private def showTopScores(): Unit = {
    val topScores = Person.getTopScores(3)

    topScores.zipWithIndex.foreach {
      case (person, 0) =>
        firstPlaceUsername.text <== person.username
        firstPlaceScore.text <== person.score
      case (person, 1) =>
        secondPlaceUsername.text <==  person.username
        secondPlaceScore.text <==  person.score
      case (person, 2) =>
        thirdPlaceUsername.text <==  person.username
        thirdPlaceScore.text <==  person.score
      case _ => // Do nothing for other indices
    }
  }
}