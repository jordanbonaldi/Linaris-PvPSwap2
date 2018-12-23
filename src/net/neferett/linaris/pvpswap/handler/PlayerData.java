package net.neferett.linaris.pvpswap.handler;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Data
@AllArgsConstructor
public class PlayerData {
    private UUID uuid;
    private String name;
    private double coins;

    public void addCoins(double coins) {
        Player player = Bukkit.getPlayer(name);
        if (player != null && player.isOnline()) {
            this.coins += player.hasPermission("funcoins.mvpplus") ? coins * 4 : player.hasPermission("funcoins.mvp") ? coins * 3 : player.hasPermission("funcoins.vip") ? coins * 2 : coins;
            Bukkit.getPlayer(name).sendMessage(ChatColor.GRAY + "Gain de FunCoins + " + ChatColor.GOLD + String.valueOf(coins).replace(".", ","));
        }
    }
}
