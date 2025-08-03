package io.github.bradenk04.housing.utils.schem

import net.kyori.adventure.nbt.CompoundBinaryTag
import java.io.ByteArrayOutputStream
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

        override fun serialize(schem: Schematic): CompoundBinaryTag {
            val root = CompoundBinaryTag.builder()

            root.putInt("Version", 3)
            root.putInt("DataVersion", 4440)
            root.putShort("Width", schem.width.toShort())
            root.putShort("Height", schem.height.toShort())
            root.putShort("Length", schem.length.toShort())

            val paletteMap = mutableMapOf<String, Int>()
            val blocksDataStream = ByteArrayOutputStream()

            val sortedBlocks = schem.blocks.sortedWith(compareBy<RelativeBlock> { it.y }.thenBy { it.z }.thenBy { it.x })

            var paletteIndex = 0
            for (block in sortedBlocks) {
                val state = block.blockData.toString()

                val blockId = paletteMap.getOrPut(state) {
                    paletteIndex++
                }

                writeVarInt(blocksDataStream, blockId)
            }

            val paletteTag = CompoundBinaryTag.builder()
            paletteMap.forEach { (state, id) ->
                paletteTag.putInt(state, id)
            }

            val blocksTag = CompoundBinaryTag.builder()
            blocksTag.put("Palette", paletteTag.build())
            blocksTag.putByteArray("Data", blocksDataStream.toByteArray())

            root.put("Blocks", blocksTag.build())

            return root.build()
        }

        private fun writeVarInt(stream: ByteArrayOutputStream, value: Int) {
            var temp = value
            while ((temp and 0b11111110) != 0) {
                stream.write(temp and 0b01111111 or 0b10000000)
                temp = temp ushr 7
            }
            stream.write(temp)
        }
    }

    fun deserialize(cbt: CompoundBinaryTag): Schematic
    fun serialize(schem: Schematic): CompoundBinaryTag
}