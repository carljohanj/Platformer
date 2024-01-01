package com.example.platformgame

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LevelManager(level: LevelData)
{
    var levelHeight = 0
    lateinit var player: Player
    private lateinit var enemy: Enemy
    val entities = ArrayList<Entity>()
    private val entitiesToAdd = ArrayList<Entity>()
    private val entitiesToRemove = ArrayList<Entity>()

    init
    {
        loadAssets(level)
    }

    fun update(dt: Float)
    {
        for(e in entities)
        {
            e.update(dt)
        }
        checkCollisions()
        addAndRemoveEntities()
    }

    private fun checkCollisions()
    {
        for(e in entities)
        {
            if(e == player) { continue }
            if(e == enemy) { continue }

            if (e is Coin && isColliding(player, e))
            {
                player.collectCoin()
                engine.onGameEvent(GameEvent.CoinPickup, player)
                removeEntity(e)
                return
            }

            if(isColliding(player, e))
            {
                player.onCollision(e)
                e.onCollision(player)
            }

            if(isColliding(enemy, e))
            {
                enemy.onCollision(e)
                e.onCollision(enemy)
            }

            if(isColliding(player, enemy) )
            {
                if (!player.isInvulnerable)
                {
                    player.takesDamage(EnemyAttributes.DAMAGE_AMOUNT)
                    player.isInvulnerable = true
                    player.isBlinking = true
                    engine.onGameEvent(GameEvent.EnemyHit, player)
                    CoroutineScope(Dispatchers.Default).launch{
                        delay(PlayerAttributes.INVULNERABILITY_TIME)
                        player.isInvulnerable = false
                        player.isBlinking = false
                    }
                }
                enemy.onCollision(player)
                player.onCollision(enemy)
            }
        }
    }


    private fun loadAssets(level: LevelData)
    {
        levelHeight = level.getHeight()
        for(y in 0 until levelHeight)
        {
            val row = level.getRow(y)
            for(x in row.indices)
            {
                val tileID = row[x]
                if(tileID == NO_TILE) continue
                val spriteName = level.getSpriteName(tileID)
                createEntity(spriteName, x, y)
            }
        }
        addAndRemoveEntities()
    }

    private fun createEntity(spriteName: String, x: Int, y: Int)
    {
        when (spriteName) {
            PLAYER -> {
                player = Player(spriteName, x.toFloat(), y.toFloat())
                addEntity(player)
            }
            ENEMY -> {
                enemy = Enemy(spriteName, x.toFloat(), y.toFloat())
                addEntity(enemy)
            }
            COIN -> {
                val coin = Coin(spriteName, x.toFloat(), y.toFloat())
                addEntity(coin)
            }
            else -> {
                addEntity(StaticEntity(spriteName, x.toFloat(), y.toFloat()))
            }
        }
    }

    fun addEntity(e: Entity)
    {
        entitiesToAdd.add(e)
    }

    fun removeEntity(e: Entity)
    {
        entitiesToRemove.add(e)
    }

    private fun addAndRemoveEntities()
    {
        for(e in entitiesToRemove)
        {
            entities.remove(e)
        }
        for(e in entitiesToAdd)
        {
            entities.add(e)
        }

        entitiesToRemove.clear()
        entitiesToAdd.clear()
    }

    private fun cleanup()
    {
        addAndRemoveEntities()
        for(e in entities)
        {
            e.destroy()
        }
    }

    private fun destroy()
    {
        cleanup()
    }
}