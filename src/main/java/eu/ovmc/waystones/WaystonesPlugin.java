package eu.ovmc.waystones;

import eu.ovmc.waystones.commands.Ws;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.events.MenuHandler;
import eu.ovmc.waystones.events.WaystoneBreak;
import eu.ovmc.waystones.events.WaystoneInteract;
import eu.ovmc.waystones.events.WaystonePlace;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public final class WaystonesPlugin extends JavaPlugin implements Listener {
    private static WaystonesPlugin plugin;
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onEnable() {
        //Configuration File
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        plugin = this;

        //SQLiteJDBC - Connect and create the tables.
        SQLiteJDBC jdbc = new SQLiteJDBC();
        jdbc.createTables();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Commands
        getCommand("ws").setExecutor(new Ws());


        //Events
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
            playerMenuUtility = new PlayerMenuUtility(player);
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

    @Override
    public void onDisable() {}
}
