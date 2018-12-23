package net.neferett.linaris.pvpswap.event.weather;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;

public class WeatherChange extends PvPSwapListener {

    public WeatherChange(final PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent event) {
        final World world = event.getWorld();
        if (!world.isThundering() && !world.hasStorm()) {
            event.setCancelled(true);
        }
    }
}
