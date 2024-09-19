package dinosaur.game

import dinosaur.game.model.Person
import dinosaur.game.util.SoundManager
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.application.Platform
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{ButtonType, Label, TextInputDialog}
import scalafx.scene.image.ImageView
import scalafx.util.Duration
import scala.Console.println
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GameLogic(dino: ImageView, obstacles: ListBuffer[ImageView], scoreLabel: Label, backgrounds: List[ImageView]) {
  private var running = false
  private var score = 0
  private var gameSpeed = 5.0
  private var isJumping = false
  private var isDucking = false

  private val gameLoop = AnimationTimer { _ => update() }
  private val jumpTimeLine = new Timeline {
    cycleCount = 1
    autoReverse = true
    keyFrames = Seq(
      KeyFrame(Duration.ZERO, onFinished = _ => dino.translateY = -100),
      KeyFrame(Duration(500), onFinished = _ => {
        dino.translateY = dino.layoutY.value - 375
        isJumping = false
      })
    )
  }

  def saveScore(): Unit = {
    Platform.runLater {
      val dialog = new TextInputDialog("Player")
      dialog.setTitle("Game Over")
      dialog.setHeaderText("Your Score: " + score)
      dialog.setContentText("Please enter your name: ")

      // Remove the default cancel button
      dialog.getDialogPane.getButtonTypes.remove(ButtonType.Cancel)

      // Create a custom "Play Again" button
      val playAgainButtonType = new ButtonType("Play Again", ButtonData.CancelClose)
      dialog.getDialogPane.getButtonTypes.add(playAgainButtonType)

      val result = dialog.showAndWait()

      result match {
        case Some(name) if name.nonEmpty =>
          Future {
            val person = new Person(name, score.toString)
            person.save()
          }.onComplete { _ =>
            Platform.runLater {
              MainGame.showScoreBoard()
            }
          }
        case _ =>
          // Handle "Play Again" or empty name
          Platform.runLater {
            // Call your method to restart the game
            MainGame.showGameView()
          }
      }
    }
  }

  def handleSpaceBar(): Unit = {
    if (!running) start()
    else if (running) dinoJump()
  }

  def start(): Unit = {
    running = true
    score = 0
    updateScore()
    gameLoop.start()
    SoundManager.stopScoreBoardMusic()
    SoundManager.playBackgroundMusic()
  }

  def stop(): Unit = {
    running = false
    stopGameLoop()
    SoundManager.stopBackgroundMusic()
    saveScore()
  }

  def stopGameLoop(): Unit = {
    if(gameLoop!= null){
      gameLoop.stop()
      println("Gameloop stopped")
    }else{
      println("Gameloop Running")
    }
  }

  def moveBackground(): Unit = {
    backgrounds.foreach{ background =>
      background.layoutX = background.layoutX.value - gameSpeed
      if( background.layoutX.value <= -1000){
        background.layoutX = backgrounds.map(_.layoutX.value).max + 1000
      }
    }
  }

  def moveObstacles(): Unit = {
    obstacles.foreach { obstacle =>
      if(obstacle != null){
        obstacle.layoutX = obstacle.layoutX.value - gameSpeed

        if (obstacle.layoutX.value + obstacle.fitWidth.value < 0) {
          var newPosition = 1000 + Math.random() * 1000
          var isTooClose = false

          do{
            isTooClose = obstacles.exists(otherObstacle =>
              otherObstacle != obstacle &&
              Math.abs(otherObstacle.layoutX.value - newPosition) < 200
            )
            if(isTooClose){
              newPosition = 1000 + Math.random() * 1000
            }
          } while (isTooClose)
          obstacle.layoutX = newPosition
        }
      }
    }
  }

  def dinoJump(): Unit = {
    if (!isJumping && !isDucking) {
      isJumping = true
      jumpTimeLine.playFromStart()
    }
  }

  def checkCollision(): Boolean = {
    obstacles.exists{ obstacle =>
      val dinoBox = dino.boundsInParent.value
      val obstacleBox = obstacle.boundsInParent.value
      val horizontalOverlap = dinoBox.getMaxX > obstacleBox.getMinX && dinoBox.getMinX < obstacleBox.getMaxX
      val verticalOverlap = dinoBox.getMaxY > obstacleBox.getMinY && dinoBox.getMinY < obstacleBox.getMaxY

      horizontalOverlap && verticalOverlap
    }
  }

  def updateScore(): Unit = {
    scoreLabel.text = s"Score : $score"
  }

  def update(): Unit = {
    if (running) {
      moveObstacles()
      moveBackground()

      if (checkCollision()) {
        stop()
      }
      score += 1
      updateScore()
      gameSpeed += 0.001
    }
  }
}