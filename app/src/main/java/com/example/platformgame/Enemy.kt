package com.example.platformgame

import android.graphics.PointF

class Enemy(spriteName: String, x: Float, y: Float) : DynamicEntity(spriteName, x, y) {
    private var movingDown = true
    private var speed = 0.1f

    override fun update(dt: Float)
    {
        if (movingDown) { y += speed }
        else { y -= speed }
    }

    override fun onCollision(that: Entity)
    {
        val overlap = PointF()
        if (getOverlap(this, that, overlap))
        {

            if (overlap.y != 0f)
            {
                movingDown = !movingDown
                y += overlap.y
            }
        }
    }
}
