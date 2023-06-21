package galaxyraiders.core.game

import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D
import java.util.Timer

class Explosion(
  initialPosition: Point2D,
  //initialVelocity: Vector2D,
  radius: Double,
  mass: Double
) : SpaceObject("Explosion", '@', initialPosition, Vector2D(1.0, 1.0), radius, mass) {
  var is_triggered: Boolean = true
  
  var timer: Timer = Timer("schedule", true);

  init {
    timer.schedule(3000) {
        is_triggered = false
    }
  }
}