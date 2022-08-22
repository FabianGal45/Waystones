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
import java.util.Collections;
import java.util.List;

public final class Waystones extends JavaPlugin implements Listener {

    ArrayList<PrivateWaystone> privateWaystones;

    @Override
    public void onEnable() {
        //Configuration File
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //SQLiteJDBC - Connect and create the tables.
        SQLiteJDBC jdbc = new SQLiteJDBC();
        jdbc.createTables();

        //Events
        getServer().getPluginManager().registerEvents(new MenuHandler(this), this);
        getServer().getPluginManager().registerEvents(new WaystonePlace(), this);
        getServer().getPluginManager().registerEvents(new WaystoneInteract(this), this);
        getServer().getPluginManager().registerEvents(new WaystoneBreak(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void openMainMenu(Player player){

        //Slots that are available for Private Waystones
        ArrayList<Integer> privateSlots = new ArrayList<>();
        Collections.addAll(privateSlots, 10, 11, 12, 13, 14, 15, 16);

        //Creating the GUI
        Inventory gui = Bukkit.createInventory(player, 54, Component.text("Main GUI"));

        //Get the array with All private waystones
        SQLiteJDBC jdbc = new SQLiteJDBC();
        ArrayList<PrivateWaystone> arrAllPrivateWaystones;
        arrAllPrivateWaystones = jdbc.getAllPrivateWaystones(player.getUniqueId().toString());


        //Todo: continue with the getall public wasytones
        //Creates an item for the first 7 waystones in the array.
        for(int i = 0; i<privateSlots.size(); i++){
            PrivateWaystone ws = arrAllPrivateWaystones.get(i);

            //Creates the Emerald block item
            ItemStack privateWs = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta ptivateWsMeta = privateWs.getItemMeta();

            //Sets the name
            ptivateWsMeta.displayName(Component.text("Private Waystone").decoration(TextDecoration.ITALIC, false));

            //Creates the lore of the item
            List<Component> loreArray = new ArrayList<>();
            loreArray.add(Component.text("Location: "+ ws.getParsedLocation().getBlockX()+", "+ ws.getParsedLocation().getBlockY()+", "+ws.getParsedLocation().getBlockZ()));
            ptivateWsMeta.lore(loreArray);

            //Upates the meta with the provided one
            privateWs.setItemMeta(ptivateWsMeta);
            privateWaystones = arrAllPrivateWaystones;

            //Places the block in the GUI
            gui.setItem(privateSlots.get(i), privateWs);
        }

        //If the arrAllPrivateWaystones => 7 then display a duplicate of the main menu Otherwise display the large menu (split menu) (large menu)

        //If the arrAllPrivateWaystones or PublicWaystones is larger than the size of privateSlots or pubWaystoneSlots generate another gui and the next button




        //Netherite block | WAITING FEATURE
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


        //Black Panel | WAITING FEATURE
        ItemStack blackPanel = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

//        ItemStack[] menuItems = {privateWs, publicWs, blackPanel};

        gui.setItem(28, publicWs);
        gui.setItem(19, blackPanel);
//        gui.setContents(menuItems);

        //Send the gui to the event handler
        System.out.println("set gui");

        player.openInventory(gui);
    }


    public ArrayList<PrivateWaystone> getPrivWSs(){
        return this.privateWaystones;
    }

    @Override
    public void onDisable() {}
}
