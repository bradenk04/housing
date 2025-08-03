package io.github.bradenk04.housing.domain
import io.github.bradenk04.housing.utils.schem.Schematic
import io.github.bradenk04.housing.utils.world.HousingWorldManager
import org.bukkit.Location
import java.util.*
import kotlin.math.absoluteValue


class House(
    val id: UUID,
    val theme: String,
    val data: Schematic,
    val owner: UUID
) {
    companion object {
        private const val HOUSE_SPACING = 1024
        val tempLoaded = mutableSetOf<House>()
    }
    val origin: Location

    init {
        val msb = id.mostSignificantBits
        val lsb = id.leastSignificantBits

        val xHash = (msb xor (msb ushr 32))
        val zHash = (lsb xor (lsb ushr 32))

        val x = (xHash % 20_000).toInt() * HOUSE_SPACING
        val z = (zHash % 20_000).toInt() * HOUSE_SPACING

        origin = Location(HousingWorldManager.world, x.toDouble(), 64.0, z.toDouble())
    }

    fun load() {
        data.paste(origin)
    }
}