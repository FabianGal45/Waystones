package eu.ovmc.waystones.menusystem.items;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.handlers.TeleportHandler;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;

public class MIPublicWaystone extends MenuItem{
    private PublicWaystone ws;
    private PlayerMenuUtility playerMenuUtility;

    public MIPublicWaystone(Material displayItem, int index, PublicWaystone ws, PlayerMenuUtility playerMenuUtility) {
        super(displayItem, ItemType.PUBLIC_WAYSTONE);
        this.ws = ws;
        this.playerMenuUtility = playerMenuUtility;
        saveIndexToNBT(index);
        setItemName(ws.getName());
        setLoreDescription();
        setActionInfo();
        if(ws.getUserId() == playerMenuUtility.getUser().getId()){//If the current wasytone is owned by the player that oppened the menu then make it glow
            addGlint();
        }
    }

    public MIPublicWaystone(Material displayItem, ItemType itemType, int index, PublicWaystone ws, PlayerMenuUtility playerMenuUtility) {
        super(displayItem, itemType);
        this.ws = ws;
        this.playerMenuUtility = playerMenuUtility;
        saveIndexToNBT(index);
        setItemName(ws.getName());
        setLoreDescription();
        setActionInfo();
        if(ws.getUserId() == playerMenuUtility.getUser().getId()){//If the current wasytone is owned by the player that oppened the menu then make it glow
            addGlint();
        }
    }

    private void setLoreDescription(){
        Location location = TeleportHandler.getParsedLocation(ws.getLocation());
        User user = WaystonesPlugin.getPlugin().getJdbc().getUser(ws.getUserId());
        String userName = user.getUserName();
        String worldName = getWorldName(location);
        Economy econ = WaystonesPlugin.getEcon();
        int cost = ws.getCost();
        double rating = ws.getRating();
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

    private void setActionInfo(){
        //Set the Action info
        String leftClickAction;
        String rightClickAction;
        int cost = ws.getCost();

        //if there is a cost, and you are not the owner
        if(cost>0 && !playerMenuUtility.getUser().getUuid().equals(ws.getUserId())){
            leftClickAction = "Pay & Teleport";
        }else{
            leftClickAction = "Teleport";
        }

        //if the owner is the person opening the menu
        if(ws.getUserId() == playerMenuUtility.getUser().getId() || playerMenuUtility.isAdmin()){
            rightClickAction = "Edit";
        }
        else if(!WaystonesPlugin.getPlugin().getJdbc().hasPlayerRated(playerMenuUtility.getPlayer(),ws)){//if the player hasn't rated before
            rightClickAction = "Rate";
        }
        else{
            rightClickAction = null;
        }
        setActionInfo(leftClickAction,rightClickAction);
    }
}
