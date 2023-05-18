package galaxyraiders.core.physics
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class Point2D(val x: Double, val y: Double) {
  operator fun plus(p: Point2D): Point2D {
    return Point2D(this.x + p.x, this.y + p.y)
  }

  operator fun plus(v: Vector2D): Point2D {
    return Point2D(this.x + v.dx, this.y + v.dy)
  }

  override fun toString(): String {
    return "Point2D(x=$x, y=$y)"
  }

  fun toVector(): Vector2D {
    return Vector2D(this.x, this.y)
  }

  fun impactVector(p: Point2D): Vector2D {
    return Vector2D(Math.abs(this.x - p.x), Math.abs(this.y - p.y))
  }

  fun impactDirection(p: Point2D): Vector2D {
    return impactVector(p).unit
  }

  fun contactVector(p: Point2D): Vector2D {
    val impactVector: Vector2D = impactVector(p)
    return Vector2D(impactVector.normal.dx, impactVector.normal.dy)
  }

  fun contactDirection(p: Point2D): Vector2D {
    return impactDirection(p).normal
  }

  fun distance(p: Point2D): Double {
    val deltaX: Double = this.x - p.x
    val deltaY: Double = this.y - p.y
    val d: Double = Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaY, 2.0))
    return d
  }
}
