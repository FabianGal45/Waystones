package eu.ovmc.waystones.menusystem.items;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
    private ItemStack displayItem;
    private ItemMeta displayItemMeta;
    private ItemType menuItemType;
    private String name;
    private int index;
    private List<Component> loreDescription = new ArrayList<>();
    private PrivateWaystone referencingWaystone;
    private int priority;

    //For easy items without description only a name such as back button.
    public MenuItem(Material displayItem, ItemType menuItemType, String name) {
        this.displayItem = new ItemStack(displayItem);
        this.displayItemMeta = this.displayItem.getItemMeta();
        this.menuItemType = menuItemType;
        setItemName(name);
    }

    //For more complex items from the paginated menu such as Waystones
    public MenuItem(Material displayItem, ItemType menuItemType, int index) {
        this.displayItem = new ItemStack(displayItem);
        this.displayItemMeta = this.displayItem.getItemMeta();
        this.menuItemType = menuItemType;
        this.index = index;
        setIndexMeta();
    }

    private void setIndexMeta(){
        //Stores the index of the waystone from the waystones list into the NBT meta of that file so that it can be identified when clicked.
        displayItemMeta.getPersistentDataContainer().set(new NamespacedKey(WaystonesPlugin.getPlugin(), "index"), PersistentDataType.INTEGER, index);
    }

    public void setItemName(String name){
        this.name = name;
        //Sets the name
        if(name == null){
            displayItemMeta.displayName(Component.text("Null")
                    .decoration(TextDecoration.ITALIC, false));
        }
        else{
            displayItemMeta.displayName(Component.text(name)
                    .decoration(TextDecoration.ITALIC, false));
        }
        displayItem.setItemMeta(displayItemMeta);
    }

    public void composePrivateWaystoneDescription(Location location){
        //Creates the lore of the item
        String worldName = getWorldName(location);

        Component locText = Component.text(worldName +": ", NamedTextColor.DARK_PURPLE)
                .append(Component.text(location.getBlockX()+", "+ location.getBlockY()+", "+location.getBlockZ(), NamedTextColor.LIGHT_PURPLE));

        loreDescription.add(locText);
        displayItemMeta.lore(loreDescription);
        displayItem.setItemMeta(displayItemMeta);
    }

    public void composePublicWaystoneDescription(Location location, String userName, double rating, double cost){
        String worldName = getWorldName(location);
        Economy econ = WaystonesPlugin.getEcon();
        String formattedCost = econ.format(cost);

        Component locText = Component.text(worldName +": ",NamedTextColor.DARK_PURPLE)
                .append(Component.text(location.getBlockX()+", "+ location.getBlockY()+", "+location.getBlockZ(), NamedTextColor.LIGHT_PURPLE));
        Component ownerText = Component.text("Owner: ", NamedTextColor.DARK_PURPLE)
                .append(Component.text(userName, NamedTextColor.LIGHT_PURPLE));
        Component ratingText = Component.text("Rating: ",NamedTextColor.DARK_PURPLE)
                .append(Component.text(rating, NamedTextColor.LIGHT_PURPLE)
                        .append(Component.text("/",NamedTextColor.DARK_PURPLE))
                        .append(Component.text("5",NamedTextColor.LIGHT_PURPLE)));
        Component costText = Component.text("Cost: ",NamedTextColor.DARK_PURPLE)
                .append(Component.text(formattedCost, NamedTextColor.AQUA));

        loreDescription.add(locText);
        loreDescription.add(ownerText);
        loreDescription.add(ratingText);
        if(cost > 0) {
            loreDescription.add(costText);
        }
        displayItemMeta.lore(loreDescription);
        displayItem.setItemMeta(displayItemMeta);
    }

    public void addGlint(){
        displayItemMeta.addEnchant(Enchantment.DAMAGE_ALL,0, true);
        displayItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        displayItem.setItemMeta(displayItemMeta);
    }

    private String getWorldName(Location location){
        String shortWorldName;
        if(location.getWorld().getName().equals("world")){
            shortWorldName = "World";
        } else if(location.getWorld().getName().equals("world_nether")) {
            shortWorldName = "Nether";
        }
        else if(location.getWorld().getName().equals("world_the_end")){
            shortWorldName = "End";
        }
        else{
            shortWorldName = "Unknown";
        }
        return shortWorldName;
    }


    public void setActionInfo(String leftClickActionInfo, String rightClickActionInfo){
        //If there is action info then add a blank space and the info that has been provided.
        if(leftClickActionInfo != null || rightClickActionInfo != null){
            Component blank = Component.text("");
            loreDescription.add(blank);

            if(leftClickActionInfo!=null){
                Component lClick = Component.text("L-Click: ", NamedTextColor.DARK_GRAY)
                        .append(Component.text(leftClickActionInfo, NamedTextColor.GRAY));
                loreDescription.add(lClick);
            }

            if(rightClickActionInfo!=null){
                Component rClick = Component.text("R-Click: ", NamedTextColor.DARK_GRAY)
                        .append(Component.text("Edit", NamedTextColor.GRAY));
                loreDescription.add(rClick);
            }

            displayItemMeta.lore(loreDescription);
            displayItem.setItemMeta(displayItemMeta);
        }

    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }
}
