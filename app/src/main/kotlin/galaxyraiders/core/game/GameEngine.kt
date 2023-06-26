package galaxyraiders.core.game

import galaxyraiders.Config
import galaxyraiders.ports.RandomGenerator
import galaxyraiders.ports.ui.Controller
import galaxyraiders.ports.ui.Controller.PlayerCommand
import galaxyraiders.ports.ui.Visualizer
import kotlin.system.measureTimeMillis
import java.io.File
import com.beust.klaxon.Klaxon
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import java.io.StringReader

const val MILLISECONDS_PER_SECOND: Int = 1000

object GameEngineConfig {
  private val config = Config(prefix = "GR__CORE__GAME__GAME_ENGINE__")

  val frameRate = config.get<Int>("FRAME_RATE")
  val spaceFieldWidth = config.get<Int>("SPACEFIELD_WIDTH")
  val spaceFieldHeight = config.get<Int>("SPACEFIELD_HEIGHT")
  val asteroidProbability = config.get<Double>("ASTEROID_PROBABILITY")
  val coefficientRestitution = config.get<Double>("COEFFICIENT_RESTITUTION")

  val msPerFrame: Int = MILLISECONDS_PER_SECOND / this.frameRate
}

@Suppress("TooManyFunctions")
class GameEngine(
  val generator: RandomGenerator,
  val controller: Controller,
  val visualizer: Visualizer,
) {

  val field = SpaceField(
    width = GameEngineConfig.spaceFieldWidth,
    height = GameEngineConfig.spaceFieldHeight,
    generator = generator
  )
  
  var scoreboardFile = File("src/main/kotlin/galaxyraiders/core/score/Scoreboard.json")
  var leaderboardFile = File("src/main/kotlin/galaxyraiders/core/score/Leaderboard.json")
  var scoreboardJson = JsonObject()
  var leaderboardJson = JsonObject()
  var status = GameStatus()
  var playing = true 

  fun execute() {

    while (true) {
      this.checkBoardsExistence()
      this.updateScores()
      val duration = measureTimeMillis { this.tick() }
      
      Thread.sleep(
        maxOf(0, GameEngineConfig.msPerFrame - duration)
      )
    }
  }

  fun execute(maxIterations: Int) {
    repeat(maxIterations) {
      this.tick()
    }    
  }
  
  fun updateScores(){
    this.scoreboardJson[this.status.startTime] = Klaxon().parseJsonObject(StringReader(this.status.toJson())) 
    this.scoreboardFile.writeText(this.scoreboardJson.toJsonString(prettyPrint = true))
    
    val sorted = this.scoreboardJson.toSortedMap(compareByDescending { this.scoreboardJson.obj(it)?.int("score") })
    val leaders = sorted.toList().take(3)
    this.leaderboardJson.clear()
    this.leaderboardJson.putAll(leaders)
    this.leaderboardFile.writeText(this.leaderboardJson.toJsonString(prettyPrint = true))
    
  }

  fun checkBoardsExistence(){
    if (!this.scoreboardFile.exists()){ //if there's no scoreboard, leaderboard is usefull
      this.scoreboardFile.createNewFile()
      this.leaderboardFile.createNewFile()
    }
    if (!this.leaderboardFile.exists()) this.leaderboardFile.createNewFile()

    if (this.scoreboardFile.readText().isEmpty()) this.scoreboardFile.writeText("{}")
    if (this.leaderboardFile.readText().isEmpty()) this.leaderboardFile.writeText("{}")
    
    this.scoreboardJson = Klaxon().parseJsonObject(this.scoreboardFile.reader())
    this.leaderboardJson = Klaxon().parseJsonObject(this.leaderboardFile.reader())

  }

 
  fun tick() {
    this.processPlayerInput()
    this.updateSpaceObjects()
    this.renderSpaceField()
  }

  fun processPlayerInput() {
    this.controller.nextPlayerCommand()?.also {
      when (it) {
        PlayerCommand.MOVE_SHIP_UP ->
          this.field.ship.boostUp()
        PlayerCommand.MOVE_SHIP_DOWN ->
          this.field.ship.boostDown()
        PlayerCommand.MOVE_SHIP_LEFT ->
          this.field.ship.boostLeft()
        PlayerCommand.MOVE_SHIP_RIGHT ->
          this.field.ship.boostRight()
        PlayerCommand.LAUNCH_MISSILE ->
          this.field.generateMissile()
        PlayerCommand.PAUSE_GAME ->
          this.playing = !this.playing
      }
    }
  }

  fun updateSpaceObjects() {
    if (!this.playing) return
    this.handleCollisions()
    this.moveSpaceObjects()
    this.trimSpaceObjects()
    this.generateAsteroids()
  }

  fun handleCollisions() {
    this.field.spaceObjects.forEachPair {
        (first, second) ->
      if (first.impacts(second)) {
        first.collideWith(second, GameEngineConfig.coefficientRestitution)
        if ((first is Missile) and (second is Asteroid)) {
            this.field.generateExplosion(first)
            this.status.addScore(second)  
            this.updateScores()
        }

        if ((first is Asteroid) and (second is Missile)){
            this.field.generateExplosion(second)
            this.status.addScore(first)   
            this.updateScores()

        }

      }
    }
  }


  fun moveSpaceObjects() {
    this.field.moveShip()
    this.field.moveAsteroids()
    this.field.moveMissiles()

  }

  fun trimSpaceObjects() {
    this.field.trimAsteroids()
    this.field.trimMissiles()
    this.field.trimExplosion()
  }

  fun generateAsteroids() {
    val probability = generator.generateProbability()

    if (probability <= GameEngineConfig.asteroidProbability) {
      this.field.generateAsteroid()
    }
  }

  fun renderSpaceField() {
    this.visualizer.renderSpaceField(this.field)
  }
}

fun <T> List<T>.forEachPair(action: (Pair<T, T>) -> Unit) {
  for (i in 0 until this.size) {
    for (j in i + 1 until this.size) {
      action(Pair(this[i], this[j]))
    }
  }
}
