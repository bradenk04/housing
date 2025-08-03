package io.github.bradenk04.housing.utils.schem

import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.io.File
import java.io.FileInputStream

object SchematicIO {
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
}