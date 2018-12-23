package net.neferett.linaris.pvpswap.event.entity;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;

import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;

public class PotionSplash extends PvPSwapListener {

    public PotionSplash(PvPSwapPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (PvPSwapPlugin.godMode && event.getPotion().getShooter() instanceof Player) {
            Player damager = (Player) event.getPotion().getShooter();
            boolean player = false;
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    if (!player && entity != damager) {
                        player = true;
                    }
                    event.setIntensity(entity, 0);
                }
            }
            if (player) {
                damager.sendMessage(PvPSwapPlugin.prefix + ChatColor.RED + "Le pvp n'est pas activé.");
            }
        }
    }
}
