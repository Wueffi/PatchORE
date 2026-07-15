package org.openredstone.patchore.patch

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.openredstone.patchore.sendInfo

class InventoryInjectionPatch(val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {

        val targetEntity = event.rightClicked as? Player ?: return

        val itemUsed = event.player.inventory.getItem(event.hand)
        if (!isEquipOnInteract(itemUsed)) {
            return
        }

        targetEntity.sendInfo(plugin, "An item was blocked from being added to your inventory.")

        event.isCancelled = true
    }

    private fun isEquipOnInteract(item: ItemStack): Boolean {
        return item.getData(DataComponentTypes.EQUIPPABLE)?.equipOnInteract() ?: false
    }
}
