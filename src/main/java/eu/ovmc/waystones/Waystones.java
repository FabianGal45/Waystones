package eu.ovmc.waystones;

import eu.ovmc.waystones.events.WaystonePlace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public final class Waystones extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        //Configuration File
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //SQLiteJDBC
        SQLiteJDBC jdbc = new SQLiteJDBC();
        Connection con = jdbc.getCon();
        jdbc.createTables(con);

        //Events
        getServer().getPluginManager().registerEvents(new WaystonePlace(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        System.out.println("A player has joined the server!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
