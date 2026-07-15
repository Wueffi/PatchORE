package org.openredstone.patchore.patch

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.openredstone.patchore.PatchORE
import org.openredstone.patchore.sendInfo

class NBTPatch() : Listener {

    private val maxNbtBytes = PatchORE.config.getInt("nbt.max_size_bytes")

    private val infoMSG = "The item's NBT Data was too complex and has been reset!"

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (validateAndReset(item)) {
            event.player.sendInfo(infoMSG)
        }
    }

    @EventHandler
    fun onPlayerConsume(event: PlayerItemConsumeEvent) {
        if (validateAndReset(event.item)) {
            event.player.sendInfo(infoMSG)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return
        if (validateAndReset(item)) {
            event.inventory.setItem(event.slot, item)
            (event.whoClicked as Player).sendInfo(infoMSG)
        }
    }

    private fun validateAndReset(item: ItemStack): Boolean {
        val nbtSize = item.serializeAsBytes().size
        if (nbtSize > maxNbtBytes) {
            resetItem(item)
            return true
        }

        return false
    }

    private fun resetItem(item: ItemStack) {
        val newMeta = Bukkit.getItemFactory().getItemMeta(item.type) ?: return
        item.itemMeta = newMeta
        item.amount = 1
    }
}
