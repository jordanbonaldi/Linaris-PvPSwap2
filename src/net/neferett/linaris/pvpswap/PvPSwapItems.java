package net.neferett.linaris.pvpswap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.pvpswap.util.ItemBuilder;

public interface PvPSwapItems {
    public static final ItemStack HUB_ITEM = new ItemBuilder(Material.BED).setTitle(ChatColor.GOLD + "Retourner au Hub").addLores(ChatColor.GRAY + "Ou faites " + ChatColor.YELLOW + "/hub").build();
    public static final ItemStack COMPASS_ITEM = new ItemBuilder(Material.COMPASS).setTitle(ChatColor.AQUA + "Menu Spectateur").build();

}
