package com.example.platformgame

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.Button

@SuppressLint("ClickableViewAccessibility")
class TouchController(view: View) : InputManager(), View.OnTouchListener
{
    init
    {
        view.findViewById<Button>(R.id.gamepad_up)
            .setOnTouchListener(this)
        view.findViewById<Button>(R.id.gamepad_down)
            .setOnTouchListener(this)
        view.findViewById<Button>(R.id.gamepad_left)
            .setOnTouchListener(this)
        view.findViewById<Button>(R.id.gamepad_right)
            .setOnTouchListener(this)
        view.findViewById<Button>(R.id.gamepad_jump)
            .setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean
    {
        val action = event.actionMasked
        val id: Int = v.getId()
        if (action == MotionEvent.ACTION_DOWN)
        {
            // player is pressing a "button"
            if (id == R.id.gamepad_up)
            {
                _verticalFactor -= 1
            }
            else if (id == R.id.gamepad_down)
            {
                _verticalFactor += 1
            }

            if (id == R.id.gamepad_left)
            {
                _horizontalFactor -= 1
            }
            else if (id == R.id.gamepad_right)
            {
                _horizontalFactor += 1
            }

            if (id == R.id.gamepad_jump)
            {
                _isJumping = true
            }
        }
        else if (action == MotionEvent.ACTION_UP)
        {
            // player released a "button"
            if (id == R.id.gamepad_up) {
                _verticalFactor += 1
            } else if (id == R.id.gamepad_down) {
                _verticalFactor -= 1
            }

            if (id == R.id.gamepad_left) {
                _horizontalFactor += 1
            } else if (id == R.id.gamepad_right) {
                _horizontalFactor -= 1
            }

            if (id == R.id.gamepad_jump) {
                _isJumping = false
            }
        }
        return false
    }
}