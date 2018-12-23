package net.neferett.linaris.pvpswap.event.weather;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.ThunderChangeEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;

public class ThunderChange extends PvPSwapListener {

    public ThunderChange(final PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onThunderChange(final ThunderChangeEvent event) {
        if (event.toThunderState()) {
            event.setCancelled(true);
        }
    }
}
