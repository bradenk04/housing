package io.github.bradenk04.housing.utils

import io.github.bradenk04.housing.HousingPlugin
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

fun Player.sendMessage(message: Component) {
    val aud = HousingPlugin.adventure.player(this)
    aud.sendMessage(message)
}