package net.neferett.linaris.pvpswap.head.actions;

import lombok.AllArgsConstructor;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Head.HeadAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CameleonHead extends HeadAction {

    @Override
    public void onDamage(final Player player, final Player damager) {
        damager.sendMessage(PvPSwapPlugin.prefix + "Et pouf ! Disparu !");
        Location playerLoc = player.getLocation();
        damager.playSound(playerLoc, Sound.FIREWORK_BLAST, 1, 0.8F);
        player.playSound(playerLoc, Sound.FIREWORK_BLAST, 1, 0.8F);
        player.sendMessage(PvPSwapPlugin.prefix + "Vos réfléxes de caméléon vous rendent temporairement invisible. Profitez en !");
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.hidePlayer(player);
            }
        }
        final CameleonListener listener = new CameleonListener(player, damager, false);
        Bukkit.getPluginManager().registerEvents(listener, PvPSwapPlugin.getInstance());
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!listener.damage) {
                    CameleonHead.this.resetCameleonEffect(listener, player, damager);
                }
            }
        }.runTaskLater(PvPSwapPlugin.getInstance(), 60);
    }

    private void resetCameleonEffect(CameleonListener listener, Player player, Player damager) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.showPlayer(player);
            }
        }
        player.sendMessage("*Pof*");
        player.sendMessage(ChatColor.RED + "Fin de l'invisibilité !!");
        damager.sendMessage("*Pof*");
        damager.sendMessage(ChatColor.RED + "Fin de l'invisibilité !!");
        HandlerList.unregisterAll(listener);
    }

    @AllArgsConstructor
    public class CameleonListener implements Listener {
        private Player player;
        private Player damager;
        private boolean damage;

        @EventHandler
        public void onPlayerDamageByPlayer(EntityDamageByEntityEvent evt) {
            if (evt.getEntity() instanceof Player && (evt.getDamager() == player || evt.getDamager() instanceof Projectile && ((Projectile) evt.getDamager()).getShooter() == player)) {
                CameleonHead.this.resetCameleonEffect(this, player, damager);
                damage = true;
            }
        }
    }

    @Override
    public void onRun(Player player) {}
}
