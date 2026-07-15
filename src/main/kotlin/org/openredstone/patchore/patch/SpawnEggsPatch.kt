package org.openredstone.patchore.patch

import de.tr7zw.nbtapi.NBTType
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.openredstone.patchore.PatchORE
import de.tr7zw.nbtapi.NBTItem
import org.openredstone.patchore.sendInfo

class SpawnEggsPatch(val plugin: JavaPlugin) : Listener {

    private val eggTypes: List<Material> = Material.entries.filter { it.key.key.endsWith("_egg") }

    @EventHandler
    fun onSpawnEggDispense(event: BlockDispenseEvent) {
        if (!eggTypes.contains(event.item.type)) {
            return
        }

        if (hasLegalTags(event.item)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun onSpawnEggUse(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val item = event.item ?: return
        if (!eggTypes.contains(item.type)) {
            return
        }

        if (hasLegalTags(item)) {
            return
        }

        event.player.sendInfo(plugin, "Yeah, no. Enjoy your fixed egg.")

        event.isCancelled = true
        val replacement = ItemStack(item.type)

        if (eggTypes.contains(event.player.inventory.itemInMainHand.type)) {
            event.player.inventory.setItemInMainHand(replacement)
        } else {
            event.player.inventory.setItemInOffHand(replacement)
        }
    }

    private fun hasLegalTags(item: ItemStack): Boolean {
        val nbti = NBTItem(item)

        if (!nbti.hasKey("EntityTag")) {
            return true
        }

        val configurationSection = PatchORE.config.getConfigurationSection("spawneggs.entity") ?: return true
        val keys = configurationSection.getKeys(false)
        val entityCompound = nbti.getCompound("EntityTag") ?: return true

        for (key in keys) {
            if (entityCompound.hasKey("id") && entityCompound.getString("id") == "minecraft:$key") {
                if (configurationSection.getBoolean("$key.block")) {
                    return false
                }

                val entitySection = configurationSection.getConfigurationSection(key) ?: continue
                for (subKey in entitySection.getKeys(false)) {
                    val formattedKey = getCamelCase(subKey)
                    if (!entityCompound.hasKey(formattedKey)) {
                        continue
                    }

                    when (entityCompound.getType(formattedKey)) {
                        NBTType.NBTTagInt -> {
                            val value = entityCompound.getInteger(formattedKey)
                            if (value > configurationSection.getInt("$key.$subKey")) {
                                return false
                            }
                        }

                        NBTType.NBTTagString -> {
                            val value = entityCompound.getString(formattedKey)
                            if (value == configurationSection.getString("$key.$subKey")) {
                                return false
                            }
                        }

                        else -> {}
                    }
                }
            }
        }

        return true
    }

    private fun getCamelCase(underScore: String): String {
        val camel = Regex("_(.)").replace(underScore) { it.groupValues[1].uppercase() }
        return camel.replaceFirstChar { it.uppercase() }
    }
}
