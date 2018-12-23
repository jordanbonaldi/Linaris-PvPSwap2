package net.neferett.linaris.pvpswap.util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.neferett.linaris.pvpswap.PvPSwapItems;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.scheduler.GameRunnable;

public class SpectatorUtils {

    public static boolean isSpectator(Player player) {
        return player.hasMetadata("SPECTATOR") || Step.isStep(Step.IN_GAME) && GameRunnable.swapCount == 0;
    }

    public static void setSpectator(Plugin plugin, Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        player.setMetadata("SPECTATOR", new FixedMetadataValue(plugin, true));
        player.setAllowFlight(true);
        player.getInventory().setItem(0, PvPSwapItems.COMPASS_ITEM);
        player.getInventory().setItem(8, PvPSwapItems.HUB_ITEM);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (player != online) {
                player.showPlayer(online);
                if (!Step.isStep(Step.IN_GAME) || !SpectatorUtils.isSpectator(online)) {
                    online.hidePlayer(player);
                }
            }
        }
    }
}
