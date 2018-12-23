package net.neferett.linaris.pvpswap.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class Item {
    private float rarity;
    private int minimum;
    private int maximum;
    private ItemStack itemStack;
}
