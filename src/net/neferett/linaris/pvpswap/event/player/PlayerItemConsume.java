package net.neferett.linaris.pvpswap.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.util.SpectatorUtils;

public class PlayerItemConsume extends PvPSwapListener {

    public PlayerItemConsume(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent evt) {
        if (Step.isStep(Step.LOBBY) || SpectatorUtils.isSpectator(evt.getPlayer())) {
            evt.setCancelled(true);
        }
    }
}
