package eu.ovmc.waystones;

import eu.ovmc.waystones.commands.Ws;
import eu.ovmc.waystones.commands.WsTabCompletion;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.events.*;
import eu.ovmc.waystones.handlers.ChatInputHandler;
import eu.ovmc.waystones.handlers.MenuHandler;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
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
    private static boolean isVotingPluginInstalled = false;
    private static boolean isCmiInstalled = false;

    @Override
    public void onEnable() {
        //Configuration File
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        plugin = this;

        //check to see if dependency plugins are installed
        if(Bukkit.getServer().getPluginManager().getPlugin("VotingPlugin")!=null){
            isVotingPluginInstalled = true;
        }

        if(Bukkit.getServer().getPluginManager().getPlugin("CMI")!=null){
            isCmiInstalled = true;
        }

        //SQLiteJDBC - Connect and create the tables.
        jdbc = new SQLiteJDBC();
        jdbc.createTables();
        jdbc.checkForDBupdate();

        //Create the chat input handler here so that it doesn't get created multiple times in other classes and make the use of static hashmap redundant / Removes the need of creating multiple slots in memory for the same class.
        chatInputHandler = new ChatInputHandler();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no economy plugin being found!", getDescription().getName()));
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

    public static boolean isIsVotingPluginInstalled() {
        return isVotingPluginInstalled;
    }
    public static boolean isIsCmiInstalled(){
        return isCmiInstalled;
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
