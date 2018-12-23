package net.neferett.linaris.pvpswap.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.bukkit.Location;

@Data
@AllArgsConstructor
public class MapLocation {
    private String map;
    private Location location;
}
