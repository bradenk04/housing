package io.github.bradenk04.housing.utils.schem

import org.bukkit.Location
import org.bukkit.World

class Schematic(
    val height: Int,
    val width: Int,
    val length: Int,
    val blocks: List<RelativeBlock>
) {
    companion object {
        fun load(loc1: Location, loc2: Location): Schematic {
            require(loc1.world != null && loc2.world != null) { "Locations must have a world"}
            require(loc1.world == loc2.world) { "Locations must be in the same world." }
            val world = loc1.world!!

            val minX = minOf(loc1.blockX, loc2.blockX)
            val minY = minOf(loc1.blockY, loc2.blockY)
            val minZ = minOf(loc1.blockZ, loc2.blockZ)

            val maxX = maxOf(loc1.blockX, loc2.blockX)
            val maxY = maxOf(loc1.blockY, loc2.blockY)
            val maxZ = maxOf(loc1.blockZ, loc2.blockZ)

            val width = maxX - minX + 1
            val height = maxY - minY + 1
            val length = maxZ - minZ + 1

            val relativeBlocks = mutableListOf<RelativeBlock>()

            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    for (z in minZ..maxZ) {
                        val block = world.getBlockAt(x, y, z)
                        val blockDataStr = block.blockData.asString

                        val relativeX = x - minX
                        val relativeY = y - minY
                        val relativeZ = z - minZ

                        relativeBlocks.add(RelativeBlock(relativeX, relativeY, relativeZ, blockDataStr))
                    }
                }
            }

            return Schematic(height, width, length, relativeBlocks)
        }
    }

    fun paste(location: Location) {
        blocks.forEach { block ->
            val pos = Location(location.world, location.x + block.x, location.y + block.y, location.z + block.z)
            pos.block.blockData = block.blockData
        }
    }
}