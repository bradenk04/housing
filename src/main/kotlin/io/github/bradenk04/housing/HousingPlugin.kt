package io.github.bradenk04.housing

import io.github.bradenk04.housing.commands.PlayerCommands
import io.github.bradenk04.housing.database.Database
import io.github.bradenk04.housing.utils.world.HousingWorldManager
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.Lamp
import revxrsal.commands.bukkit.BukkitLamp
import revxrsal.commands.bukkit.actor.BukkitCommandActor

class HousingPlugin : JavaPlugin() {
    companion object {
        lateinit var plugin: HousingPlugin
    }

    private lateinit var lamp: Lamp<BukkitCommandActor>

    override fun onEnable() {
        if (!dataFolder.exists()) dataFolder.mkdirs()
        plugin = this
        registerCommands()
        HousingWorldManager.createWorld()

        Database.init()
    }
    override fun onDisable() {
        HousingWorldManager.destroyWorld()
    }

    private fun registerCommands() {
        lamp = BukkitLamp.builder(this).build()
        lamp.register(PlayerCommands())
    }
}