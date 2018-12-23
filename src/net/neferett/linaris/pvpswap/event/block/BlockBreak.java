package net.neferett.linaris.pvpswap.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.util.SpectatorUtils;

public class BlockBreak extends PvPSwapListener {

    public BlockBreak(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Step.isStep(Step.LOBBY) || SpectatorUtils.isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
