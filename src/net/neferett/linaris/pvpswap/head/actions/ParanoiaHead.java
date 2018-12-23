package net.neferett.linaris.pvpswap.head.actions;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.neferett.linaris.pvpswap.PvPSwapPlugin;
import net.neferett.linaris.pvpswap.handler.Head.HeadAction;
import net.neferett.linaris.pvpswap.util.MathUtils;

public class ParanoiaHead extends HeadAction {

    @Override
    public void onDamage(Player player, Player damager) {}

    @Override
    public void onRun(Player player) {
        player.sendMessage(PvPSwapPlugin.prefix + "Vos adversaires ne savent plus où donner de la tête...");
        player.sendMessage(ChatColor.GOLD + "Les autres joueurs deviennent paranos..");
        for (final Player alive : PvPSwapPlugin.getInstance().getAlivePlayers()) {
            if (alive != player) {
                Sound[] sounds = new Sound[] { Sound.CHEST_OPEN, Sound.DRINK, Sound.STEP_LADDER, Sound.BAT_IDLE, Sound.SHOOT_ARROW };
                final Sound sound = sounds[MathUtils.random(sounds.length - 1)];
                alive.playSound(alive.getLocation(), sound, 1, 1);
                if (sound == Sound.CHEST_OPEN) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            alive.playSound(alive.getLocation(), Sound.CHEST_CLOSE, 1, 1);
                        }
                    }.runTaskLater(PvPSwapPlugin.getInstance(), MathUtils.random(20, 40));
                } else if (sound == Sound.STEP_LADDER) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            alive.playSound(alive.getLocation(), sound, 1, 1);
                            if (MathUtils.randomBoolean()) {
                                this.cancel();
                                return;
                            }
                        }
                    }.runTaskLater(PvPSwapPlugin.getInstance(), 2);
                } else if (sound == Sound.DRINK) {
                    new BukkitRunnable() {
                        int count = 0;

                        @Override
                        public void run() {
                            if (count == 2) {
                                this.cancel();
                                return;
                            }
                            alive.playSound(alive.getLocation(), sound, 1, 1);
                            count++;
                        }
                    }.runTaskTimer(PvPSwapPlugin.getInstance(), 2, 2);
                } else if (sound == Sound.BAT_IDLE) {
                    new BukkitRunnable() {
                        boolean death = false;

                        @Override
                        public void run() {
                            if (death) {
                                alive.playSound(alive.getLocation(), Sound.BAT_DEATH, 1, 1);
                                this.cancel();
                                return;
                            }
                            alive.playSound(alive.getLocation(), Sound.BAT_HURT, 1, 1);
                            death = MathUtils.randomBoolean();
                        }
                    }.runTaskTimer(PvPSwapPlugin.getInstance(), 0, 10);
                }
            }
        }
    }
}
