package net.neferett.linaris.pvpswap.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.util.SpectatorUtils;

public class PlayerRespawn extends PvPSwapListener {

    public PlayerRespawn(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!Step.isStep(Step.LOBBY)) {
            event.setRespawnLocation(plugin.getLobbyLocation());
            final Player player = event.getPlayer();
            new BukkitRunnable() {

                @Override
                public void run() {
                    SpectatorUtils.setSpectator(plugin, player);
                    if (player.hasMetadata("DEATH")) {
                        player.teleport((Location) player.getMetadata("DEATH").get(0).value());
                        player.setFlying(true);
                        player.removeMetadata("DEATH", plugin);
                    }
                }
            }.runTaskLater(plugin, 1);
        }
    }
}
