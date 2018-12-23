package net.neferett.linaris.pvpswap.event.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;

public class ServerListPing extends PvPSwapListener {

    public ServerListPing(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        event.setMotd(Step.getMOTD());
    }
}
