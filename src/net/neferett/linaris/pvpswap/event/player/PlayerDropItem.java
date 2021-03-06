package net.neferett.linaris.pvpswap.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.util.SpectatorUtils;

public class PlayerDropItem extends PvPSwapListener {

    public PlayerDropItem(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (Step.isStep(Step.LOBBY) || SpectatorUtils.isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
