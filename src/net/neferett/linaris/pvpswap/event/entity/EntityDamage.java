package net.neferett.linaris.pvpswap.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;

public class EntityDamage extends PvPSwapListener {

    public EntityDamage(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        }
    }
}
