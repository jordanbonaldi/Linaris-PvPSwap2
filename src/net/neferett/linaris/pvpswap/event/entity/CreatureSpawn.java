package net.neferett.linaris.pvpswap.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;

public class CreatureSpawn extends PvPSwapListener {

    public CreatureSpawn(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }
}
