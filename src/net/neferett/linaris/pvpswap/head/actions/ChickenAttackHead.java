package net.neferett.linaris.pvpswap.head.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Head.HeadAction;
import net.neferett.linaris.pvpswap.util.MathUtils;

public class ChickenAttackHead extends HeadAction {

    @Override
    public void onDamage(Player player, Player damager) {}

    @Override
    public void onRun(final Player player) {
        Bukkit.broadcastMessage(PvPSwapPlugin.prefix + player.getName() + " active <" + ChatColor.RED + "Pluie de poulets !!" + ChatColor.WHITE + ">");
        final Map<Player, List<Entity>> entities = new HashMap<>();
        for (final Player alive : PvPSwapPlugin.getInstance().getAlivePlayers()) {
            if (alive != player) {
                List<Entity> chickens = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    Chicken chicken = alive.getWorld().spawn(alive.getLocation().add(MathUtils.random(), 1 + MathUtils.random(), MathUtils.random()), Chicken.class);
                    chicken.setMaxHealth(2048.0D);
                    chicken.setHealth(2048.0D);
                    chicken.setVelocity(new Vector(0, 0, 0));
                    chickens.add(chicken);
                }
                entities.put(alive, chickens);
            }
        }
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                for (Entry<Player, List<Entity>> entry : entities.entrySet()) {
                    Player alive = entry.getKey();
                    for (Entity entity : entry.getValue()) {
                        if (!entity.isDead()) {
                            if (count >= 21) {
                                entity.remove();
                                continue;
                            }
                            entity.setVelocity(new Vector(0, 0, 0));
                            entity.teleport(alive.getLocation().add(MathUtils.random(), 1 + MathUtils.random(), MathUtils.random()));
                        }
                    }
                    if (count == 10 || count == 20) {
                        alive.damage(2.0D, entry.getValue().get(0));
                    } else if (count < 21 && count % 3 == 0) {
                        alive.playSound(alive.getLocation(), Sound.CHICKEN_HURT, 1, 1);
                    } else if (count >= 21) {
                        this.cancel();
                    }
                }
                count++;
            }
        }.runTaskTimer(PvPSwapPlugin.getInstance(), 0, 1);
    }
}
