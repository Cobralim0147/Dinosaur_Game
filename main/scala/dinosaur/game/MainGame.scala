package dinosaur.game

import dinosaur.game.util.{Database, SoundManager}
import dinosaur.game.view.GameController
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.scene.Scene
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.{AnchorPane, BorderPane, Pane}
import scalafx.Includes._
import scalafx.scene.text.Font


object MainGame extends JFXApp {
  Font.loadFont(getClass.getResourceAsStream("fonts/PressStart2P-Regular.ttf"), 9)
  Database.setupDB()
  private var rootLayout: jfxs.layout.BorderPane = _
  private var gameController: GameController#Controller = _
  SoundManager.initialize("../audio/cyhtm.mp3", "../audio/scoreboard.mp3")

  stage = new PrimaryStage{
    title = "Dinosaur Game"
    width = 1000
    height = 600
    resizable = false
  }

  def initializeRootLayout() {
    val rootResource = getClass.getResource("view/RootLayout.fxml")
    if (rootResource == null) {
      throw new IllegalArgumentException("FXML resource not found: view/RootLayout.fxml")
    }
    val loader = new FXMLLoader(rootResource, NoDependencyResolver)
    rootLayout = loader.load[jfxs.layout.BorderPane]()
    val scalaFXRootLayout = new BorderPane(rootLayout)

      stage.scene = new Scene(scalaFXRootLayout) {
        stylesheets += getClass.getResource("css/theme.css").toString
        onKeyPressed = (event: KeyEvent) => {
          if (gameController != null) {
            gameController.handleKeyPress(event)
          }
        }
      }
    }

  def showMainGame(): Unit = {
    val resource = getClass.getResource("view/StartPage.fxml")
    if (resource == null) {
      throw new IllegalArgumentException("FXML resource not found: /dinosaur/game/view/StartPage.fxml")
    }
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val mainPage = loader.load[jfxs.layout.AnchorPane]
    rootLayout.setCenter(mainPage)
  }

  def showGameView(): Unit = {
    val resource = getClass.getResource("view/GameView.fxml")
    if (resource == null) {
      throw new IllegalArgumentException("FXML resource not found: /dinosaur/game/view/GameView.fxml")
    }
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val gameView = loader.load[jfxs.layout.AnchorPane]()
    gameController = loader.getController[GameController#Controller]
    rootLayout.setCenter(gameView)
    gameController.startGame()
  }

  def showScoreBoard(): Unit = {
    val resource = getClass.getResource("view/ScoreBoard.fxml")
    if (resource == null) {
      throw new IllegalArgumentException("FXML resource not found: /dinosaur/game/view/ScoreBoard.fxml")
    }
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val mainPage = loader.load[jfxs.layout.AnchorPane]
    rootLayout.setCenter(mainPage)
    SoundManager.playScoreBoardMusic()
  }

  try{
    initializeRootLayout()
    showMainGame()
  } catch {
    case e: Exception =>
      e.printStackTrace()
  }

}
