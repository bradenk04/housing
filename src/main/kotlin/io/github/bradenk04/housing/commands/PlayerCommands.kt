package io.github.bradenk04.housing.commands

import io.github.bradenk04.housing.database.Database
import io.github.bradenk04.housing.domain.House
import io.github.bradenk04.housing.utils.schem.SchematicIO
import io.github.bradenk04.housing.utils.sendMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
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
    suspend fun explore(actor: BukkitCommandActor) {
        val actorPlayer = actor.requirePlayer()
        val houses = Database.houseRepository.getHouses()

        var message = Component.text("")

        houses.forEach { house ->
            message = message.append(Component.text("${house.id}").clickEvent(ClickEvent.runCommand("house visit ${house.id}"))).appendSpace()
        }
        actorPlayer.sendMessage(message)
    }

    @Subcommand("visit")
    @CommandPermission("housing.visit", defaultAccess = PermissionDefault.TRUE)
    suspend fun visit(actor: BukkitCommandActor, house: String) {
        val actorPlayer = actor.requirePlayer()
        val houseId = UUID.fromString(house)
        val house = Database.houseRepository.getHouse(houseId)

        if (house == null) {
            actorPlayer.sendMessage(Component.text("House not found!", NamedTextColor.RED))
            return
        }

        house.load()
        actorPlayer.teleport(house.spawnPoint)
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