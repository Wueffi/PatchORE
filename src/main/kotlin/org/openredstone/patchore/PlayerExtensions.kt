package org.openredstone.patchore

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

fun Player.sendInfo(plugin: JavaPlugin, message: String) {
    sendMessage(
        Component.text("[", NamedTextColor.DARK_GRAY)
            .append(Component.text(plugin.name, NamedTextColor.GRAY))
            .append(Component.text("] ", NamedTextColor.DARK_GRAY))
            .append(Component.text(message, NamedTextColor.GOLD, TextDecoration.BOLD))
    )
}
