package galaxyraiders.core.game

import galaxyraiders.Config
import galaxyraiders.ports.RandomGenerator
import galaxyraiders.ports.ui.Controller
import galaxyraiders.ports.ui.Controller.PlayerCommand
import galaxyraiders.ports.ui.Visualizer
import kotlin.system.measureTimeMillis
import java.time.LocalDate
import java.io.File
import com.beust.klaxon.Klaxon
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

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
  

  fun execute() {

    while (true) {
      val duration = measureTimeMillis { this.tick() }
      
      Thread.sleep(
        maxOf(0, GameEngineConfig.msPerFrame - duration)
      )
    }
    //updateLeaderBoard()
  }

  fun execute(maxIterations: Int) {
    val startTime = LocalDate.now()
    var destroyedAsteroids = 0.0
    var gameScore = 0.0
    
    repeat(maxIterations) {
      this.tick()
    }

    
    updateScoreBoard(gameScore, destroyedAsteroids, startTime)
    
  }
  
  class Score(val gameScore: Double, val destroyedAsteroids: Double, val startTime: LocalDate)
  
  fun updateScoreBoard(gameScore: Double, destroyedAsteroids: Double, startTime: LocalDate){
    val scoreBoardJson = File("../score/text.json").readText()
    //val scoreBoardArray = Klaxon().parseArray<Score>(scoreBoardJson)
    //val currentScore = Score(gameScore, destroyedAsteroids, startTime)
    /*val json = Klaxon().parse<Score>("""
      {
        "gameScore": "%.1f"
      }
    """.format()
    )*/
    
  }

  fun updateLeaderBoard(gameScore: Double, destroyedAsteroids: Double, startTime: LocalDate){
    
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
        if((first.type=="Missle" && second.type=="Asteroid") || (second.type=="Missle" && first.type=="Asteroid")){
<<<<<<< HEAD
          var asteroid = if (first.type == "Asteroid") first else second
          //if (asteroid.is_triggered){
          gameScore += asteroid.mass * asteroid.mass / asteroid.radius
          destroyedAsteroids += 1
          //}
=======
          //tem que ter explosao
          this.field.generateExplosion(first.center, 5.0, 5.0)

          asteroid = if (first.type=="Asteroid") first else second
          if (asteroid.is_triggered){
            gameScore += asteroid.mass * asteroid.mass / asteroid.radius
            destroyedAsteroids += 1
          }
>>>>>>> 5960936247bea5e24111b4d05d7627a61e4c623e
        
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
