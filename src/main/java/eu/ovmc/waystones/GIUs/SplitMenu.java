package eu.ovmc.waystones.GIUs;

import eu.ovmc.waystones.PrivateWaystone;
import eu.ovmc.waystones.SQLiteJDBC;
import eu.ovmc.waystones.events.MenuHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplitMenu {

    ArrayList<Integer> privWsSlots;
    ArrayList<Integer> pubWsSlots;

    ArrayList<PrivateWaystone> privateWaystones;

    public SplitMenu() {
        //Slots that are available for Private Waystones
        privWsSlots = new ArrayList<>();
        Collections.addAll(privWsSlots, 10, 11, 12, 13, 14, 15, 16);
        pubWsSlots = new ArrayList<>();
        Collections.addAll(pubWsSlots, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
        privateWaystones = new ArrayList<>();
    }

    public void openMainMenu(Player player){

        //Creating the GUI
        Inventory gui = Bukkit.createInventory(player, 54, Component.text("Main GUI"));

        //Get the array with All private waystones
        SQLiteJDBC jdbc = new SQLiteJDBC();
        privateWaystones = jdbc.getAllPrivateWaystones(player.getUniqueId().toString());


        //Todo: continue with the getall public wasytones
        //Creates an item for the first 7 waystones in the array.
        for(int i = 0; i<privWsSlots.size(); i++){
            PrivateWaystone ws = privateWaystones.get(i);

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

            //Places the block in the GUI
            gui.setItem(privWsSlots.get(i), privateWs);
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

        player.openInventory(gui);
    }




    public ArrayList<Integer> getPrivWsSlots() {
        return privWsSlots;
    }

    public void setPrivWsSlots(ArrayList<Integer> privWsSlots) {
        this.privWsSlots = privWsSlots;
    }

    public ArrayList<Integer> getPubWsSlots() {
        return pubWsSlots;
    }

    public void setPubWsSlots(ArrayList<Integer> pubWsSlots) {
        this.pubWsSlots = pubWsSlots;
    }

    public ArrayList<PrivateWaystone> getPrivateWaystones() {
        return privateWaystones;
    }

    public void setPrivateWaystones(ArrayList<PrivateWaystone> privateWaystones) {
        this.privateWaystones = privateWaystones;
    }
}
