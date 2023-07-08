package eu.ovmc.waystones.menusystem;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.handlers.TeleportHandler;
import eu.ovmc.waystones.menusystem.items.ItemType;
import eu.ovmc.waystones.menusystem.items.MIRTDeathLocation;
import eu.ovmc.waystones.menusystem.items.MenuItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PaginatedMenu extends Menu {

    protected int page;
    protected ArrayList<Integer> blankSlots;


    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, int page) {
        super(playerMenuUtility);
        this.page = page;

        blankSlots = new ArrayList<>();
        Collections.addAll(blankSlots, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53);
    }

    public void addMenuBorder(){
        for(Integer blankSlot : blankSlots) {
            ItemStack blackPanel = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta bpMeta = blackPanel.getItemMeta();
            TextComponent bpName = Component.text(" ");
            bpMeta.displayName(bpName);
            blackPanel.setItemMeta(bpMeta);
            inventory.setItem(blankSlot, blackPanel);
        }
    }


    @Override
    public Component getMenuName() {
        return null;
    }

    @Override
    public int getSlots() {
        return 0;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {}

    @Override
    public void setMenuItems() {}

    public void commonMenuHandlers(InventoryClickEvent e){
        Material currentItem = e.getCurrentItem().getType();
        Player player = (Player) e.getWhoClicked();
        Location deathLocation;
        if(WaystonesPlugin.isIsCmiInstalled()){ //If CMI is installed then get the death location from CMI as it is more acurate
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(player);
//            System.out.println("User: "+ user.getDeathLoc());
            deathLocation = user.getDeathLoc();
//            System.out.println("CMI installed! ");
        }
        else{//Otherwise just use the spigot death location which is less acurate
            deathLocation = player.getLastDeathLocation();
        }

        if (currentItem.equals(Material.RECOVERY_COMPASS)) {
            if (deathLocation != null) {//if the player has died before
                playerMenuUtility.setTpCostMaterial(Material.ECHO_SHARD);
                TeleportHandler.safeTeleport(player, playerMenuUtility, deathLocation);
            } else {
                player.sendMessage(Component.text("You haven't died yet. ", NamedTextColor.DARK_RED)
                        .append(Component.text("Group hug with creepers?", NamedTextColor.RED)));
            }
        }
    }

    public void addCompass(PlayerMenuUtility playerMenuUtility){
        Player player = playerMenuUtility.getOwner();
        Location deathLocation = player.getLastDeathLocation();

        MIRTDeathLocation compassItem = new MIRTDeathLocation(Material.RECOVERY_COMPASS, ItemType.RETURN_TO_DEATH_LOCATION, "Death Location");

        // Lore/description
        compassItem.setLoreDescription(playerMenuUtility);

        inventory.setItem(49, compassItem.getDisplayItem());
    }


}
