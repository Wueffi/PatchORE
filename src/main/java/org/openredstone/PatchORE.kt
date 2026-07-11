package org.openredstone

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.openredstone.patch.*
import org.openredstone.protoLib.PlayerPositionPacketHandler
import org.openredstone.protoLib.PlayerInventoryPacketHandler

import java.io.File
import java.util.logging.Level

class PatchORE : JavaPlugin() {

    companion object {
        lateinit var config: FileConfiguration
    }

    private lateinit var protocolManager: ProtocolManager

    private val defaults: Map<String, Any> = mapOf(
        // Patch options
        "patches.fireworks" to true,
        "patches.enchantments" to true,
        "patches.extendedpistons" to true,
        "patches.spawneggs" to true,
        "patches.deathpotions" to true,
        "patches.void" to true,
        "patches.nbt" to true,

        // Fireworks options
        "fireworks.power" to 5,
        "fireworks.count" to 50,
        "fireworks.effects_count" to 5,

        // Enchantment options
        "enchantments.power.minimum_enchantment" to 0,
        "enchantments.power.maximum_enchantment" to 100,
        "enchantments.flame.minimum_enchantment" to 0,
        "enchantments.flame.maximum_enchantment" to 100,
        "enchantments.infinity.minimum_enchantment" to 0,
        "enchantments.infinity.maximum_enchantment" to 100,
        "enchantments.punch.minimum_enchantment" to 0,
        "enchantments.punch.maximum_enchantment" to 100,
        "enchantments.binding_curse.minimum_enchantment" to 0,
        "enchantments.binding_curse.maximum_enchantment" to 100,
        "enchantments.channeling.minimum_enchantment" to 0,
        "enchantments.channeling.maximum_enchantment" to 100,
        "enchantments.sharpness.minimum_enchantment" to 0,
        "enchantments.sharpness.maximum_enchantment" to 100,
        "enchantments.bane_of_arthropods.minimum_enchantment" to 0,
        "enchantments.bane_of_arthropods.maximum_enchantment" to 100,
        "enchantments.smite.minimum_enchantment" to 0,
        "enchantments.smite.maximum_enchantment" to 100,
        "enchantments.depth_strider.minimum_enchantment" to 0,
        "enchantments.depth_strider.maximum_enchantment" to 100,
        "enchantments.efficiency.minimum_enchantment" to 0,
        "enchantments.efficiency.maximum_enchantment" to 100,
        "enchantments.unbreaking.minimum_enchantment" to 0,
        "enchantments.unbreaking.maximum_enchantment" to 100,
        "enchantments.fire_aspect.minimum_enchantment" to 0,
        "enchantments.fire_aspect.maximum_enchantment" to 100,
        "enchantments.frost_walker.minimum_enchantment" to 0,
        "enchantments.frost_walker.maximum_enchantment" to 100,
        "enchantments.impaling.minimum_enchantment" to 0,
        "enchantments.impaling.maximum_enchantment" to 100,
        "enchantments.knockback.minimum_enchantment" to 0,
        "enchantments.knockback.maximum_enchantment" to 100,
        "enchantments.fortune.minimum_enchantment" to 0,
        "enchantments.fortune.maximum_enchantment" to 100,
        "enchantments.looting.minimum_enchantment" to 0,
        "enchantments.looting.maximum_enchantment" to 100,
        "enchantments.loyalty.minimum_enchantment" to 0,
        "enchantments.loyalty.maximum_enchantment" to 100,
        "enchantments.luck_of_the_sea.minimum_enchantment" to 0,
        "enchantments.luck_of_the_sea.maximum_enchantment" to 100,
        "enchantments.lure.minimum_enchantment" to 0,
        "enchantments.lure.maximum_enchantment" to 100,
        "enchantments.mending.minimum_enchantment" to 0,
        "enchantments.mending.maximum_enchantment" to 100,
        "enchantments.multishot.minimum_enchantment" to 0,
        "enchantments.multishot.maximum_enchantment" to 100,
        "enchantments.respiration.minimum_enchantment" to 0,
        "enchantments.respiration.maximum_enchantment" to 100,
        "enchantments.piercing.minimum_enchantment" to 0,
        "enchantments.piercing.maximum_enchantment" to 100,
        "enchantments.protection.minimum_enchantment" to 0,
        "enchantments.protection.maximum_enchantment" to 100,
        "enchantments.blast_protection.minimum_enchantment" to 0,
        "enchantments.blast_protection.maximum_enchantment" to 100,
        "enchantments.feather_falling.minimum_enchantment" to 0,
        "enchantments.feather_falling.maximum_enchantment" to 100,
        "enchantments.fire_protection.minimum_enchantment" to 0,
        "enchantments.fire_protection.maximum_enchantment" to 100,
        "enchantments.projectile_protection.minimum_enchantment" to 0,
        "enchantments.projectile_protection.maximum_enchantment" to 100,
        "enchantments.quick_charge.minimum_enchantment" to 0,
        "enchantments.quick_charge.maximum_enchantment" to 100,
        "enchantments.riptide.minimum_enchantment" to 0,
        "enchantments.riptide.maximum_enchantment" to 10,
        "enchantments.silk_touch.minimum_enchantment" to 0,
        "enchantments.silk_touch.maximum_enchantment" to 10,
        "enchantments.sweeping.minimum_enchantment" to 0,
        "enchantments.sweeping.maximum_enchantment" to 10,
        "enchantments.thorns.minimum_enchantment" to 0,
        "enchantments.thorns.maximum_enchantment" to 10,
        "enchantments.vanishing_curse.minimum_enchantment" to 0,
        "enchantments.vanishing_curse.maximum_enchantment" to 100,
        "enchantments.aqua_affinity.minimum_enchantment" to 0,
        "enchantments.aqua_affinity.maximum_enchantment" to 100,

        // SpawnEggs options
        "spawneggs.entity.potion.block" to true,
        "spawneggs.entity.fireball.block" to false,
        "spawneggs.entity.fireball.explosion_power" to 500,
        "spawneggs.entity.fireball.explosion_radius" to 10,

        // DeathPotion options
        "deathpotions.max_amplifier" to 32,
        "deathpotions.max_duration" to 1000
    )

    override fun onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager()
        setupConfig()
        loadPatches()
    }

    private fun loadPatches() {
        if (config.getBoolean("patches.fireworks")) {
            server.pluginManager.registerEvents(FireworksPatch(this), this)
        }
        if (config.getBoolean("patches.enchantments")) {
            server.pluginManager.registerEvents(EnchantmentPatch(this), this)
        }
        if (config.getBoolean("patches.extendedpistons")) {
            server.pluginManager.registerEvents(ExtendedPistonsPatch(this), this)
        }
        if (config.getBoolean("patches.spawneggs")) {
            server.pluginManager.registerEvents(SpawnEggsPatch(this), this)
        }
        if (config.getBoolean("patches.deathpotions")) {
            server.pluginManager.registerEvents(DeathPotionsPatch(this), this)
        }
        if (config.getBoolean("patches.void")) {
            protocolManager.addPacketListener(PlayerPositionPacketHandler(this))
        }
        if (config.getBoolean("patches.nbt")) {
            protocolManager.addPacketListener(PlayerInventoryPacketHandler(this))
        }
    }

    private fun setupConfig() {
        try {
            PatchORE.config = this.config
            if (!dataFolder.exists()) {
                if (!dataFolder.mkdir()) {
                    logger.log(Level.SEVERE, "Unable to make plugin directory.")
                    this.isEnabled = false
                }
            }
            val file = File(dataFolder, "config.yml")

            if (!file.exists()) {
                logger.info("Config.yml not found, creating.")
            } else {
                logger.info("Config.yml found, loading.")
            }

            defaults.forEach { (key, value) -> config.addDefault(key, value) }

            config.options().copyDefaults(true)
            saveConfig()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}