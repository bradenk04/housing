package io.github.bradenk04.housing.utils

import io.github.bradenk04.housing.HousingPlugin
import net.sandrohc.schematic4j.schematic.Schematic
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import org.slf4j.LoggerFactory
import kotlin.math.min

private const val BLOCKS_PER_TICK = 1024

private val LOGGER = LoggerFactory.getLogger("SchematicUtils")

fun Schematic.place(location: Location) {
    val world = location.world ?: throw NullPointerException("Could not place schematic: Location world is null.")

    val totalBlocks = width() * height() * length()

    val blockList = blocks().toList()
    var blocksPlaced = 0

    object : BukkitRunnable() {
        override fun run() {
            if (blocksPlaced < totalBlocks) {
                val end = min(blocksPlaced + BLOCKS_PER_TICK, totalBlocks)
                for (i in blocksPlaced until end) {
                    val blockPos = blockList[i].left
                    val block = blockList[i].right
                    val target = world.getBlockAt(location.blockX + blockPos.x(), location.blockY + blockPos.y(), location.blockZ + blockPos.z())

                    val id = block.block()
                    val states = block.states()
                    val dataStr = if (states.isEmpty()) id else {
                        "$id[${states.entries.joinToString(",") { "${it.key}=${it.value}" }}]"
                    }

                    try {
                        val data = Bukkit.createBlockData(dataStr)
                        target.setBlockData(data, false)
                    } catch (e: IllegalArgumentException) {
                        LOGGER.warn("Could not create BlockData for data `$dataStr` at location ${blockPos.x()},${blockPos.y()},${blockPos.z()}")
                    }
                }
                blocksPlaced = end
                return
            }

            this.cancel()
        }
    }.runTaskTimer(HousingPlugin.plugin, 0L, 1L)
}