package eu.ovmc.waystones;

import eu.ovmc.waystones.GIUs.SplitMenu;
import eu.ovmc.waystones.events.MenuHandler;
import eu.ovmc.waystones.events.WaystoneBreak;
import eu.ovmc.waystones.events.WaystoneInteract;
import eu.ovmc.waystones.events.WaystonePlace;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Waystones extends JavaPlugin implements Listener {

    ArrayList<PrivateWaystone> privateWaystones;
    MenuHandler mh = new MenuHandler();
    @Override
    public void onEnable() {
        //Configuration File
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //SQLiteJDBC - Connect and create the tables.
        SQLiteJDBC jdbc = new SQLiteJDBC();
        jdbc.createTables();

        //Events
        getServer().getPluginManager().registerEvents(mh, this);
        getServer().getPluginManager().registerEvents(new WaystonePlace(), this);
        getServer().getPluginManager().registerEvents(new WaystoneInteract(this), this);
        getServer().getPluginManager().registerEvents(new WaystoneBreak(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    public MenuHandler getMh(){
        return this.mh;
    }

    @Override
    public void onDisable() {}
}
