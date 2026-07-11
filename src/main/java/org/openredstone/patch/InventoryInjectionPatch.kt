package org.openredstone.patch

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.plugin.java.JavaPlugin
import org.openredstone.PatchORE

class InventoryInjectionPatch(plugin: JavaPlugin) : Patch(plugin), Listener {

    private val enabled = PatchORE.config.getBoolean("patches.inventoryinjection")

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (!enabled) {
            return
        }

        val targetEntity = event.rightClicked as? Player ?: return
        val inventoryBefore = targetEntity.inventory.contents.map { it?.toString() ?: "EMPTY" }

        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            val inventoryAfter = targetEntity.inventory.contents.map { it?.toString() ?: "EMPTY" }

            for (i in inventoryBefore.indices) {
                if (inventoryBefore[i] == "EMPTY" && inventoryAfter[i] != "EMPTY") {
                    targetEntity.inventory.clear(i)

                    sendMessage(targetEntity, "An item was blocked from being added to your inventory.")
                    return@Runnable
                }
            }
        }, 1L)
    }
}