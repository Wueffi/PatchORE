package org.openredstone.patch

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Piston
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class ExtendedPistonsPatch(plugin: JavaPlugin) : Patch(plugin), Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.isCancelled) {
            return
        }

        val material = event.block.blockData.material
        if (material != Material.PISTON && material != Material.STICKY_PISTON) {
            return
        }

        if (!(event.blockPlaced.blockData as Piston).isExtended) {
            return
        }

        val placeLocation = event.block.location
        val facing = (event.block.blockData as Piston).facing

        event.isCancelled = true
        setBlockPlaced(placeLocation, material, facing)

        sendMessage(event.player, "Now that is one long piston.")
    }

    private fun setBlockPlaced(location: Location, material: Material, facing: BlockFace) {
        object : BukkitRunnable() {
            override fun run() {
                val world = location.world ?: return
                world.getBlockAt(location).type = material

                val pistonData = world.getBlockAt(location).blockData as Piston
                pistonData.facing = facing

                world.getBlockAt(location).blockData = pistonData
            }
        }.runTaskLater(plugin, 2L)
    }
}