package net.neferett.linaris.pvpswap.event.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import net.neferett.linaris.pvpswap.PvPSwapItems;
import net.neferett.linaris.pvpswap.PvPSwapListener;
import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Head;
import net.neferett.linaris.pvpswap.handler.Item;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.scheduler.GameRunnable;
import net.neferett.linaris.pvpswap.util.BungeeCordUtils;
import net.neferett.linaris.pvpswap.util.MathUtils;
import net.neferett.linaris.pvpswap.util.SpectatorUtils;

public class PlayerInteract extends PvPSwapListener {
    private List<Location> chestsOpened;

    public PlayerInteract(PvPSwapPlugin plugin) {
        super(plugin);
        chestsOpened = new ArrayList<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem() && event.getItem().isSimilar(PvPSwapItems.HUB_ITEM)) {
            event.setCancelled(true);
            BungeeCordUtils.teleportToLobby(plugin, event.getPlayer());
        } else if (!Step.isStep(Step.LOBBY) && SpectatorUtils.isSpectator(event.getPlayer())) {
            event.setCancelled(true);
            if (event.hasItem() && event.getItem().isSimilar(PvPSwapItems.COMPASS_ITEM)) {
                Inventory inv = Bukkit.createInventory(event.getPlayer(), 27, "Menu Spectateur");
                for (Player alive : plugin.getAlivePlayers()) {
                    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    meta.setOwner(alive.getName());
                    meta.setDisplayName(ChatColor.WHITE + alive.getName());
                    skull.setItemMeta(meta);
                    inv.addItem(skull);
                }
                event.getPlayer().openInventory(inv);
            }
        } else if (event.getAction().name().contains("RIGHT") && Step.isStep(Step.IN_GAME)) {
            if (event.hasItem() && event.getMaterial() == Material.SKULL_ITEM) {
                for (Head head : Head.values()) {
                    if (head.getItem().isSimilar(event.getItem())) {
                        event.setCancelled(true);
                        this.removeItemInHand(event);
                        head.giveHeadEffect(event.getPlayer());
                        event.getPlayer().sendMessage("Tête activée");
                        break;
                    }
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                if (block.getState() instanceof Chest) {
                    if (!chestsOpened.contains(block.getLocation())) {
                        Chest chest = (Chest) block.getState();
                        boolean firstDuelSwap = PvPSwapPlugin.duelMode && GameRunnable.swapCount == 1;
                        if (firstDuelSwap) {
                            if (event.getPlayer().hasMetadata("CHEST_OPEN")) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(PvPSwapPlugin.prefix + ChatColor.RED + "Attendez le prochain swap pour ouvrir ce coffre.");
                                return;
                            }
                        }
                        List<Item> items = new ArrayList<>(plugin.getItems());
                        int random = 0;
                        if (firstDuelSwap) {
                            random = items.size();
                            event.getPlayer().setMetadata("CHEST_OPEN", new FixedMetadataValue(plugin, 1));
                        } else {
                            random = PvPSwapPlugin.duelMode ? 12 : MathUtils.random(3, 6);
                            if (random > items.size()) {
                                random = items.size();
                            }
                        }
                        for (int i = 0; i < random; i++) {
                            Item randomItem = null;
                            while (randomItem == null && !items.isEmpty()) {
                                randomItem = items.remove(MathUtils.random(items.size() - 1));
                                if (!MathUtils.randomBoolean(randomItem.getRarity())) {
                                    randomItem = null;
                                }
                            }
                            if (items.isEmpty()) {
                                break;
                            }
                            int slot = -1;
                            while (slot == -1) {
                                slot = MathUtils.random(chest.getInventory().getSize() - 1);
                                ItemStack at = chest.getInventory().getItem(slot);
                                if (at != null && at.getType() != Material.AIR) {
                                    slot = -1;
                                }
                            }
                            ItemStack itemStack = randomItem.getItemStack().clone();
                            itemStack.setAmount(MathUtils.random(randomItem.getMinimum(), randomItem.getMaximum()));
                            chest.getInventory().setItem(slot, itemStack);
                        }
                        chest.update();
                        chestsOpened.add(block.getLocation());
                    }
                } else if (event.hasItem() && event.getMaterial().isBlock()) {
                    for (Entity entity : event.getPlayer().getNearbyEntities(1, 1, 1)) {
                        if (entity instanceof Player && SpectatorUtils.isSpectator((Player) entity)) {
                            Block newBlock = block.getRelative(event.getBlockFace());
                            newBlock.setType(event.getItem().getType());
                            newBlock.setData(event.getItem().getData().getData());
                            this.removeItemInHand(event);
                            event.getPlayer().updateInventory();
                        }
                    }
                }
            }
        }
    }

    private void removeItemInHand(PlayerInteractEvent event) {
        if (event.getItem().getAmount() > 1) {
            event.getItem().setAmount(event.getItem().getAmount() - 1);
        } else {
            event.getPlayer().setItemInHand(null);
        }
    }
}
