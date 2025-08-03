package io.github.bradenk04.housing.domain
import io.github.bradenk04.housing.utils.schem.Schematic
import org.bukkit.Location
import java.util.*


class House(
    val id: UUID,
    val theme: String,
    val data: Schematic
) {
    fun load(location: Location) {
        data.paste(location)
    }
}