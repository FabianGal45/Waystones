package eu.ovmc.waystones;

import eu.ovmc.waystones.commands.Ws;
import eu.ovmc.waystones.commands.WsTabCompletion;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.events.*;
import eu.ovmc.waystones.menusystem.ChatInputHandler;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

public final class WaystonesPlugin extends JavaPlugin implements Listener {
    private static WaystonesPlugin plugin;
    private SQLiteJDBC jdbc;
    private ChatInputHandler chatInputHandler;
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static boolean votingPluginInstalled = false;



    @Override
    public void onEnable() {
        //Configuration File
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        plugin = this;

        if(Bukkit.getServer().getPluginManager().getPlugin("VotingPlugin")!=null){
            votingPluginInstalled = true;
        }

        //SQLiteJDBC - Connect and create the tables.
        jdbc = new SQLiteJDBC();
        jdbc.createTables();

        //Create the chat input handler here so that it doesn't get created multiple times in other classes and make the use of static hashmap redundant / Removes the need of creating multiple slots in memory for the same class.
        chatInputHandler = new ChatInputHandler();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Commands
        getCommand("ws").setExecutor(new Ws());
        getCommand("ws").setTabCompleter(new WsTabCompletion());

//        System.out.println("EditmenuUtility - Thread: "+ Thread.currentThread().getName()+"; "+Thread.currentThread().getName());

        //Events
        getServer().getPluginManager().registerEvents(new CloseInventory(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new MenuHandler(), this);
        getServer().getPluginManager().registerEvents(new WaystonePlace(), this);
        getServer().getPluginManager().registerEvents(new WaystoneInteract(), this);
        getServer().getPluginManager().registerEvents(new WaystoneBreak(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }


    public static PlayerMenuUtility getPlayerMenuUtility(Player player){
        PlayerMenuUtility playerMenuUtility;

        //if player is already registered in the map then return it's playerMenuUtility var.
        if(playerMenuUtilityMap.containsKey(player)){
            return playerMenuUtilityMap.get(player);
        }
        else {//Register the player and return it's newly generated playerMenuUtility var.
            playerMenuUtility = new PlayerMenuUtility(Bukkit.getOfflinePlayer(player.getUniqueId()));
            playerMenuUtilityMap.put(player,playerMenuUtility);

            return playerMenuUtility;
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Location getSafeLocation(Location location){
        //Teleports player above the selected waystone
        Location loc = null;

        for(int i = 0; i<5; i++){
            //Centers the player on the block
            loc = location.set(centerCoordinate(location.getX()), location.getY(), centerCoordinate(location.getZ()));
            loc.setYaw(location.getYaw());
            loc.setPitch(location.getPitch());

            Block landingBlock = loc.getBlock().getLocation().subtract(0.0, (double) i, 0.0).getBlock();

            //if there is a block to land on
            if(!landingBlock.getType().equals(Material.AIR)){
                //position the player once a landing block has been found
                loc.set(landingBlock.getX()+0.5, landingBlock.getY()+1, landingBlock.getZ()+0.5);

                //if the block is dangerous and there isn't enough space above for the player then it's unsafe
                if((landingBlock.getType().equals(Material.LAVA)
                        || landingBlock.getType().equals(Material.FIRE)
                        || landingBlock.getType().equals(Material.NETHER_PORTAL)
                        || landingBlock.getType().equals(Material.STONECUTTER)) || !isSpaceAbove(landingBlock.getLocation())){
                    //UNSAFE
                    loc = null;
                }
                break;
            }
        }
        return loc;
    }

    private static double centerCoordinate(double n){
        if(n < 0){
            n = (int)n - 0.5;
        }
        else{
            n = (int)n + 0.5;
        }
        return n;
    }

    private static boolean isSpaceAbove(Location loc){
        Material above1 = loc.add(0.0,1.0,0.0).getBlock().getType();
        Material above2 = loc.add(0.0,2.0,0.0).getBlock().getType();
        boolean spaceAbove = above1.isAir() && above2.isAir();

        return spaceAbove;
    }



    public static WaystonesPlugin getPlugin(){
        return plugin;
    }

    public static Economy getEcon() {
        return econ;
    }

    public SQLiteJDBC getJdbc() {
        return jdbc;
    }
    public ChatInputHandler getChatInputHandler() {
        return chatInputHandler;
    }

    public static boolean isVotingPluginInstalled() {
        return votingPluginInstalled;
    }

    @Override
    public void onDisable() {
        try {
            jdbc.getCon().close();
        } catch (SQLException e) {
            System.out.println("Unable to close connection!");
        }

    }
}
