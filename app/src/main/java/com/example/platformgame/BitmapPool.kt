package com.example.platformgame

import android.graphics.Bitmap
import android.util.Log
import com.example.platformgame.BitmapUtils.loadScaledBitmap

class BitmapPool(private val engine: Game)
{
    val TAG = "BitmapPool"
    private val bitmaps: HashMap<String, Bitmap> = HashMap()
    private var nullsprite: Bitmap = loadScaledBitmap(
        engine.context, "nullsprite",
        engine.worldToScreenX(1.0f).toInt(),
        engine.worldToScreenY(1.0f).toInt()
    )

    private fun getBitmap(key: String) = bitmaps[key] ?: nullsprite

    public fun createBitmap(sprite: String, widthMeters: Float, heightMeters: Float): Bitmap
    {
        val key = makeKey(sprite, widthMeters, heightMeters)
        if(bitmaps.containsKey(key))
        {
            return getBitmap(key)
        }
        try
        {
            val bmp = loadScaledBitmap(
                engine.context,
                sprite,
                engine.worldToScreenX(widthMeters).toInt(),
                engine.worldToScreenY(heightMeters).toInt()
            )
            put(key, bmp)
            return bmp
        }
        catch (e: Exception)
        {
            Log.w(TAG, "Failed to createBitmap $sprite! Returning nullsprite", e)
        }
        return nullsprite
    }

    public fun clear()
    {
        for ((key, value) in bitmaps)
        {
            value.recycle()
        }
        bitmaps.clear()
    }

    public fun size() = bitmaps.size
    private fun makeKey(name: String, widthMeters: Float, heightMeters: Float) = name + "_" + widthMeters + "_" + heightMeters
    private fun put(key: String, bmp: Bitmap) { bitmaps[key] = bmp }
    private fun contains(key: String) = bitmaps.containsKey(key)
    private fun contains(bmp: Bitmap) = bitmaps.containsValue(bmp)

    private fun getKey(bmp: Bitmap): String
    {
        for ((key, value) in bitmaps)
        {
            if (bmp == value)
            {
                return key
            }
        }
        return ""
    }

    private fun remove(key: String)
    {
        val bmp = bitmaps[key] ?: return
        bitmaps.remove(key)
        bmp.recycle()
    }
}