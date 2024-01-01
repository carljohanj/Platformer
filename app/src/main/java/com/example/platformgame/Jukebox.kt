package com.example.platformgame

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import java.io.IOException

private const val SOUNDS_PREF_KEY = "sounds_pref_key"
private const val MUSIC_PREF_KEY = "music_pref_key"

class Jukebox(private val engine: Game)
{
    private val TAG = "Jukebox"
    private var mSoundPool: SoundPool? = null
    private var mBgPlayer: MediaPlayer? = null
    private val mSoundsMap = HashMap<GameEvent, Int>()
    private var mSoundEnabled: Boolean = true
    private var mMusicEnabled: Boolean = true

    init
    {
        engine.getActivity().volumeControlStream = AudioManager.STREAM_MUSIC
        val prefs = engine.getPreferences()
        mSoundEnabled = prefs.getBoolean(SOUNDS_PREF_KEY, true)
        mMusicEnabled = prefs.getBoolean(MUSIC_PREF_KEY, true)
        loadIfNeeded()
    }

    private fun loadIfNeeded()
    {
        if (mSoundEnabled) {
            loadSounds()
        }
        if (mMusicEnabled) {
            loadMusic()
        }
    }

    private fun loadSounds()
    {
        val attr = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mSoundPool = SoundPool.Builder()
            .setAudioAttributes(attr)
            .setMaxStreams(JukeboxSettings.MAX_STREAMS)
            .build()

        mSoundsMap.clear()
        loadEventSound(GameEvent.Jump, "sound/jump_sound.wav")
        loadEventSound(GameEvent.CoinPickup, "sound/pickup_coin.wav")
        loadEventSound(GameEvent.EnemyHit, "sound/hurt.wav")
        loadEventSound(GameEvent.Restart, "sound/select.wav")
    }

    private fun loadEventSound(event: GameEvent, fileName: String)
    {
        try
        {
            val afd = engine.getAssets().openFd(fileName)
            val soundId = mSoundPool!!.load(afd, 1)
            mSoundsMap[event] = soundId
        }
        catch (e: IOException)
        {
            Log.e(TAG, "Error loading sound $e")
        }
    }

    private fun unloadSounds()
    {
        if (mSoundPool == null) {
            return
        }
        mSoundPool!!.release()
        mSoundPool = null
        mSoundsMap.clear()
    }

    fun playEventSound(event: GameEvent)
    {
        if (!mSoundEnabled)
        {
            return
        }
        val leftVolume = JukeboxSettings.DEFAULT_SFX_VOLUME
        val rightVolume = JukeboxSettings.DEFAULT_SFX_VOLUME
        val priority = 1
        val loop = 0 //-1 loop forever, 0 play once
        val rate = 1.0f
        val soundID = mSoundsMap[event]
        if(soundID == null)
        {
            Log.e(TAG, "Attempting to play non-existent event sound: {event}")
            return
        }
        if (soundID > 0) { //if soundID is 0, the file failed to load. Make sure you catch this in the loading routine.
            mSoundPool!!.play(soundID, leftVolume,
                rightVolume, priority, loop, rate)
        }
    }

    fun loadMusic()
    {
        try {
            mBgPlayer = MediaPlayer()
            val afd = engine.getAssets().openFd("sound/goodman_savoy.wav")
            mBgPlayer!!.setDataSource(
                afd.fileDescriptor,
                afd.startOffset,
                afd.length
            )
            mBgPlayer!!.isLooping = true
            mBgPlayer!!.setVolume(JukeboxSettings.DEFAULT_MUSIC_VOLUME, JukeboxSettings.DEFAULT_MUSIC_VOLUME)
            mBgPlayer!!.prepare()
        } catch (e: IOException) {
            Log.e(TAG, "Unable to create MediaPlayer.", e)
        }
    }

    fun pauseBgMusic()
    {
        if (!mMusicEnabled) {
            return
        }
        mBgPlayer!!.pause()
    }

    fun resumeBgMusic()
    {
        if (!mMusicEnabled) {
            return
        }
        mBgPlayer!!.start()
    }

    fun unloadMusic()
    {
        if (mBgPlayer == null) {
            return
        }
        mBgPlayer!!.stop()
        mBgPlayer!!.release()
    }

    fun toggleSoundStatus()
    {
        mSoundEnabled = !mSoundEnabled
        if (mSoundEnabled) {
            loadSounds()
        } else {
            unloadSounds()
        }
        engine.savePreference(SOUNDS_PREF_KEY, mSoundEnabled)
    }

    fun toggleMusicStatus()
    {
        mMusicEnabled = !mMusicEnabled
        if (mMusicEnabled) {
            loadMusic()
        } else {
            unloadMusic()
        }
        engine.savePreference(MUSIC_PREF_KEY, mSoundEnabled)
    }

}

enum class GameEvent
{
    Jump, EnemyHit, CoinPickup, Restart
}