package net.neferett.linaris.pvpswap.scheduler;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.MapLocation;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.util.MathUtils;
import net.neferett.linaris.pvpswap.util.StatsUtils;

public class GameRunnable extends BukkitRunnable {
    private PvPSwapPlugin plugin;
    public static int swapCount = 0;
    private int nextSwapAt = 0;
    private int totalSeconds = 0;
    private int minutes = 0, seconds = 0;
    private Objective pvpSwap;

    public GameRunnable(PvPSwapPlugin plugin, Objective pvpSwap) {
        this.plugin = plugin;
        this.pvpSwap = pvpSwap;
        this.runTaskTimer(PvPSwapPlugin.getInstance(), 0, 20);
    }

    public void doSwap() {
        minutes = seconds = totalSeconds = 0;
        Set<Player> alivePlayers = plugin.getAlivePlayers();
        final boolean finalSwap = GameRunnable.swapCount >= 2 && alivePlayers.size() <= plugin.getFinalSpawns().size();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (finalSwap) {
                online.sendMessage(PvPSwapPlugin.prefix + ChatColor.GOLD + "Quel bruit horrible ! Mais étrangement, vous pétez la forme... Ceci était le dernier swap, que le meilleur gagne !");
            } else if (PvPSwapPlugin.duelMode && GameRunnable.swapCount == 0) {
                for (int i = 0; i < 10; i++) {
                    online.sendMessage("");
                }
                online.sendMessage(ChatColor.RED + "Têtes retirées pour la map Duels.");
            }
            if (alivePlayers.contains(online)) {
                online.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
                if (!finalSwap) {
                    online.playSound(online.getLocation(), Sound.PORTAL_TRIGGER, 1, 1);
                } else {
                    online.playSound(online.getLocation(), Sound.ENDERDRAGON_DEATH, 1, 1);
                    StatsUtils.addCoins(online, 10);
                }
            }
        }
        nextSwapAt = finalSwap ? -1 : (GameRunnable.swapCount == 0 ? PvPSwapPlugin.duelMode ? 11 : 120 : MathUtils.random(PvPSwapPlugin.duelMode ? 60 : 120, PvPSwapPlugin.duelMode ? 120 : 180)) - 3;
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : plugin.getAlivePlayers()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
                }
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        final List<MapLocation> mapLocations = plugin.getUnusedSpawns();
                        final Set<Player> alivePlayers = plugin.getAlivePlayers();
                        for (final Player alive : alivePlayers) {
                            if (finalSwap) {
                                alive.setMaxHealth(30.0D);
                                alive.setHealth(30.0D);
                            } else if (GameRunnable.swapCount == 0) {
                                alive.sendMessage(PvPSwapPlugin.prefix + ChatColor.RED + "Pvp désactivé ! " + ChatColor.GRAY + "Prochain swap dans " + ChatColor.GOLD + "2m00s" + ChatColor.GRAY + ", Ouvrez les " + ChatColor.GOLD + "coffres " + ChatColor.GRAY + "!");
                                alive.sendMessage(PvPSwapPlugin.prefix + ChatColor.GRAY + "Activez votre " + ChatColor.RED + "son " + ChatColor.GRAY + "! A chaque swap, un " + ChatColor.RED + "coup de canon " + ChatColor.GRAY + "indique la présence d'un " + ChatColor.RED + "adversaire" + ChatColor.GRAY + ".");
                            } else if (GameRunnable.swapCount == 1) {
                                PvPSwapPlugin.godMode = false;
                                alive.sendMessage(PvPSwapPlugin.prefix + ChatColor.RED + "Pvp activé ! " + ChatColor.GOLD + "Préparez votre épée !");
                                alive.sendMessage(PvPSwapPlugin.prefix + ChatColor.GOLD + "Vous serez régulièrement swapé toutes les " + ChatColor.BLUE + "2m00s" + ChatColor.GOLD + " à " + ChatColor.BLUE + "3m00s" + ChatColor.GOLD + ".");
                            }
                            alive.setFallDistance(0);
                            if (finalSwap) {
                                alive.teleport(plugin.getFinalSpawn());
                            } else {
                                MapLocation mapLocation = mapLocations.remove(MathUtils.random(mapLocations.size() - 1));
                                final String map = mapLocation.getMap();
                                if (alive.hasMetadata("MAP")) {
                                    alive.removeMetadata("MAP", plugin);
                                }
                                alive.setMetadata("MAP", new FixedMetadataValue(plugin, map));
                                new BukkitRunnable() {
                                    int count = finalSwap ? alivePlayers.size() : plugin.getOnMap(map);
                                    int loopCount = 0;

                                    @Override
                                    public void run() {
                                        if (loopCount == count) {
                                            this.cancel();
                                            return;
                                        }
                                        alive.playSound(alive.getLocation(), Sound.EXPLODE, 1, 0.5F);
                                        loopCount++;
                                    }
                                }.runTaskTimer(plugin, 20, 20);
                                alive.teleport(mapLocation.getLocation());
                            }
                            alive.playSound(alive.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                        }
                        GameRunnable.swapCount = finalSwap ? -1 : GameRunnable.swapCount + 1;
                    }
                }.runTaskLater(plugin, 20);
            }
        }.runTaskLater(plugin, 40);
    }

    @Override
    public void run() {
        if (!Step.isStep(Step.IN_GAME)) {
            this.cancel();
            return;
        }
        pvpSwap.setDisplayName("PvPSwap " + ChatColor.RED + "0" + minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
        if (totalSeconds == nextSwapAt) {
            this.doSwap();
        }
        totalSeconds++;
        seconds++;
        if (seconds == 60) {
            minutes++;
            seconds = 0;
        }
    }
}
