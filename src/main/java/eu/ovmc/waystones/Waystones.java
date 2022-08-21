package eu.ovmc.waystones;

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
import java.util.List;

public final class Waystones extends JavaPlugin implements Listener {


    @Override
    public void onEnable() {
        //Configuration File
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //SQLiteJDBC - Connect and create the tables.
        SQLiteJDBC jdbc = new SQLiteJDBC();
        jdbc.createTables();

        //Events
        getServer().getPluginManager().registerEvents(new MenuHandler(), this);
        getServer().getPluginManager().registerEvents(new WaystonePlace(), this);
        getServer().getPluginManager().registerEvents(new WaystoneInteract(this), this);
        getServer().getPluginManager().registerEvents(new WaystoneBreak(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void openMainMenu(Player player){
        Inventory gui = Bukkit.createInventory(player, 54, ChatColor.AQUA + "Main GUI");

        ItemStack privateWs = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta ptivateWsMeta = privateWs.getItemMeta();
        ptivateWsMeta.displayName(Component.text("Private Waystone").decoration(TextDecoration.ITALIC, false));
        privateWs.setItemMeta(ptivateWsMeta);


        ItemStack publicWs = new ItemStack(Material.NETHERITE_BLOCK);
        ItemMeta pbUsMeta = publicWs.getItemMeta();
        //Item Name
        TextComponent itemName = Component.text("Public Waystone")
                .color(TextColor.fromCSSHexString("#473736"))
                .decoration(TextDecoration.ITALIC, false);
        pbUsMeta.displayName(itemName);
        //lore
        List<Component> loreArray = new ArrayList<>();
        loreArray.add(Component.text("Category: "));
        loreArray.add(Component.text("Rating: "));
        loreArray.add(Component.text("Cost: "));
        pbUsMeta.lore(loreArray);
        publicWs.setItemMeta(pbUsMeta);



        ItemStack blackPanel = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);


        ItemStack[] menuItems = {privateWs, publicWs, blackPanel};
        gui.setContents(menuItems);

        //Send the gui to the event handler
        System.out.println("set gui");

        player.openInventory(gui);
    }

    @Override
    public void onDisable() {}
}
