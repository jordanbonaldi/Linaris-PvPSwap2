package net.neferett.linaris.pvpswap.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.util.SpectatorUtils;

public class FoodLevelChange extends PvPSwapListener {

    public FoodLevelChange(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (Step.isStep(Step.LOBBY) || SpectatorUtils.isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
