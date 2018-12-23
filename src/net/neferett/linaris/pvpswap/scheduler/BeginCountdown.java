package net.neferett.linaris.pvpswap.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Step;

public class BeginCountdown extends BukkitRunnable {
    public static boolean started = false;
    public static int timeUntilStart = 90;
    private PvPSwapPlugin plugin;

    public BeginCountdown(PvPSwapPlugin plugin) {
        this.plugin = plugin;
        BeginCountdown.started = true;
        this.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void run() {
        if (BeginCountdown.timeUntilStart <= 0) {
            this.cancel();
            if (Bukkit.getOnlinePlayers().length < 2) {
                Bukkit.broadcastMessage(PvPSwapPlugin.prefix + ChatColor.RED + "Il n'y a pas assez de joueurs !");
                BeginCountdown.timeUntilStart = 90;
                BeginCountdown.started = false;
            } else {
                Step.setCurrentStep(Step.IN_GAME);
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                this.createObjective(scoreboard, "health", "health", ChatColor.DARK_RED + "♥").setDisplaySlot(DisplaySlot.BELOW_NAME);
                this.createObjective(scoreboard, "kills", "playerKillCount", "kills").setDisplaySlot(DisplaySlot.PLAYER_LIST);
                Objective pvpSwap = this.createObjective(scoreboard, "players", "dummy", "PvPSwap " + ChatColor.RED + "00:00");
                pvpSwap.setDisplaySlot(DisplaySlot.SIDEBAR);
                pvpSwap.getScore(ChatColor.DARK_AQUA + "-------------").setScore(99);
                pvpSwap.getScore("Joueurs").setScore(Bukkit.getOnlinePlayers().length);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    plugin.getAlivePlayers().add(player);
                    BeginCountdown.resetPlayer(player);
                }
                new GameRunnable(plugin, pvpSwap).doSwap();
            }
            return;
        }
        int remainingMins = BeginCountdown.timeUntilStart / 60 % 60;
        int remainingSecs = BeginCountdown.timeUntilStart % 60;
        if (BeginCountdown.timeUntilStart % 30 == 0 || remainingMins == 0 && (remainingSecs % 10 == 0 || remainingSecs <= 5)) {
            String message = ChatColor.GOLD + "Début du jeu dans " + ChatColor.YELLOW + (remainingMins > 0 ? remainingMins + " minute" + (remainingMins > 1 ? "s" : "") : "") + (remainingSecs > 0 ? (remainingMins > 0 ? " " : "") + remainingSecs + " seconde" + (remainingSecs > 1 ? "s" : "") : "") + ".";
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(PvPSwapPlugin.prefix + message);
                if (remainingMins == 0 && remainingSecs <= 10) {
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                }
            }
        }
        BeginCountdown.timeUntilStart--;
    }

    private Objective createObjective(Scoreboard scoreboard, String name, String type, String displayName) {
        Objective objective = scoreboard.getObjective(name);
        if (objective != null) {
            objective.unregister();
        }
        objective = scoreboard.registerNewObjective(name, type);
        objective.setDisplayName(displayName);
        return objective;
    }

    public static void resetPlayer(Player player) {
        player.setFireTicks(0);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setExhaustion(5.0F);
        player.setFallDistance(0);
        player.setExp(0.0F);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.closeInventory();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
}
