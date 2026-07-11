package org.openredstone.patch

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

open class Patch(val plugin: JavaPlugin) {

    fun sendMessage(player: Player, message: String) {
        player.sendMessage(
            Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text(plugin.name, NamedTextColor.GRAY))
                .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                .append(Component.text(message, NamedTextColor.GOLD, TextDecoration.BOLD))
        )
    }
}