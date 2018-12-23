package net.neferett.linaris.pvpswap.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.util.SpectatorUtils;

public class BlockPlace extends PvPSwapListener {

    public BlockPlace(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Step.isStep(Step.LOBBY) || SpectatorUtils.isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
