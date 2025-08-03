package io.github.bradenk04.housing.commands

import io.github.bradenk04.housing.HousingPlugin
import io.github.bradenk04.housing.domain.House
import io.github.bradenk04.housing.utils.schem.SchematicIO
import org.bukkit.permissions.PermissionDefault
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.annotation.CommandPermission
import revxrsal.commands.bukkit.annotation.FallbackPrefix
import java.io.File
import java.util.*


@Command("housing", "house")
@FallbackPrefix("housing")
class PlayerCommands {
    @Subcommand("create")
    @CommandPermission("housing.create", defaultAccess = PermissionDefault.TRUE)
    @Description("Create a new house.")
    fun create(actor: BukkitCommandActor) {
        val file = File("${HousingPlugin.plugin.dataFolder}/template.schem")

        val schem = SchematicIO.read(file)
        val house = House(UUID.randomUUID(), "none", schem, actor.requirePlayer().uniqueId)

        house.load()
        House.tempLoaded.add(house)
        // TODO("Creates a new house for the player or errors if they have too many.")
    }

    @Subcommand("explore")
    @CommandPermission("housing.explore", defaultAccess = PermissionDefault.TRUE)
    @Description("Explores public homes across the server.")
    fun explore(actor: BukkitCommandActor) {
        val playersHouses = House.tempLoaded.filter { it.owner == actor.requirePlayer().uniqueId }
        val house = playersHouses.first()
        house.load()

        actor.requirePlayer().teleport(house.spawnPoint)
        // TODO("Opens GUI or sends message of available houses")
    }

    @Subcommand("clear")
    fun clear(actor: BukkitCommandActor) {
        // NOTE: Temp command for testing without database
        val playersHouses = House.tempLoaded.filter { it.owner == actor.requirePlayer().uniqueId }
        val house = playersHouses.first()
        actor.requirePlayer().health = 0.0

        house.saveAndClear()
    }
}