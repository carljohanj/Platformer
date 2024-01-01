package com.example.platformgame

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView


const val PREFS = "com.example.platformgame"

lateinit var engine: Game

class Game(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs), Runnable, SurfaceHolder.Callback
{
    private val TAG = "Game"
    private val stageHeight = getScreenHeight()/2
    private val stageWidth = getScreenWidth()/2
    private val visibleEntities = ArrayList<Entity>()
    private val soundEvents = ArrayList<GameEvent>()
    private val jukebox = Jukebox(this)
    private var touchListener: OnTouchListener? = null

    init
    {
        engine = this
        resources
        holder.addCallback(this)

        /* The emulator *might* crash when we use getScreenWidth() and
        *  getScreenHeight() here. If it does, just do the hardcoded
        *  version instead (STAGE_WIDTH and STAGE_HEIGHT):
        */
        holder.setFixedSize(stageWidth, stageHeight)
    }

    private lateinit var gameThread: Thread
    @Volatile private var isRunning = false
    var isGameOver = false
    private val camera = Viewport(stageWidth, stageHeight, GameSettings.METERS_TO_SHOW_X, GameSettings.METERS_TO_SHOW_Y)
    val pool = BitmapPool(this) //Change this so the constructor also takes the camera
    var levelManager = LevelManager(LoadLevel())
    private val hudRenderer = HUDRenderer(context)
    private var inputs = InputManager()
    private val paint = Paint()
    private val transform = Matrix()
    private val position = PointF()

    fun getActivity() = context as MainActivity
    fun getAssets(): AssetManager = context.assets
    fun getPreferences(): SharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private fun getPreferencesEditor(): SharedPreferences.Editor = getPreferences().edit()
    fun savePreference(key: String, v: Boolean) = getPreferencesEditor().putBoolean(key, v).commit()

    fun worldHeight() = levelManager.levelHeight
    fun worldToScreenX(worldDistance: Float) = camera.worldToScreenX(worldDistance)
    fun worldToScreenY(worldDistance: Float) = camera.worldToScreenY(worldDistance)
    private fun getScreenHeight() = context.resources.displayMetrics.heightPixels
    private fun getScreenWidth() = context.resources.displayMetrics.widthPixels

    fun getControls() = inputs

    override fun run()
    {
        var lastFrame = System.nanoTime()
        while(isRunning)
        {
            val deltaTime = (System.nanoTime() - lastFrame) * GameSettings.NANOS_TO_SECOND
            lastFrame = System.nanoTime()
            update(deltaTime)
            //* Give entities access to controllers for user input
            buildVisibleSet()
            render(visibleEntities)
            playSoundEvents()
        }
    }

    private fun buildVisibleSet()
    {
        visibleEntities.clear()
        for(e in levelManager.entities)
        {
            if(camera.inView(e))
            {
                visibleEntities.add(e)
            }
        }
    }

    private fun update(deltaTime: Float)
    {
        levelManager.update(deltaTime)
        camera.lookAt(levelManager.player)
        checkGameOver()
    }

    private fun render(visibleSet: ArrayList<Entity>)
    {
        val canvas = acquireAndLockCanvas() ?: return
        canvas.drawColor(Color.BLUE)
        for(e in visibleSet)
        {
            transform.reset()
            camera.worldToScreen(e, position)
            transform.postTranslate(position.x, position.y)
            e.render(canvas, transform, paint)
        }
        hudRenderer.updateHUD(canvas, paint, levelManager, isGameOver)
        holder.unlockCanvasAndPost(canvas)
    }

    private fun acquireAndLockCanvas() : Canvas?
    {
        if(holder?.surface?.isValid == false)
        {
            return null
        }
        return holder.lockCanvas()
    }

    private fun checkGameOver()
    {
        if(levelManager.player.health < 1)
        {
            isGameOver = true
            setListener()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener()
    {
        if (touchListener == null)
        {
            touchListener = OnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN)
                {
                    reset()
                }
                true
            }
            setOnTouchListener(touchListener)
        }
        else if (!isGameOver)
        {
            // Detach the touch listener when the game is not over
            setOnTouchListener(null)
            touchListener = null
        }
    }

    fun pause()
    {
        Log.d(TAG, "pause")
        isRunning = false
        jukebox.pauseBgMusic()
        getControls().onPause()
        try{
            gameThread.join()
        }
        catch(e: Exception){/*swallow exception, we're exiting anyway*/}
    }

    fun resume()
    {
        Log.d(TAG, "resume")
        jukebox.resumeBgMusic()
        getControls().onResume()
        isRunning = true
    }

    private fun reset()
    {
        jukebox.pauseBgMusic()
        jukebox.unloadMusic()
        levelManager = LevelManager(LoadLevel())
        isGameOver = false
        jukebox.loadMusic()
        jukebox.resumeBgMusic()
        jukebox.playEventSound(GameEvent.Restart)
    }

    override fun surfaceCreated(p0: SurfaceHolder)
    {
        Log.d(TAG, "surfaceCreated")
        gameThread = Thread(this)
        gameThread.start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, format: Int, width: Int, height: Int)
    {
        Log.d(TAG, "Surface changed. Width: $width. Height: $height")
        Log.d(TAG, "Screen width: ${getScreenWidth()} Screen height: ${getScreenHeight()}")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder)
    {
        Log.d(TAG, "Surface destroyed")
    }

    fun setControls(input: InputManager)
    {
        inputs.onPause()
        inputs.onStop()
        inputs = input
        inputs.onResume()
        inputs.onStart()
    }

    fun onGameEvent(event: GameEvent, e: Entity?)
    {
        soundEvents.add(event)
    }

    private fun playSoundEvents()
    {
        for(event in soundEvents)
        {
            jukebox.playEventSound(event)
        }
        soundEvents.clear()
    }
}