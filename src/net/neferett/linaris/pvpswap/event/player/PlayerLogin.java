package net.neferett.linaris.pvpswap.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;

public class PlayerLogin extends PvPSwapListener {

    public PlayerLogin(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!Step.isStep(Step.LOBBY) && !player.hasPermission("games.join")) {
            event.allow();
        } else if (!Step.canJoin()) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Step.getMOTD());
        }
    }
}
