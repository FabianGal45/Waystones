package eu.ovmc.waystones.menusystem.items;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.handlers.TeleportHandler;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class MIPrivateWaystone extends MenuItem {

    protected PrivateWaystone ws;
    protected PlayerMenuUtility playerMenuUtility;

    public MIPrivateWaystone(Material displayItem, int index, PrivateWaystone ws, PlayerMenuUtility playerMenuUtility) {
        super(displayItem, ItemType.PRIVATE_WAYSTONE);
        this.ws = ws;
        this.playerMenuUtility = playerMenuUtility;
        setItemName(ws.getName());
        saveIndexToNBT(index);
        setLoreDescription();
        setActionInfo("Teleport", "Edit");
    }

    public MIPrivateWaystone(Material displayItem, ItemType itemType, int index, PrivateWaystone ws, PlayerMenuUtility playerMenuUtility) {
        super(displayItem, itemType);
        this.ws = ws;
        this.playerMenuUtility = playerMenuUtility;
        setItemName(ws.getName());
        saveIndexToNBT(index);
        setLoreDescription();
        setActionInfo("Teleport", "Edit");
    }

    private void setLoreDescription(){
        //Creates the lore of the item
        Location location = TeleportHandler.getParsedLocation(ws.getLocation());
        String worldName = getWorldName(location);

        Component locText = Component.text(worldName +": ", NamedTextColor.DARK_PURPLE)
                .append(Component.text(location.getBlockX()+", "+ location.getBlockY()+", "+location.getBlockZ(), NamedTextColor.LIGHT_PURPLE));

        loreDescription.add(locText);
        displayItemMeta.lore(loreDescription);
        displayItem.setItemMeta(displayItemMeta);
    }

}
