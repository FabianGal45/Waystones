package eu.ovmc.waystones.menusystem.items;

import eu.ovmc.waystones.WaystonesPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class MIPrivateWaystone extends MenuItem {
    private final int INDEX;

    public MIPrivateWaystone(Material displayItem, int index) {
        super(displayItem);
        this.INDEX = index;
        menuItemType = ItemType.PRIVATE_WAYSTONE;
        saveIndexToNBT();
    }

    public MIPrivateWaystone(Material displayItem, ItemType itemType, int index) {
        super(displayItem, itemType);
        this.INDEX = index;
        saveIndexToNBT();
    }

    public void setLoreDescription(Location location){
        //Creates the lore of the item
        String worldName = getWorldName(location);

        Component locText = Component.text(worldName +": ", NamedTextColor.DARK_PURPLE)
                .append(Component.text(location.getBlockX()+", "+ location.getBlockY()+", "+location.getBlockZ(), NamedTextColor.LIGHT_PURPLE));

        loreDescription.add(locText);
        displayItemMeta.lore(loreDescription);
        displayItem.setItemMeta(displayItemMeta);
    }

    protected void saveIndexToNBT(){
        //Stores the index of the waystone from the waystones list into the NBT meta of that file so that it can be identified when clicked.
        displayItemMeta.getPersistentDataContainer().set(new NamespacedKey(WaystonesPlugin.getPlugin(), "index"), PersistentDataType.INTEGER, INDEX);
    }

}
