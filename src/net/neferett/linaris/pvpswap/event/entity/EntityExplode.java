package net.neferett.linaris.pvpswap.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;

public class EntityExplode extends PvPSwapListener {

    public EntityExplode(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        }
    }
}
