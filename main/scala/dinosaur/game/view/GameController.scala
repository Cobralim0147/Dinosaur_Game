package dinosaur.game.view

import dinosaur.game.GameLogic
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml
import scala.collection.mutable.ListBuffer
import scalafx.Includes._

@sfxml
class GameController(var gamePane: AnchorPane, val scoreLabel: Label) {
  private val ObstaclesImages = Map(
    "big-cactus1" -> "../img/dino/big-cactus1.png",
    "cactus1" -> "../img/dino/cactus1.png",
    "bird" -> "../img/dino/bird.gif",
  )

  private val BackgroundImages = Map(
    "cloud" -> "../img/dino/cloud.png",
    "bird" -> "../img/dino/bird1.png"
  )

  private def loadImage(path: String): Image = {
    val resourceStream = getClass.getResourceAsStream(path)
    if (resourceStream == null) {
      throw new IllegalArgumentException(s"Resource not found: $path")
    }
    new Image(resourceStream)
  }

  private val ObstImages: Map[String, Image] = ObstaclesImages.map { case (key, path) =>
    key -> loadImage(path)
  }

  private val BackImages: Map[String, Image] = BackgroundImages.map { case (key, path) =>
    key -> loadImage(path)
  }

  private var dinoRunning: ImageView = _
  private var obstacles: ListBuffer[ImageView] = ListBuffer()
  private var tracks: List[ImageView] = List()
  private var backgrounds: List[ImageView] = List()
  private var gameLogic: GameLogic = _

  initializeTrack()
  initializeDino()
  createBackground("cloud", fitWidth = 20, fitHeight = 10, bottomAnchor = 300)
  createObstacle("cactus1")
  createObstacle("big-cactus1")
  createObstacle("bird", fitWidth = 65, fitHeight = 50, bottomAnchor = 300)

  gameLogic = new GameLogic(dinoRunning, obstacles, scoreLabel, tracks)

  def initializeTrack(): Unit = {
    tracks = List.tabulate(2){ index =>
      new ImageView(new Image(getClass.getResourceAsStream("../img/flappy/base.png"))){
        fitWidth = 1000
        fitHeight = 15
        layoutY = 598
        layoutX = index * 1000
      }
    }
    tracks.foreach{ track =>
      gamePane.children.add(track)
      AnchorPane.setBottomAnchor(track, 55)
    }
  }

  def createBackground(backgroundKey: String, fitWidth: Double = 0, fitHeight: Double = 0, bottomAnchor: Double = 60): Unit = {
    val backgroundImage = BackImages.getOrElse(backgroundKey, throw new IllegalArgumentException(s"Invalid obstacle key: $backgroundKey"))
    val background = new ImageView(backgroundImage) {
      layoutX = 1000 + Math.random() * Math.random() * 10 * 641
    }

    // Add the obstacle to the obstacles list and to the gamePane
    backgrounds = backgrounds :+ background
    gamePane.children.add(background)
    AnchorPane.setBottomAnchor(background, bottomAnchor)
  }

  def initializeDino(): Unit = {
    dinoRunning = new ImageView (new Image(getClass.getResourceAsStream("../img/dino/dino-run.gif"))) {
      fitWidth = 70
      fitHeight = 90
      layoutX = 50
    }

    gamePane.children.add(dinoRunning)
    AnchorPane.setBottomAnchor(dinoRunning, 60)
  }

  def createObstacle(obstacleKey: String, fitWidth: Double = 0, fitHeight: Double = 0, bottomAnchor: Double = 60, minDistance: Double = 200): Unit = {
    val obstacleImage = ObstImages.getOrElse(obstacleKey, throw new IllegalArgumentException(s"Invalid obstacle key: $obstacleKey"))
    var newObstacleX = 1000 + Math.random() * 600
    var isTooClose = false

    // Check and adjust the position if it's too close to existing obstacles
    do {
      isTooClose = obstacles.exists { otherObstacle: ImageView =>
        Math.abs(otherObstacle.layoutX.value - newObstacleX) < minDistance
      }
      if (isTooClose) {
        newObstacleX = 1000 + Math.random() * 600 // Recalculate position
      }
    } while (isTooClose)

    val obstacle = new ImageView(obstacleImage) {
      layoutX = newObstacleX
    }

    // Add the obstacle to the obstacles list and to the gamePane
    obstacles = obstacles :+ obstacle
    gamePane.children.add(obstacle)
    AnchorPane.setBottomAnchor(obstacle, bottomAnchor)
  }

  def startGame(): Unit = {
    gameLogic.start()
  }

  def handleKeyPress(keyCode: KeyEvent): Unit = {
    keyCode.code match {
      case KeyCode.Space => gameLogic.handleSpaceBar()
      case _ =>
    }
  }
}