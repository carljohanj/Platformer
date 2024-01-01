package com.example.platformgame

/* This class uses the object notation to essentially create singletons
   for all the game constants. These can then be called statically from
   all the classes in the application, negating the need to create new
   instances of the objects.
 */

object GameSettings
{
    const val NULLSPRITE = "nullsprite"
    const val NO_TILE = 0
    const val PIXELS_PER_METER = 50
    const val METERS_TO_SHOW_X = 16f
    const val METERS_TO_SHOW_Y = 0f
    const val NANOS_TO_SECOND = 1.0f / 1000000000f
    const val TEST_LEVEL = "tiles.txt"
}

object PlayerAttributes
{
    const val PLAYER = "player_left1"
    const val INVULNERABILITY_TIME: Long = 2500
    const val PLAYER_RUN_SPEED = 5.0f
}

object EnemyAttributes
{
    const val ENEMY = "enemy_brown"
    const val DAMAGE_AMOUNT = 1
}

object CollectibleAttributes
{
    const val COIN = "coin_yellow_shade"
}

object JukeboxSettings
{
    const val MAX_STREAMS = 5
    const val DEFAULT_MUSIC_VOLUME: Float = 10f
    const val DEFAULT_SFX_VOLUME: Float = 0.6f
}