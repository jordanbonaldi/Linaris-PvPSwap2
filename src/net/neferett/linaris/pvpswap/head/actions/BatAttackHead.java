package net.neferett.linaris.pvpswap.head.actions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Head.HeadAction;
import net.neferett.linaris.pvpswap.util.MathUtils;

public class BatAttackHead extends HeadAction {

    @Override
    public void onDamage(Player player, Player damager) {}

    @Override
    public void onRun(Player player) {
        Bukkit.broadcastMessage(PvPSwapPlugin.prefix + player.getName() + " active <" + ChatColor.RED + "Chauvres-souris-ninjas !!" + ChatColor.WHITE + ">");
        final List<Entity> entities = new ArrayList<>();
        final List<Item> itemEntities = new ArrayList<>();
        for (Player alive : PvPSwapPlugin.getInstance().getAlivePlayers()) {
            if (alive != player) {
                List<ItemStack> items = new ArrayList<>();
                for (ItemStack item : alive.getInventory().getContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        items.add(item);
                    }
                }
                for (int i = 0; i < 6; i++) {
                    Bat bat = alive.getWorld().spawn(alive.getLocation().add(MathUtils.random(-2, 2), 1 + MathUtils.random(1.0F, 1.5F), MathUtils.random(-2, 2)), Bat.class);
                    if (!items.isEmpty()) {
                        ItemStack itemStack = items.remove(MathUtils.random(items.size() - 1));
                        alive.getInventory().remove(itemStack);
                        Item item = bat.getWorld().dropItem(bat.getLocation(), itemStack);
                        item.setPickupDelay(Integer.MAX_VALUE);
                        bat.setPassenger(item);
                        bat.setHealth(0.5D);
                        bat.setCustomName("*niark*");
                        bat.setCustomNameVisible(true);
                        itemEntities.add(item);
                    }
                    entities.add(bat);
                }
            }
        }
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count < 100) {
                    for (Entity entity : entities) {
                        entity.setVelocity(new Vector(0, 0, 0));
                    }
                } else {
                    for (Entity entity : entities) {
                        if (!entity.isDead()) {
                            entity.remove();
                        }
                    }
                    this.cancel();
                }
                for (Item item : itemEntities) {
                    if (item.getVehicle() == null || item.getVehicle().isDead()) {
                        item.setPickupDelay(0);
                    }
                }
                count += 5;
            }
        }.runTaskTimer(PvPSwapPlugin.getInstance(), 0, 5);
    }
}
