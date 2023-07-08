package eu.ovmc.waystones.menusystem.items;

import eu.ovmc.waystones.WaystonesPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;

public class MIPublicWaystone extends MIPrivateWaystone{
    public MIPublicWaystone(Material displayItem, int index) {
        super(displayItem, index);
    }

    public MIPublicWaystone(Material displayItem, ItemType itemType, int index) {
        super(displayItem, itemType, index);
    }

    public void setLoreDescription(Location location, String userName, double rating, double cost){
        String worldName = getWorldName(location);
        Economy econ = WaystonesPlugin.getEcon();
        String formattedCost = econ.format(cost);

        Component locText = Component.text(worldName +": ", NamedTextColor.DARK_PURPLE)
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

}
