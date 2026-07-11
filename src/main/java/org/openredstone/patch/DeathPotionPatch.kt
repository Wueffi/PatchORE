package org.openredstone.patch

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.LingeringPotionSplashEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.openredstone.PatchORE

class DeathPotionsPatch(plugin: JavaPlugin) : Patch(plugin), Listener {

    private val maxAmplifier = PatchORE.config.getInt("deathpotions.max_amplifier")
    private val maxDuration = PatchORE.config.getInt("deathpotions.max_duration")

    private val safeEffectsSet = setOf(
        //PotionEffectType.POISON,
        //PotionEffectType.WITHER,
        //PotionEffectType.HEALTH_BOOST,
        //PotionEffectType.ABSORPTION,
        //PotionEffectType.INSTANT_HEALTH,
        //PotionEffectType.INSTANT_DAMAGE,
        //PotionEffectType.REGENERATION,
        //PotionEffectType.STRENGTH,
        //PotionEffectType.SPEED,
        //PotionEffectType.DOLPHINS_GRACE,
        //PotionEffectType.NAUSEA,
        PotionEffectType.SLOWNESS,
        PotionEffectType.HASTE,
        PotionEffectType.MINING_FATIGUE,
        PotionEffectType.JUMP_BOOST,
        PotionEffectType.RESISTANCE,
        PotionEffectType.FIRE_RESISTANCE,
        PotionEffectType.WATER_BREATHING,
        PotionEffectType.INVISIBILITY,
        PotionEffectType.BLINDNESS,
        PotionEffectType.NIGHT_VISION,
        PotionEffectType.HUNGER,
        PotionEffectType.WEAKNESS,
        PotionEffectType.SATURATION,
        PotionEffectType.GLOWING,
        PotionEffectType.LEVITATION,
        PotionEffectType.LUCK,
        PotionEffectType.UNLUCK,
        PotionEffectType.SLOW_FALLING,
        PotionEffectType.CONDUIT_POWER,
        PotionEffectType.BAD_OMEN,
        PotionEffectType.HERO_OF_THE_VILLAGE
    )

    @EventHandler
    fun consumePotionEvent(event: PlayerItemConsumeEvent) {
        if (event.item.type != Material.POTION) {
            return
        }

        val meta = event.item.itemMeta as PotionMeta
        if (hasLegalEffects(meta.customEffects)) {
            return
        }

        event.isCancelled = true
        sendMessage(event.player, "We just saved you from possible death. You're welcome.")
    }

    @EventHandler
    fun splashPotionEvent(event: PotionSplashEvent) {
        if (hasLegalEffects(event.potion.effects)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun lingeringSplashPotionEvent(event: LingeringPotionSplashEvent) {
        if (hasLegalEffects(event.areaEffectCloud.customEffects)) {
            return
        }

        event.isCancelled = true
    }

    private fun hasLegalEffects(potionEffects: Collection<PotionEffect>): Boolean {
        for (effect in potionEffects) {
            if (safeEffectsSet.contains(effect.type)) {
                continue
            }

            if (effect.type == PotionEffectType.INSTANT_HEALTH && effect.amplifier > maxAmplifier) {
                return false
            }

            if (effect.duration > maxDuration) {
                return false
            }
        }
        return true
    }
}