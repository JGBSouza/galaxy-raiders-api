package galaxyraiders.core.physics

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

@JsonIgnoreProperties("unit", "normal", "degree", "magnitude")
data class Vector2D(val dx: Double, val dy: Double) {

  override fun toString(): String {
    return "Vector2D(dx=$dx, dy=$dy)"
  }

  val magnitude: Double
    get() = sqrt(this.dx * this.dx + this.dy * this.dy)

  val radiant: Double
    get() = atan2(this.dy, this.dx)

  val degree: Double
    get() = 180 * radiant / PI

  val unit: Vector2D
    get() = this / this.magnitude

  val normal: Vector2D
    get() = Vector2D(this.unit.dy, -this.unit.dx)

  operator fun times(scalar: Double): Vector2D {
    return Vector2D(this.dx * scalar, this.dy * scalar)
  }

  operator fun div(scalar: Double): Vector2D {
    return Vector2D(this.dx / scalar, this.dy / scalar)
  }

  operator fun minus(vector: Vector2D): Vector2D {
    return Vector2D(this.dx - vector.dx, this.dy - vector.dy)
  }

  operator fun plus(vector: Vector2D): Vector2D {
    return Vector2D(this.dx + vector.dx, this.dy + vector.dy)
  }

  operator fun times(vector: Vector2D): Double {
    return (this.dx * vector.dx) + (dy * vector.dy)
  }
  operator fun div(vector: Vector2D): Double {
    return (this.dx / vector.dx) + (dy / vector.dx)
  }

  operator fun unaryMinus(): Vector2D {
    return Vector2D(dx = -dx, dy = -dy)
  }

  operator fun plus(point: Point2D): Point2D {
    return Point2D(dx + point.x, dy + point.y)
  }

  fun vectorProject(vector: Vector2D): Vector2D {
    return this.scalarProject(vector) * vector.unit
  }

  fun scalarProject(vector: Vector2D): Double {
    return (this * vector) / (vector.magnitude)
  }
}

operator fun Double.times(v: Vector2D): Vector2D {
  return v * this
}
