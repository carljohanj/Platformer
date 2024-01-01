package com.example.platformgame

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class LoadLevel : LevelData()
{
    private val context: Context = engine.context

    init
    {
        tileToBitmap.put(NO_TILE, "no_tile")
        tileToBitmap.put(1, PLAYER)
        tileToBitmap.put(2, "ground_rounded_left")
        tileToBitmap.put(3, "ground_square")
        tileToBitmap.put(4, "ground_rounded_right")
        tileToBitmap.put(5, "ground_rounded")
        tileToBitmap.put(6, "spears_down")
        tileToBitmap.put(7, "ice_square")
        tileToBitmap.put(8, ENEMY)
        tileToBitmap.put(9, COIN)
        tileToBitmap.put(10, "sign_right")

        loadTiles()
    }

    private fun loadTiles()
    {
        try
        {
            val inputStream = context.assets.open(GameSettings.TEST_LEVEL)
            val reader = BufferedReader(InputStreamReader(inputStream))

            val tileRows = mutableListOf<IntArray>()
            var line: String?

            while (reader.readLine().also { line = it } != null)
            {
                val parts = line?.split(",")?.map { it.trim().toInt() }?.toIntArray()
                if (parts != null)
                {
                    tileRows.add(parts)
                }
            }

            tiles = tileRows.toTypedArray()

            reader.close()
        }
        catch (e: Exception) { /* Handle exceptions or errors */ }
    }

}