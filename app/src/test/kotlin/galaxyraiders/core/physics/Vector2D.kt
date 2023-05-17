import kotlin.math.sqrt
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.PI

class Vector2D(var dx:Double, var dy:Double){
    fun defineMagnitude(dx:Double, dy:Double): Double{
        return abs(sqrt(dx * dy))
    }

    fun defineRadiant(dx:Double, dy:Double): Double{
        return atan2(dx, dy)
    }

    fun defineAngle(radiant:Double): Double{
        return 180 * radiant/PI
    }
    
    var magnitude:Double = defineMagnitude(dx, dy)
    var radiant: Double = defineRadiant(dx, dy)
    var degree: Double = defineAngle(radiant)

    operator fun times(scalar: Double) = Vector2D(
        dx = dx * scalar,
        dy = dy * scalar
    )

    operator fun div(scalar: Double) = Vector2D(
        dx = dx / scalar,
        dy = dy / scalar
    )

    operator fun plus(vector: Vector2D) = Vector2D(
        dx = dx + vector.dx,
        dy = dy + vector.dy
    )

    operator fun unaryMinus() = Vector2D(
        dx = -dx,
        dy = -dy
    )

}


fun main(){
    val vector = Vector2D(1.0, 0.5)
    println(vector.radiant)
    println(vector.degree)

}