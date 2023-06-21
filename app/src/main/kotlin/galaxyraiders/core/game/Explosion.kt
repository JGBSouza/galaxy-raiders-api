package galaxyraiders.core.game

import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D

class Explosion(
  initialPosition: Point2D,
  //initialVelocity: Vector2D,
  radius: Double,
  mass: Double
) : SpaceObject("Explosion", '@', initialPosition, Vector2D(dx = 1.0, dy = 1.0), radius, mass) {
  var is_triggered: Boolean = false
  
  fun explode( ){
    // acontece a explosao de alguma forma
    is_triggered = true

  }
}