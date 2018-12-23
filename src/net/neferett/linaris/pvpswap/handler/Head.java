package net.neferett.linaris.pvpswap.handler;

import lombok.AllArgsConstructor;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.head.actions.BatAttackHead;
import net.neferett.linaris.pvpswap.head.actions.BumpShieldHead;
import net.neferett.linaris.pvpswap.head.actions.CameleonHead;
import net.neferett.linaris.pvpswap.head.actions.ChickenAttackHead;
import net.neferett.linaris.pvpswap.head.actions.ExpelliarmusShieldHead;
import net.neferett.linaris.pvpswap.head.actions.ParanoiaHead;
import net.neferett.linaris.pvpswap.util.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public enum Head {
    BAT_ATTACK("Bat-Attack", "Des chauves souris\ns'emparent du stuff\nde vos adversaires", new BatAttackHead(), HeadType.ON_RUN),
    CAMELEON("Cameleon", "Le caméléon devient provisoirement\ninvisible en cas d'aggression", new CameleonHead(), HeadType.ON_DAMAGE),
    BUMP_SHIELD("BumpShield", "Votre aggresseur aura une petite surprise...", new BumpShieldHead(), HeadType.ON_DAMAGE),
    EXPELLIARMUS_SHIELD("ExpelliarmusShield", "Projete l'item en main\nde votre prochain aggresseur\ncomme dans Harry Potter !", new ExpelliarmusShieldHead(), HeadType.ON_DAMAGE),
    CHICKEN_ATTACK("Chicken", "Des poulets attaquent les autres joueurs", new ChickenAttackHead(), HeadType.ON_RUN),
    PARANOIA("Paranoïa", "Et tous vos adversaires\ndeviennent paranos...", new ParanoiaHead(), HeadType.ON_RUN);

    public static enum HeadType {
        ON_RUN, ON_DAMAGE;
    }

    @AllArgsConstructor
    public class HeadDamageListener implements Listener {
        private Player player;
        private Head head;

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerDamageByPlayer(EntityDamageByEntityEvent evt) {
            if (!evt.isCancelled() && evt.getEntity() instanceof Player && evt.getDamager() instanceof Player) {
                if (evt.getEntity() == player) {
                    evt.setCancelled(true);
                    head.action.onDamage(player, (Player) evt.getDamager());
                    EntityDamageEvent.getHandlerList().unregister(this);
                }
            }
        }
    }

    public static abstract class HeadAction {

        public abstract void onDamage(Player player, Player damager);

        public abstract void onRun(Player player);
    }

    private String name;
    private String description;
    private HeadAction action;
    private HeadType type;
    private ItemStack itemStack;

    private Head(String name, String description, HeadAction action, HeadType type) {
        this.name = name;
        this.description = description;
        this.action = action;
        this.type = type;
    }

    public void giveHeadEffect(Player player) {
        if (type == HeadType.ON_RUN) {
            action.onRun(player);
        } else if (type == HeadType.ON_DAMAGE) {
            Bukkit.getPluginManager().registerEvents(new HeadDamageListener(player, this), PvPSwapPlugin.getInstance());
        }
    }

    public ItemStack getItem() {
        if (itemStack == null) {
            ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM, (short) 3);
            builder.setTitle(ChatColor.WHITE + "" + ChatColor.ITALIC + name + ChatColor.GOLD + " (Clic Droit)");
            builder.addLores(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + name, "");
            for (String line : description.split("\n")) {
                builder.addLores(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + line);
            }
            itemStack = builder.build();
        }
        return itemStack;
    }
}
