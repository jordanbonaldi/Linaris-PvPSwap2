package net.neferett.linaris.pvpswap.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import net.neferett.linaris.pvpswap.handler.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StatsUtils {
    @Getter
    private static Map<UUID, PlayerData> data = new HashMap<>();

    public static void loadData(Player player) {
        StatsUtils.data.put(player.getUniqueId(), new PlayerData(player.getUniqueId(), player.getName(), 0));
    }

    public static void addCoins(Player player, float coins) {
        StatsUtils.getData(player).addCoins(coins);
    }

    public static void showStatsMessage(Player player) {
        PlayerData data = StatsUtils.getData(player);
        player.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
        player.sendMessage(ChatColor.GOLD + "Fin de partie sur " + ChatColor.GREEN + "PvPSwap");
        player.sendMessage(ChatColor.GRAY + "Gain total de " + ChatColor.YELLOW + "FunCoins " + ChatColor.GRAY + "sur la partie : " + ChatColor.YELLOW + data.getCoins());
        player.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
    }

    public static PlayerData getData(Player player) {
        PlayerData data = StatsUtils.data.get(player.getUniqueId());
        if (data == null) {
            player.kickPlayer(ChatColor.RED + "Erreur");
            return null;
        }
        return data;
    }

    public static void removeData(Player player) {
        StatsUtils.data.remove(player.getUniqueId());
    }

}
