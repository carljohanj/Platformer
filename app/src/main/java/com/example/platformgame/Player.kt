package com.example.platformgame

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

val PLAYER_JUMP_FORCE: Float = -(GRAVITY / 2f)
const val LEFT = 1.0f
const val RIGHT = -1.0f

class Player(spriteName: String, xPos: Float, yPos: Float) : DynamicEntity(spriteName, xPos, yPos)
{
    val TAG = "Player"
    private var facing = RIGHT
    var maxHealth = 6  //Note to self: Don't delete, the HUD needs this to draw the hearts!
    var health = 6
    var coinsCollected: Int = 0
    var isInvulnerable = false
    var isBlinking = false
    private var blinkInterval = 5

    override fun update(dt: Float)
    {
        if (isInvulnerable)
        {
            blinkInterval--
            if (blinkInterval <= 0)
            {
                blinkInterval = 5
                isBlinking = !isBlinking
            }
        }

        val controls: InputManager = engine.getControls()
        val direction: Float = controls._horizontalFactor
        velX = direction * PlayerAttributes.PLAYER_RUN_SPEED
        facing = getFacingDirection(direction)

        if (controls._isJumping && isOnGround)
        {
            velY = PLAYER_JUMP_FORCE
            isOnGround = false
            engine.onGameEvent(GameEvent.Jump, this)
        }

        super.update(dt) //parent will integrate our velocity and time with our position
    }

    private fun getFacingDirection(direction: Float): Float
    {
        if(direction < 0.0f)
        {
            return LEFT
        }
        else if(direction > 0.0f)
        {
            return RIGHT
        }

        return facing
    }

    override fun render(canvas: Canvas, transform: Matrix, paint: Paint)
    {
        if(!isBlinking)
        {
            transform.preScale(facing, 1.0f)
            //Logic to reposition bitmap when flipped so the player doesn't "teleport"
            if(facing == RIGHT)
            {
                val offset = engine.worldToScreenX(width)
                transform.postTranslate(offset, 0.0f)
            }
            super.render(canvas, transform, paint)
        }
    }

    fun takesDamage(damageAmount: Int)
    {
        if (!isInvulnerable)
        {
            health -= damageAmount
        }
    }

    fun collectCoin()
    {
        coinsCollected++
    }

}