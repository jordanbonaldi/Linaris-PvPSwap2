package net.neferett.linaris.pvpswap.event.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;

public class ChunkUnload extends PvPSwapListener {

    public ChunkUnload(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }
}
