package dinosaur.game.view

import dinosaur.game.MainGame
import scalafxml.core.macros.sfxml

@sfxml
class StartPageController() {

  def getScoreBoard(): Unit = {
    MainGame.showScoreBoard()
  }

  def getStartGame(): Unit = {
    MainGame.showGameView()
  }
}