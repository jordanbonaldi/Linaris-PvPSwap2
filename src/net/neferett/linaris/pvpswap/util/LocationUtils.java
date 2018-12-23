package net.neferett.linaris.pvpswap.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {

    public static Location toLocation(String string) {
        String[] splitted = string.split("_");
        World world = Bukkit.getWorld(splitted[0]);
        return new Location(world, Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
    }

    public static String toString(Location location) {
        World world = location.getWorld();
        return world.getName() + "_" + location.getX() + "_" + location.getY() + "_" + location.getZ() + "_" + location.getYaw() + "_" + location.getPitch();
    }
}
