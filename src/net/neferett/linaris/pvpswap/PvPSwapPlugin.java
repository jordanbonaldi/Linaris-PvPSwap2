package net.neferett.linaris.pvpswap;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.SneakyThrows;
import net.neferett.linaris.pvpswap.event.block.BlockBreak;
import net.neferett.linaris.pvpswap.event.block.BlockPlace;
import net.neferett.linaris.pvpswap.event.entity.CreatureSpawn;
import net.neferett.linaris.pvpswap.event.entity.EntityDamage;
import net.neferett.linaris.pvpswap.event.entity.EntityDamageByPlayer;
import net.neferett.linaris.pvpswap.event.entity.EntityExplode;
import net.neferett.linaris.pvpswap.event.entity.FoodLevelChange;
import net.neferett.linaris.pvpswap.event.entity.PotionSplash;
import net.neferett.linaris.pvpswap.event.inventory.InventoryClick;
import net.neferett.linaris.pvpswap.event.player.AsyncPlayerChat;
import net.neferett.linaris.pvpswap.event.player.PlayerCommandPreprocess;
import net.neferett.linaris.pvpswap.event.player.PlayerDamage;
import net.neferett.linaris.pvpswap.event.player.PlayerDamageByPlayer;
import net.neferett.linaris.pvpswap.event.player.PlayerDeath;
import net.neferett.linaris.pvpswap.event.player.PlayerDropItem;
import net.neferett.linaris.pvpswap.event.player.PlayerInteract;
import net.neferett.linaris.pvpswap.event.player.PlayerItemConsume;
import net.neferett.linaris.pvpswap.event.player.PlayerJoin;
import net.neferett.linaris.pvpswap.event.player.PlayerKick;
import net.neferett.linaris.pvpswap.event.player.PlayerLogin;
import net.neferett.linaris.pvpswap.event.player.PlayerMove;
import net.neferett.linaris.pvpswap.event.player.PlayerPickupItem;
import net.neferett.linaris.pvpswap.event.player.PlayerQuit;
import net.neferett.linaris.pvpswap.event.player.PlayerRespawn;
import net.neferett.linaris.pvpswap.event.server.ServerListPing;
import net.neferett.linaris.pvpswap.event.weather.ThunderChange;
import net.neferett.linaris.pvpswap.event.weather.WeatherChange;
import net.neferett.linaris.pvpswap.event.world.ChunkUnload;
import net.neferett.linaris.pvpswap.handler.Head;
import net.neferett.linaris.pvpswap.handler.Item;
import net.neferett.linaris.pvpswap.handler.MapLocation;
import net.neferett.linaris.pvpswap.handler.MySQL;
import net.neferett.linaris.pvpswap.handler.PlayerData;
import net.neferett.linaris.pvpswap.handler.Step;
import net.neferett.linaris.pvpswap.scheduler.GameRunnable;
import net.neferett.linaris.pvpswap.util.BungeeCordUtils;
import net.neferett.linaris.pvpswap.util.FileUtils;
import net.neferett.linaris.pvpswap.util.LocationUtils;
import net.neferett.linaris.pvpswap.util.MathUtils;
import net.neferett.linaris.pvpswap.util.ReflectionHandler;
import net.neferett.linaris.pvpswap.util.StatsUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PvPSwapPlugin extends JavaPlugin {
    @Getter
    private static PvPSwapPlugin instance;
    public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "PVPSwap" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + " ";
    public static boolean godMode = true;
    public static boolean duelMode = true;

    private MySQL database;
    @Getter
    private Location lobbyLocation;
    @Getter
    private Set<Player> alivePlayers = new HashSet<>();

    private List<Item> firstItems;
    private Map<String, List<Location>> spawns;
    private Map<String, List<Location>> unusedSpawns;
    private Map<String, Integer> onMaps = new HashMap<>();
    private List<String> maps;

    private List<Item> items;
    private List<Item> finalItems;
    private String lastMap;

    @Getter
    private List<Location> finalSpawns;
    private List<Location> unusedFinalSpawns;

    @SneakyThrows
    @Override
    public void onLoad() {
        Bukkit.unloadWorld("world", false);
        final File worldContainer = this.getServer().getWorldContainer();
        final File worldFolder = new File(worldContainer, "world");
        final File copyFolder = new File(worldContainer, "pvpswap");
        if (copyFolder.exists()) {
            FileUtils.delete(worldFolder);
            FileUtils.copyFolder(copyFolder, worldFolder);
        }
    }

    @Override
    public void onEnable() {
        PvPSwapPlugin.instance = this;
        this.loadConfiguration(Bukkit.getWorlds().get(0));
        for (Entry<String, List<Location>> entry : spawns.entrySet()) {
            this.loadChunks(entry.getValue());
        }
        this.loadChunks(finalSpawns);
        database = new MySQL(this, this.getConfig().getString("mysql.host"), this.getConfig().getString("mysql.port"), this.getConfig().getString("mysql.database"), this.getConfig().getString("mysql.user"), this.getConfig().getString("mysql.pass"));
        try {
            database.openConnection();
            database.updateSQL("CREATE TABLE IF NOT EXISTS `players` ( `id` int(11) NOT NULL AUTO_INCREMENT, `name` varchar(30) NOT NULL, `uuid` varbinary(16) NOT NULL, `coins` double NOT NULL, `fk_miner` int(11) DEFAULT '0' NOT NULL, `fk_better_bow` int(11) DEFAULT '0' NOT NULL, `fk_better_sword` int(11) DEFAULT '0' NOT NULL, `fk_better_armor` int(11) DEFAULT '0' NOT NULL, `fk_merlin` int(11) DEFAULT '0' NOT NULL, `created_at` datetime NOT NULL, `updated_at` datetime NOT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
        } catch (ClassNotFoundException | SQLException e) {
            this.getLogger().severe("Impossible de se connecter à la base de données :");
            e.printStackTrace();
            this.getLogger().severe("Arrêt du serveur...");
            Bukkit.shutdown();
            return;
        }
        Step.setCurrentStep(Step.LOBBY);
        World world = Bukkit.getWorlds().get(0);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(6000);
        this.register(BlockBreak.class, BlockPlace.class, CreatureSpawn.class, EntityDamage.class, EntityDamageByPlayer.class, EntityExplode.class, FoodLevelChange.class, PotionSplash.class, InventoryClick.class, AsyncPlayerChat.class, PlayerCommandPreprocess.class, PlayerDamage.class, PlayerDamageByPlayer.class, PlayerDeath.class, PlayerDropItem.class, PlayerInteract.class, PlayerItemConsume.class, PlayerJoin.class, PlayerKick.class, PlayerLogin.class, PlayerMove.class, PlayerPickupItem.class, PlayerQuit.class, PlayerRespawn.class, ServerListPing.class, ThunderChange.class, WeatherChange.class, ChunkUnload.class);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        this.saveConfiguration();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Vous devez être un joueur.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 0) {
            String sub = args[0];
            if (sub.equalsIgnoreCase("help")) {
                player.sendMessage(ChatColor.GOLD + "Aide du plugin PvPSwap :");
                player.sendMessage("/pvpswap setlobby" + ChatColor.YELLOW + " - définit le lobby du jeu");
                player.sendMessage("/pvpswap additem" + ChatColor.YELLOW + " - définit un item de swap");
                player.sendMessage("/pvpswap addfinalitem" + ChatColor.YELLOW + " - définit un item de swap final");
                player.sendMessage("/pvpswap addswap map" + ChatColor.YELLOW + " - définit un spawn de swap");
                player.sendMessage("/pvpswap addfinalswap" + ChatColor.YELLOW + " - définit un spawn de swap final");
            } else if (sub.equalsIgnoreCase("setlobby")) {
                lobbyLocation = player.getLocation();
                player.sendMessage(ChatColor.GREEN + "Vous avez défini le lobby avec succès.");
                this.saveConfiguration();
            } else if (sub.equalsIgnoreCase("additem")) {
                Item item = this.createItemWithHand(player, Arrays.copyOfRange(args, 1, args.length));
                if (item != null) {
                    items.add(item);
                    player.sendMessage(ChatColor.GREEN + "L'item a bien été ajouté !");
                }
            } else if (sub.equalsIgnoreCase("addfinalitem")) {
                Item finalItem = this.createItemWithHand(player, Arrays.copyOfRange(args, 1, args.length));
                if (finalItem != null) {
                    finalItems.add(finalItem);
                    player.sendMessage(ChatColor.GREEN + "L'item a bien été ajouté !");
                }
            } else if (sub.equalsIgnoreCase("addswap") && args.length == 2) {
                player.sendMessage(ChatColor.GREEN + "Vous avez défini un swap avec succès pour la map " + args[1] + ".");
                List<Location> locations = spawns.containsKey(args[1]) ? spawns.get(args[1]) : new ArrayList<Location>();
                locations.add(player.getLocation());
                spawns.put(args[1], locations);
                this.saveConfiguration();
            } else if (sub.equalsIgnoreCase("addfinalswap")) {
                Location location = player.getLocation();
                player.sendMessage(ChatColor.GREEN + "Vous avez défini un spawn de swap final avec succès.");
                finalSpawns.add(location);
                this.saveConfiguration();
            } else if (sub.equalsIgnoreCase("head") && args.length == 2) {
                Head head = Head.valueOf(args[1]);
                player.getInventory().addItem(head.getItem());
            } else {
                sender.sendMessage(ChatColor.RED + "Mauvais arguments ou commande inexistante. Tapez " + ChatColor.DARK_RED + "/pvpswap help" + ChatColor.RED + " pour de l'aide.");
            }
            return true;
        }
        return false;
    }

    private void loadChunks(List<Location> spawns) {
        for (Location location : spawns) {
            location.getChunk().load();
            for (int x = -5; x < 5; x++) {
                for (int z = -5; z < 5; z++) {
                    location.clone().add(x * 16, 0, z * 16).getChunk().load();
                }
            }
        }
    }

    private Item createItemWithHand(Player player, String[] args) {
        ItemStack inHand = player.getItemInHand();
        if (inHand == null || inHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Vous devez avoir un item dans la main.");
            return null;
        }
        Item item = new Item(1, inHand.getAmount(), inHand.getAmount(), inHand);
        for (String arg : args) {
            if (arg.startsWith("rarity:")) {
                item.setRarity(Integer.parseInt(arg.replace("rarity:", "").replace("%", "")) / 100.0F);
            } else if (arg.startsWith("max:")) {
                item.setMaximum(Integer.parseInt(arg.replace("max:", "")));
            } else if (arg.startsWith("min:")) {
                item.setMinimum(Integer.parseInt(arg.replace("min:", "")));
            }
        }
        return item;
    }

    public boolean isReady() {
        return spawns.size() * 4 >= Bukkit.getMaxPlayers() && finalSpawns.size() >= 2;
    }

    private void loadConfiguration(World world) {
        this.saveDefaultConfig();
        String defaultLoc = LocationUtils.toString(world.getSpawnLocation());
        lobbyLocation = LocationUtils.toLocation(this.getConfig().getString("lobby", defaultLoc));
        spawns = new HashMap<>();
        unusedSpawns = new HashMap<>();
        if (this.getConfig().isConfigurationSection("default.spawns")) {
            ConfigurationSection mapSections = this.getConfig().getConfigurationSection("default.spawns");
            for (String map : mapSections.getKeys(false)) {
                List<Location> locations = new ArrayList<>();
                for (String stringLoc : mapSections.getStringList(map)) {
                    locations.add(LocationUtils.toLocation(stringLoc));
                }
            }
        }
        maps = new ArrayList<>(spawns.keySet());
        finalSpawns = new ArrayList<>();
        unusedFinalSpawns = new ArrayList<>();
        if (this.getConfig().isConfigurationSection("final.spawns")) {
            ConfigurationSection locationsSection = this.getConfig().getConfigurationSection("final.spawns");
            for (String stringKey : locationsSection.getKeys(false)) {
                Location location = LocationUtils.toLocation(locationsSection.getString(stringKey));
                finalSpawns.add(location);
                unusedFinalSpawns.add(location);
            }
        }
        items = this.loadItems("default.items");
        finalItems = this.loadItems("final.items");
    }

    private List<Item> loadItems(String key) {
        List<Item> items = new ArrayList<>();
        if (this.getConfig().isConfigurationSection(key)) {
            ConfigurationSection itemsSection = this.getConfig().getConfigurationSection(key);
            for (String itemKey : itemsSection.getKeys(false)) {
                ConfigurationSection item = itemsSection.getConfigurationSection(itemKey);
                items.add(new Item((float) item.getDouble("rarity"), item.getInt("minimum"), item.getInt("maximum"), item.getItemStack("itemstack")));
            }
        }
        return items;
    }

    private void saveConfiguration() {
        this.getConfig().set("lobby", LocationUtils.toString(lobbyLocation));
        this.getConfig().set("default", null);
        this.getConfig().set("final", null);
        for (Entry<String, List<Location>> entry : spawns.entrySet()) {
            List<String> locations = new ArrayList<>();
            for (Location location : entry.getValue()) {
                locations.add(LocationUtils.toString(location));
            }
            this.getConfig().set("default.spawns." + entry.getKey(), locations);
        }
        int locationId = 0;
        for (Location spawn : finalSpawns) {
            this.getConfig().set("final.spawns." + locationId, LocationUtils.toString(spawn));
            locationId++;
        }
        this.saveItems("default.items", items);
        this.saveItems("final.items", finalItems);
        this.saveConfig();
    }

    private void saveItems(String key, List<Item> items) {
        int itemId = 0;
        for (Item item : items) {
            ConfigurationSection section = this.getConfig().createSection(key + "." + itemId);
            section.set("rarity", item.getRarity());
            section.set("minimum", item.getMinimum());
            section.set("maximum", item.getMaximum());
            section.set("itemstack", item.getItemStack());
            itemId++;
        }
    }

    @SneakyThrows
    private void register(Class<? extends PvPSwapListener>... classes) {
        for (Class<? extends PvPSwapListener> clazz : classes) {
            Bukkit.getPluginManager().registerEvents((Listener) ReflectionHandler.getConstructor(clazz, PvPSwapPlugin.class).newInstance(this), this);
        }
    }

    public int getOnMap(String map) {
        if (!onMaps.containsKey(map)) { return 0; }
        return onMaps.get(map);
    }

    private void refillSpawns() {
        lastMap = null;
        unusedSpawns.clear();
        for (Entry<String, List<Location>> entry : spawns.entrySet()) {
            unusedSpawns.put(entry.getKey(), new ArrayList<Location>(entry.getValue()));
        }
        maps = new ArrayList<>(spawns.keySet());
    }

    public List<MapLocation> getUnusedSpawns() {
        List<MapLocation> locations = new ArrayList<>();
        int alivePlayers = this.getAlivePlayers().size();
        int counter = 0;
        while (locations.size() < alivePlayers) {
            if (counter >= maps.size()) {
                counter = 0;
            }
            lastMap = maps.get(counter);
            List<Location> mapLocations = unusedSpawns.get(lastMap);
            if (mapLocations == null || mapLocations.isEmpty()) {
                maps.remove(counter);
            } else {
                if (Math.floor(alivePlayers / spawns.size()) > 1) {
                    locations.add(new MapLocation(lastMap, mapLocations.remove(MathUtils.random(mapLocations.size() - 1))));
                    onMaps.put(lastMap, onMaps.containsKey(lastMap) ? onMaps.get(lastMap) + 1 : 0);
                } else {
                    for (Location mapLocation : mapLocations) {
                        locations.add(new MapLocation(lastMap, mapLocation));
                        onMaps.put(lastMap, onMaps.containsKey(lastMap) ? onMaps.get(lastMap) + 1 : 0);
                    }
                }
                counter++;
            }
        }
        this.refillSpawns();
        return locations;
    }

    public List<Item> getItems() {
        if (PvPSwapPlugin.duelMode && GameRunnable.swapCount == 1) {
            if (firstItems == null) {
                firstItems = new ArrayList<>();
                firstItems.add(new Item(1, 1, 1, new ItemStack(Material.IRON_HELMET)));
                firstItems.add(new Item(1, 1, 1, new ItemStack(Material.IRON_CHESTPLATE)));
                firstItems.add(new Item(1, 1, 1, new ItemStack(Material.IRON_LEGGINGS)));
                firstItems.add(new Item(1, 1, 1, new ItemStack(Material.IRON_BOOTS)));
                firstItems.add(new Item(1, 1, 1, new ItemStack(Material.IRON_SWORD)));
                firstItems.add(new Item(1, 6, 15, new ItemStack(Material.COOKED_BEEF)));
                firstItems.add(new Item(0.1F, 4, 4, new ItemStack(Material.EXP_BOTTLE)));
            }
            return firstItems;
        }
        List<Item> items = GameRunnable.swapCount == -1 ? finalItems : this.items;
        if (GameRunnable.swapCount == -1) {
            items = new ArrayList<>(items);
            items.addAll(this.items);
        }
        return items;
    }

    public Location getFinalSpawn() {
        return unusedFinalSpawns.remove(MathUtils.random(unusedFinalSpawns.size() - 1));
    }

    @SneakyThrows
    private void clearCommands(final String... command) {
        final CommandMap commandMap = (CommandMap) ReflectionHandler.getField(Bukkit.getServer().getClass(), true, "commandMap").get(Bukkit.getServer());
        final Field knownCommands = ReflectionHandler.getField(commandMap.getClass(), true, "knownCommands");
        final Map<String, Command> commands = (Map<String, Command>) knownCommands.get(commandMap);
        new BukkitRunnable() {

            @Override
            @SneakyThrows
            public void run() {
                List<String> commandNames = Arrays.asList(command);
                for (Entry<String, Command> entry : new HashMap<>(commands).entrySet()) {
                    if (commandNames.contains(entry.getValue().getName())) {
                        commands.remove(entry.getKey());
                    }
                }
                knownCommands.set(commandMap, commands);
            }
        }.runTaskLater(this, 1);
    }

    public void onPlayerLoose(Player player) {
        if (Step.isStep(Step.LOBBY)) {
            StatsUtils.removeData(player);
        } else if (Step.isStep(Step.IN_GAME)) {
            alivePlayers.remove(player);
            Bukkit.getScoreboardManager().getMainScoreboard().getObjective("players").getScore("Joueurs").setScore(alivePlayers.size());
            if (alivePlayers.size() == 1) {
                final Player winner = alivePlayers.iterator().next();
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        Bukkit.broadcastMessage(PvPSwapPlugin.prefix + ChatColor.GOLD + ChatColor.BOLD + "Victoire de " + winner.getName() + " " + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.BOLD + " Félicitations " + ChatColor.YELLOW + ChatColor.MAGIC + " |" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|");
                    }
                }.runTaskLater(this, 1);
                StatsUtils.addCoins(winner, 15);
                this.stopGame();
            }
        }
    }

    public void stopGame() {
        Step.setCurrentStep(Step.POST_GAME);
        for (Entry<UUID, PlayerData> entry : StatsUtils.getData().entrySet()) {
            final String uuid = entry.getKey().toString().replaceAll("-", "");
            final PlayerData data = entry.getValue();
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        ResultSet res = database.querySQL("SELECT name FROM players WHERE uuid=UNHEX('" + uuid + "')");
                        if (res.first()) {
                            database.updateSQL("UPDATE players SET name='" + data.getName() + "', coins=coins+" + data.getCoins() + ", updated_at=NOW() WHERE uuid=UNHEX('" + uuid + "')");
                        } else {
                            database.updateSQL("INSERT INTO players(name, uuid, coins, created_at, updated_at) VALUES('" + data.getName() + "', UNHEX('" + uuid + "'), " + data.getCoins() + ", NOW(), NOW())");
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(this);
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    BungeeCordUtils.teleportToLobby(PvPSwapPlugin.this, online);
                }
            }
        }.runTaskLater(PvPSwapPlugin.this, 300);
        new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.shutdown();
            }
        }.runTaskLater(PvPSwapPlugin.this, 400);
    }
}
