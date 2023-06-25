package galaxyraiders.core.game

import java.time.Instant


class GameStatus(var score: Double = 0.0, 
                var destroyedAsteroids: Int = 0,
                var startTime : String = Instant.now().toString()){

    fun addScore(asteroid: SpaceObject){
        this.score += asteroid.mass*(asteroid.mass/asteroid.radius)
        this.destroyedAsteroids += 1
    }

    fun toJson(): String {
        return ("\"\"\"" +
                "{\"score\": ${this.score}," +
                "\"destroyedAsteroids\": ${this.destroyedAsteroids}," +
                "\"startTime\": \"${this.startTime}\"}" +
                "\"\"\"")
    }
}