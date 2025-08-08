package io.github.bradenk04.housing.utils.schem

import io.github.bradenk04.housing.HousingPlugin
import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.io.File
import java.io.FileInputStream
import java.util.UUID

object SchematicIO {
    private val PLOT_SCHEMATIC = File("${HousingPlugin.plugin.dataFolder}/template.schem")

    fun read(file: File): Schematic {
        return when (file.extension) {
            "schem" -> {
                readSpongeData(BinaryTagIO.unlimitedReader().read(FileInputStream(file), BinaryTagIO.Compression.GZIP))
            }
            else -> throw IllegalArgumentException("Unsupported file format")
        }
    }

    private fun readSpongeData(cbt: CompoundBinaryTag): Schematic {
        if (cbt.get("Schematic") != null) {
            return readSpongeData(cbt.getCompound("Schematic"))
        }
        val version = cbt.getInt("Version")
        return when (version) {
            3 -> SchematicVersion.SpongeV3Schematic().deserialize(cbt)
            else -> throw IllegalArgumentException("Unknown version $version")
        }
    }

    fun write(file: File, schematic: Schematic) {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        val data = SchematicVersion.SpongeV3Schematic().serialize(schematic)
        BinaryTagIO.writer().write(data, file.toPath(), BinaryTagIO.Compression.GZIP)
    }

    fun createFromTemplate(id: UUID): Schematic {
        val schematic = read(PLOT_SCHEMATIC)
        val schematicPath = "${HousingPlugin.plugin.dataFolder}/houses/${id}.schem"
        val schematicFile = File(schematicPath)

        write(schematicFile, schematic)
        return schematic
    }
}