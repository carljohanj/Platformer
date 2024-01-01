package com.example.platformgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

open class StaticEntity(sprite: String, x: Float, y: Float) : Entity()
{
    private var bitmap: Bitmap

    init
    {
        this.x = x
        this.y = y
        width = 1.0f
        height = 1.0f
        bitmap = engine.pool.createBitmap(sprite, width, height)
    }

    override fun render(canvas: Canvas, transform: Matrix, paint: Paint)
    {
        canvas.drawBitmap(bitmap, transform, paint)
    }
}