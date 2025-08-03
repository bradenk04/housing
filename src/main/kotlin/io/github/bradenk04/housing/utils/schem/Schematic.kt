package io.github.bradenk04.housing.utils.schem

import org.bukkit.Location

class Schematic(
    val height: Int,
    val width: Int,
    val length: Int,
    val blocks: List<RelativeBlock>
) {
    fun paste(location: Location) {
        blocks.forEach { block ->
            val pos = Location(location.world, location.x + block.x, location.y + block.y, location.z + block.z)
            pos.block.blockData = block.blockData
        }
    }
}