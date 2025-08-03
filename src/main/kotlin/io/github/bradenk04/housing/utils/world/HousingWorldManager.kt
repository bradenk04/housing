package io.github.bradenk04.housing.utils.world

import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import java.util.UUID
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.deleteRecursively

object HousingWorldManager {
    private lateinit var _world: World
    fun createWorld(): World {
        val name = "housing_world_${UUID.randomUUID()}"
        val worldCreator = WorldCreator(name)
        worldCreator.generateStructures(false)
        worldCreator.generator(VoidChunkGenerator)

        val world = Bukkit.createWorld(worldCreator) ?: throw IllegalStateException("Could not create world")
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        _world = world
        return world
    }

    @OptIn(ExperimentalPathApi::class)
    fun destroyWorld(): Boolean {
        val unloadSuccessful = Bukkit.unloadWorld(_world, false)

        if (unloadSuccessful) {
            try {
                Path(_world.worldFolder.absolutePath).deleteRecursively()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return unloadSuccessful
    }

    val world: World
        get() = _world

    object VoidChunkGenerator : ChunkGenerator() {
        override fun generateChunkData(
            world: World,
            random: java.util.Random,
            chunkZ: Int,
            z: Int,
            biome: BiomeGrid
        ): ChunkData {
            // Return a ChunkData object with no blocks, effectively creating a void world.
            return createChunkData(world)
        }
    }
}