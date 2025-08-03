package io.github.bradenk04.housing.utils.schem

import net.kyori.adventure.nbt.CompoundBinaryTag
import kotlin.experimental.and

sealed interface SchematicVersion {
    class SpongeV3Schematic : SchematicVersion {
        override fun deserialize(cbt: CompoundBinaryTag): Schematic {
            val width = cbt.getShort("Width")
            val height = cbt.getShort("Height")
            val length = cbt.getShort("Length")
            val blocksData = cbt.getCompound("Blocks")
            val palette = parsePaletteData(blocksData.getCompound("Palette"))
            val blocks = parseBlockData(blocksData.getByteArray("Data"), palette, width, height, length)
            return Schematic(
                height = height.toInt(),
                width = width.toInt(),
                length = length.toInt(),
                blocks = blocks
            )
        }

        private fun parseBlockData(blockData: ByteArray, palette: Map<Int, String>, width: Short, height: Short, length: Short): List<RelativeBlock> {
            val blocks = mutableListOf<RelativeBlock>()

            var index = 0
            var i = 0
            var value = 0
            var varIntLength = 0
            while (i < blockData.size) {
                value = 0
                varIntLength = 0
                while (true) {
                    value = value or ((blockData[i] and 127).toInt()) shl (varIntLength++ * 7)
                    if (varIntLength > 5) {
                        throw RuntimeException("VarInt too big")
                    }

                    if ((blockData[i] and 128.toByte()).toInt() != 128) {
                        i++
                        break
                    }
                    i++
                }

                val state = palette[value]
                if (state == null) throw RuntimeException("State missing in palette")

                val y = index / (width * length);
                val z = (index % (width * length)) / width;
                val x = (index % (width * length)) % width;
                blocks.add(RelativeBlock(
                    x,
                    y,
                    z,
                    state
                ))
                index++
            }

            return blocks
        }

        private fun parsePaletteData(cbt: CompoundBinaryTag): Map<Int, String> {
            val palette = mutableMapOf<Int, String>()
            cbt.keySet().forEach {
                val value = cbt.getInt(it)
                palette[value] = it
            }
            return palette
        }
    }

    abstract fun deserialize(cbt: CompoundBinaryTag): Schematic
}