package com.example.platformgame

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

class Coin(spriteName: String, x: Float, y: Float) : StaticEntity(spriteName, x, y)
{

    override fun onCollision(that: Entity)
    {
        if (that is Player)
        {
            that.coinsCollected++
            engine.levelManager.removeEntity(this)
        }
    }

    override fun render(canvas: Canvas, transform: Matrix, paint: Paint)
    {
        transform.preScale(0.6f, 0.6f)
        super.render(canvas, transform, paint)
    }
}
