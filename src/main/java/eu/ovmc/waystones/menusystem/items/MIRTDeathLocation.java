package eu.ovmc.waystones.menusystem.items;

import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MIRTDeathLocation extends MenuItem{

    public MIRTDeathLocation(Material displayItem, ItemType menuItemType, String name) {
        super(displayItem, menuItemType, name);
    }


    public void setLoreDescription(PlayerMenuUtility playerMenuUtility){
        Player player = playerMenuUtility.getPlayer();
        Location deathLocation = player.getLastDeathLocation();


        if(deathLocation != null){
            String worldName = getWorldName(deathLocation);

            loreDescription.add(Component.text(worldName+": ", NamedTextColor.DARK_PURPLE)
                    .append(Component.text(deathLocation.getBlockX()+", "+deathLocation.getBlockY()+", "+ deathLocation.getBlockZ(),NamedTextColor.LIGHT_PURPLE)));
            loreDescription.add(Component.text(""));
            loreDescription.add(Component.text("Click: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("TP to Death Location", NamedTextColor.GRAY)));
            loreDescription.add(Component.text("Cost: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("Echo Shard x1", NamedTextColor.GRAY)));
        }
        else{
            loreDescription.add(Component.text("You haven't died yet.", NamedTextColor.DARK_PURPLE));
            loreDescription.add(Component.text(""));
            loreDescription.add(Component.text("Click: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("TP to Death Location", NamedTextColor.GRAY)));
            loreDescription.add(Component.text("Cost: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("Echo Shard x1", NamedTextColor.GRAY)));
        }
        displayItemMeta.lore(loreDescription);
        displayItem.setItemMeta(displayItemMeta);
    }


}
