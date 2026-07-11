package org.openredstone.protoLib

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PlayerInventoryPacketHandler(plugin: JavaPlugin) :
    PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.ENTITY_NBT_QUERY) {

    override fun onPacketReceiving(event: PacketEvent) {
        if (event.packetType != PacketType.Play.Client.ENTITY_NBT_QUERY) {
            return
        }

        val doubles = event.packet.doubles
        if (doubles.read(0) < 2097151) {
            return
        }

        event.isCancelled = true
        plugin.server.scheduler.runTask(plugin, Runnable { clearNBT(event.player) })
    }

    private fun clearNBT(player: Player) {
        player.inventory.clear()

        player.sendMessage(
            Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text(plugin.name, NamedTextColor.GRAY))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(
                    Component.text(
                        "Your inventory was cleared due to an NBT overload.",
                        NamedTextColor.GOLD,
                        TextDecoration.BOLD
                    )
                )
        )
    }
}