package io.github.bradenk04.housing.commands

import io.github.bradenk04.housing.database.Database
import io.github.bradenk04.housing.domain.House
import io.github.bradenk04.housing.utils.schem.SchematicIO
import org.bukkit.permissions.PermissionDefault
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.annotation.CommandPermission
import revxrsal.commands.bukkit.annotation.FallbackPrefix
import java.util.*


@Command("housing", "house")
@FallbackPrefix("housing")
class PlayerCommands {
    @Subcommand("create")
    @CommandPermission("housing.create", defaultAccess = PermissionDefault.TRUE)
    @Description("Create a new house.")
    suspend fun create(actor: BukkitCommandActor) {
        val creator = actor.requirePlayer()

        val houseId = UUID.randomUUID()
        val theme = "" // TODO: In GUI make this selectable
        val plotSize = 50
        val defaultTags = listOf<String>()

        val newHouse = House(houseId, theme, SchematicIO.createFromTemplate(houseId), creator.uniqueId, plotSize, defaultTags)
        Database.houseRepository.createHouse(newHouse)
        newHouse.load()

        creator.teleport(newHouse.spawnPoint)
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