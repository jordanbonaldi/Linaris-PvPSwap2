package net.neferett.linaris.pvpswap.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BungeeCordUtils {

    public static void teleportToLobby(Plugin plugin, Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("lobby");
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
