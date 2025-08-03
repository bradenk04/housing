package io.github.bradenk04.housing.domain
import io.github.bradenk04.housing.HousingPlugin
import io.github.bradenk04.housing.utils.schem.Schematic
import io.github.bradenk04.housing.utils.schem.SchematicIO
import io.github.bradenk04.housing.utils.world.HousingWorldManager
import org.bukkit.Location
import org.bukkit.Material
import java.io.File
import java.util.*


class House(
    val id: UUID,
    val theme: String,
    data: Schematic,
    val owner: UUID,
    val plotSize: Int = 50,
) {
    companion object {
        private const val HOUSE_SPACING = 1024
        private const val THEME_PADDING = 256
        val tempLoaded = mutableSetOf<House>() // NOTE: Temp for testing without database
    }
    val origin: Location
    private val plotOrigin: Location
    val spawnPoint: Location

    var schematic: Schematic = data
        private set

    init {
        val msb = id.mostSignificantBits
        val lsb = id.leastSignificantBits

        val xHash = (msb xor (msb ushr 32))
        val zHash = (lsb xor (lsb ushr 32))

        val x = (xHash % 20_000).toInt() * HOUSE_SPACING
        val z = (zHash % 20_000).toInt() * HOUSE_SPACING

        origin = Location(HousingWorldManager.world, x.toDouble(), -64.0, z.toDouble())
        plotOrigin = Location(HousingWorldManager.world, x.toDouble() + THEME_PADDING, -64.0, z.toDouble() + THEME_PADDING)
        spawnPoint = Location(HousingWorldManager.world, x.toDouble() + THEME_PADDING + (plotSize / 2), 64.0, z.toDouble() + THEME_PADDING + (plotSize / 2))
    }

    fun load() {
        schematic.paste(plotOrigin)
    }

    fun saveAndClear() {
        save()
        clearArea()
    }

    fun save() {
        val loc1 = Location(HousingWorldManager.world, plotOrigin.x, -64.0, plotOrigin.z)
        val loc2 = Location(HousingWorldManager.world, plotOrigin.x + plotSize, 320.0, plotOrigin.z + plotSize)

        val schem = Schematic.load(loc1, loc2)
        SchematicIO.write(File("${HousingPlugin.plugin.dataFolder}/houses/${id}.schem"), schem)
        schematic = schem
    }

    fun clearArea() {
        val offset = (THEME_PADDING * 2) + plotSize

        val loc1 = Location(HousingWorldManager.world, origin.x, -64.0, origin.z)
        val loc2 = Location(HousingWorldManager.world, origin.x + offset, 320.0, origin.z + offset)

        val minX: Int = loc1.blockX.coerceAtMost(loc2.blockX)
        val maxX: Int = loc1.blockX.coerceAtLeast(loc2.blockX)
        val minY: Int = loc1.blockY.coerceAtMost(loc2.blockY)
        val maxY: Int = loc1.blockY.coerceAtLeast(loc2.blockY)
        val minZ: Int = loc1.blockZ.coerceAtMost(loc2.blockZ)
        val maxZ: Int = loc1.blockZ.coerceAtLeast(loc2.blockZ)

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    HousingWorldManager.world.getBlockAt(x, y, z).setType(Material.AIR, false)
                }
            }
        }
    }
}