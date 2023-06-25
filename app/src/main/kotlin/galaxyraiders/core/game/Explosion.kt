package galaxyraiders.core.game

import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D
import java.util.Timer
import java.util.TimerTask

const val EXPLOSION_DURATION = 2000L
class Explosion(
  initialPosition: Point2D,
  radius: Double,
  mass: Double
) : SpaceObject("Explosion", '@', initialPosition, Vector2D(1.0, 1.0), 3.0, mass=0) {
  var is_triggered: Boolean = true
  private set

   init {
    Timer("Schedule", false).schedule(
      object : TimerTask() {
        override fun run() {
          is_triggered = false
        }
      },
      EXPLOSION_DURATION
    )
  }
}