package org.openredstone.patchore

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

private const val PLUGIN_NAME = "PatchORE"

fun Player.sendInfo(message: String) {
    sendMessage(
        Component.text("[", NamedTextColor.DARK_GRAY)
            .append(Component.text(PLUGIN_NAME, NamedTextColor.GRAY))
            .append(Component.text("] ", NamedTextColor.DARK_GRAY))
            .append(Component.text(message, NamedTextColor.GOLD, TextDecoration.BOLD))
    )
}
