package net.neferett.linaris.pvpswap.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.util.SpectatorUtils;

public class EntityDamageByPlayer extends PvPSwapListener {

    public EntityDamageByPlayer(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!Step.isStep(Step.IN_GAME) || event.getDamager() instanceof Player && SpectatorUtils.isSpectator((Player) event.getDamager())) {
            event.setCancelled(true);
        }
    }
}
