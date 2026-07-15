package org.openredstone.patchore.patch

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import org.openredstone.patchore.PatchORE
import org.openredstone.patchore.sendInfo
import kotlin.collections.iterator

class EnchantmentsPatch(val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun onItemUse(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (item.enchantments.isEmpty()) {
            return
        }

        var sendWarning = false

        for ((key, enchantPower) in item.enchantments) {
            val minimum = PatchORE.config.getInt("enchantments.${key.key.key}.minimum_enchantment")
            val maximum = PatchORE.config.getInt("enchantments.${key.key.key}.maximum_enchantment")

            if (enchantPower > maximum) {
                item.removeEnchantment(key)
                item.addUnsafeEnchantment(key, maximum)
                sendWarning = true
            } else if (enchantPower < minimum) {
                item.removeEnchantment(key)
                item.addUnsafeEnchantment(key, minimum)
                sendWarning = true
            }
        }

        if (sendWarning) {
            event.player.sendInfo(plugin, "We just filtOREd your enchantments :o")
        }
    }
}
