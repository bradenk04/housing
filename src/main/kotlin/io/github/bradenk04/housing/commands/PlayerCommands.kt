package io.github.bradenk04.housing.commands

import org.bukkit.permissions.PermissionDefault
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.annotation.CommandPermission
import revxrsal.commands.bukkit.annotation.FallbackPrefix

@Command("housing", "house")
@FallbackPrefix("housing")
class PlayerCommands {
    @Subcommand("create")
    @CommandPermission("housing.create", defaultAccess = PermissionDefault.NOT_OP)
    @Description("Create a new house.")
    fun create(actor: BukkitCommandActor) {
        TODO("Creates a new house for the player or errors if they have too many.")
    }

    @Subcommand("explore")
    @CommandPermission("housing.explore", defaultAccess = PermissionDefault.NOT_OP)
    @Description("Explores public homes across the server.")
    fun explore(actor: BukkitCommandActor) {
        TODO("Opens GUI or sends message of available houses")
    }
}