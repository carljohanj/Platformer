package com.example.platformgame

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable

class HUDRenderer(context: Context)
{
    private lateinit var fullHeartBitmap: Bitmap
    private lateinit var halfHeartBitmap: Bitmap
    private var gameOverText: String
    private var coinCounterLabel: String

    init
    {
        loadAssets(context)
        gameOverText = context.getString(R.string.game_over_message)
        coinCounterLabel = context.getString(R.string.coin_counter_label)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun loadAssets(context: Context)
    {
        val res = context.resources
        val fullHeartDrawable = res.getDrawable(R.drawable.hearth_full, null)
        val halfHeartDrawable = res.getDrawable(R.drawable.hearth_half, null)
        fullHeartBitmap = (fullHeartDrawable as BitmapDrawable).bitmap
        halfHeartBitmap = (halfHeartDrawable as BitmapDrawable).bitmap
    }

    fun updateHUD(canvas: Canvas, paint: Paint, levelManager: LevelManager, isGameOver: Boolean)
    {
        val heartSize = fullHeartBitmap.width + 10
        val x = 20f
        val y = 40f
        val coinCountText = coinCounterLabel + " " + "${levelManager.player.coinsCollected}"
        val textX = 20f
        val textY = 30f

        paint.color = Color.WHITE
        paint.textSize = 20f

        if (!isGameOver)
        {
            val maxHearts = 3
            val playerHealth = levelManager.player.health
            val fullHearts = playerHealth / 2
            val displayHalfHeart: Boolean = playerHealth % 2 == 1

            for (i in 0 until maxHearts)
            {
                val heartX = x + i * (heartSize + 10)
                if (i < fullHearts)
                {
                    canvas.drawBitmap(fullHeartBitmap, heartX, y, paint)
                }
                else if (i == fullHearts && displayHalfHeart)
                {
                    canvas.drawBitmap(halfHeartBitmap, heartX, y, paint)
                }
            }
            canvas.drawText(coinCountText, textX, textY, paint)
        }
        else
        {
            paint.textSize = 32f
            canvas.drawText(gameOverText, x, y, paint)
        }
    }
}
