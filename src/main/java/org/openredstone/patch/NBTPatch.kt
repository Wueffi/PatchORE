package org.openredstone.patch

import de.tr7zw.nbtapi.NBTItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.openredstone.PatchORE

class NBTPatch(plugin: JavaPlugin) : Patch(plugin), Listener {

    private val maxNbtBytes = PatchORE.config.getInt("nbt.max_size_bytes")
    private val maxLoreLines = PatchORE.config.getInt("nbt.max_lore_lines")
    private val forbiddenTags = PatchORE.config.getStringList("nbt.forbidden_tags")

    private val infoMSG = "The item's NBT Data was too complex and has been reset!"

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (validateAndReset(item)) {
            sendMessage(event.player, infoMSG)
        }
    }

    @EventHandler
    fun onPlayerConsume(event: PlayerItemConsumeEvent) {
        if (validateAndReset(event.item)) {
            sendMessage(event.player, infoMSG)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return
        if (validateAndReset(item)) {
            event.inventory.setItem(event.slot, item)
            sendMessage(event.whoClicked as org.bukkit.entity.Player, infoMSG)
        }
    }

    private fun validateAndReset(item: ItemStack): Boolean {
        val meta = item.itemMeta ?: return false

        val lore = meta.lore
        if (lore != null && lore.size > maxLoreLines) {
            resetItem(item)
            return true
        }

        val nbti = NBTItem(item)
        val nbtString = nbti.toString()

        if (nbtString.length > maxNbtBytes) {
            resetItem(item)
            return true
        }

        for (tag in forbiddenTags) {
            if (nbti.hasKey(tag)) {
                resetItem(item)
                return true
            }
        }

        return false
    }

    private fun resetItem(item: ItemStack) {
        val newMeta = org.bukkit.Bukkit.getItemFactory().getItemMeta(item.type) ?: return
        item.itemMeta = newMeta
        item.amount = 1
    }
}