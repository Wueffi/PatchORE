package org.openredstone.patchore.patch

import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.openredstone.patchore.PatchORE

class FireworksPatch(val plugin: JavaPlugin) : Listener {

    private var fireworkCount = 0
    private val maxCount = PatchORE.config.getInt("fireworks.count")
    private val maxPower = PatchORE.config.getInt("fireworks.power")
    private val maxEffectsCount = PatchORE.config.getInt("fireworks.effects_count")

    @EventHandler
    fun onFireworksShoot(event: EntityShootBowEvent) {
        if (event.projectile.type != EntityType.FIREWORK_ROCKET) {
            return
        }

        if (event.isCancelled) {
            return
        }

        if (fireworkCount >= maxCount) {
            event.isCancelled = true
            return
        }

        fireworkCount++
        val firework = event.projectile as Firework
        val fireworkMeta = firework.fireworkMeta

        filterPower(fireworkMeta)
        filterEffects(fireworkMeta)

        firework.fireworkMeta = fireworkMeta
        event.projectile = firework

        startDecrementTimer(fireworkMeta.power)
    }

    @EventHandler
    fun onFireworksSpawn(event: EntitySpawnEvent) {
        if (event.entityType != EntityType.FIREWORK_ROCKET) {
            return
        }

        event.isCancelled = false

        val firework = event.entity as Firework
        val fireworkMeta = firework.fireworkMeta

        filterEffects(fireworkMeta)
        filterPower(fireworkMeta)

        firework.fireworkMeta = fireworkMeta
    }

    @EventHandler
    fun onFireworksUse(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (item.type != Material.FIREWORK_ROCKET) {
            return
        }

        if (event.isCancelled) {
            return
        }

        if (fireworkCount >= maxCount) {
            event.isCancelled = true
            return
        }

        fireworkCount++
        val meta = item.itemMeta as FireworkMeta

        filterPower(meta)
        filterEffects(meta)
        item.itemMeta = meta

        startDecrementTimer(meta.power)
    }

    @EventHandler
    fun onFireworkDispense(event: BlockDispenseEvent) {
        if (event.item.type != Material.FIREWORK_ROCKET) {
            return
        }

        if (event.isCancelled) {
            return
        }

        if (fireworkCount >= maxCount) {
            event.isCancelled = true
            return
        }

        fireworkCount++
        val meta = event.item.itemMeta as FireworkMeta

        filterPower(meta)
        filterEffects(meta)
        event.item.itemMeta = meta

        startDecrementTimer(meta.power)
    }

    private fun startDecrementTimer(power: Int) {
        object : BukkitRunnable() {
            override fun run() {
                fireworkCount--
            }
        }.runTaskLater(plugin, (20 * power).toLong())
    }

    private fun filterPower(meta: FireworkMeta): FireworkMeta {
        if (meta.power > maxPower) {
            meta.power = maxPower
        }

        return meta
    }

    private fun filterEffects(meta: FireworkMeta): FireworkMeta {
        if (meta.hasEffects() && meta.effectsSize > maxEffectsCount) {
            val sublist: Iterable<FireworkEffect> = meta.effects.subList(0, maxEffectsCount)

            meta.clearEffects()
            meta.addEffects(sublist)
        }

        return meta
    }
}
