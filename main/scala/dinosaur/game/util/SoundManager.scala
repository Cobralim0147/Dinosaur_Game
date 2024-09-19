package dinosaur.game.util
import scalafx.scene.media.{Media, MediaPlayer}


object SoundManager {
  var backgroundMusicPlayer: MediaPlayer = _
  var scoreBoardMusicPlayer: MediaPlayer = _

  def initialize(backgroundMusicPath: String, scoreBoardMusicPath: String): Unit = {
    try {
      backgroundMusicPlayer = createMediaPlayer(backgroundMusicPath)
      scoreBoardMusicPlayer = createMediaPlayer(scoreBoardMusicPath)

      backgroundMusicPlayer.setCycleCount(MediaPlayer.Indefinite)
    } catch {
      case e: Exception =>
        println(s"Error loading sounds: ${e.getMessage}")
    }
  }

  private def createMediaPlayer(path: String): MediaPlayer = {
    new MediaPlayer(new Media(getClass.getResource(path).toString))
  }

  def playBackgroundMusic(): Unit = playAudio(backgroundMusicPlayer)
  def stopBackgroundMusic(): Unit = stopAudio(backgroundMusicPlayer)
  def playScoreBoardMusic(): Unit = playAudio(scoreBoardMusicPlayer)
  def stopScoreBoardMusic(): Unit = stopAudio(scoreBoardMusicPlayer)


  private def playAudio(player: MediaPlayer): Unit = if (player != null) player.play()
  private def stopAudio(player: MediaPlayer): Unit = if (player != null) player.stop()

}


