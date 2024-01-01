package com.example.platformgame

import android.util.SparseArray

internal const val PLAYER = PlayerAttributes.PLAYER
internal const val ENEMY = EnemyAttributes.ENEMY
internal const val COIN = CollectibleAttributes.COIN
internal const val NULLSPRITE = GameSettings.NULLSPRITE
internal const val NO_TILE = GameSettings.NO_TILE

abstract class LevelData
{
    var tiles: Array<IntArray> = emptyArray()
    val tileToBitmap = SparseArray<String>()

    fun getRow(y: Int): IntArray
    {
        return tiles[y]
    }

    fun getTile(x: Int, y: Int): Int
    {
        return getRow(y)[x]
    }

    fun getSpriteName(tileType: Int): String
    {
        val filename = tileToBitmap[tileType]
        return filename ?: NULLSPRITE
    }

    fun getHeight(): Int
    {
        return tiles.size
    }

    fun getWidth(): Int
    {
        return getRow(0).size
    }


}