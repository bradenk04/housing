package io.github.bradenk04.housing.utils.schem

import org.bukkit.Bukkit
import org.bukkit.block.data.BlockData

class RelativeBlock(
    val x: Int,
    val y: Int,
    val z: Int,
    private val blockDataStr: String
) {
    val blockData: BlockData
        get() = Bukkit.getServer().createBlockData(blockDataStr)
}