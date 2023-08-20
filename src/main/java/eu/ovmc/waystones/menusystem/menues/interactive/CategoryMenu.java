package eu.ovmc.waystones.menusystem.menues.interactive;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.Menu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.menusystem.items.ItemType;
import eu.ovmc.waystones.menusystem.items.MICategory;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PubWsCategory;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class CategoryMenu extends Menu {
    private PublicWaystone selected;
    public CategoryMenu(PlayerMenuUtility playerMenuUtility, PublicWaystone selected) {
        super(playerMenuUtility);
        this.selected = selected;
    }

    @Override
    public Component getMenuName() {
        return Component.text("Edit: " + selected.getName());
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemMeta itemMeta = e.getCurrentItem().getItemMeta();

        NamespacedKey itemTypeKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "item_type");
        ItemType currentItemType = ItemType.valueOf(itemMeta.getPersistentDataContainer().get(itemTypeKey,PersistentDataType.STRING));

        if(currentItemType == ItemType.CATEGORY){
            //Grab the currentItemCategory from the NBT data of the block
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "item_category");
            PubWsCategory currentItemCategory = PubWsCategory.valueOf(Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING)));

            if(currentItemCategory == PubWsCategory.STAFF){

            }
            //Set the current item Material and the Category to the Public Waystone object.
            selected.setCustomItem(e.getCurrentItem().getType().toString());
            selected.setCategory(currentItemCategory.toString());

            WaystonesPlugin.getPlugin().getJdbc().updateWaystone(selected);

            inventory.close();
        }
    }

    @Override
    public void setMenuItems() {
        fillAllWithBlack();

        MICategory remove = new MICategory(ItemType.CATEGORY, "Default",PubWsCategory.DEFAULT);
        inventory.setItem(10, remove.getDisplayItem());

        MICategory playerHome = new MICategory(ItemType.CATEGORY, "Player Home",PubWsCategory.PLAYER_HOME);
        inventory.setItem(11, playerHome.getDisplayItem());

        MICategory mobFarm = new MICategory(ItemType.CATEGORY, "Mob Farm",PubWsCategory.MOB_FARM);
        inventory.setItem(12, mobFarm.getDisplayItem());

        MICategory shop = new MICategory(ItemType.CATEGORY, "Shop",PubWsCategory.SHOP);
        inventory.setItem(13, shop.getDisplayItem());

        MICategory waypoint = new MICategory(ItemType.CATEGORY, "Waypoint",PubWsCategory.WAYPOINT);
        inventory.setItem(14, waypoint.getDisplayItem());

        MICategory pvp = new MICategory(ItemType.CATEGORY, "PVP",PubWsCategory.PVP);
        inventory.setItem(15, pvp.getDisplayItem());

        MICategory fun = new MICategory(ItemType.CATEGORY, "Fun",PubWsCategory.FUN);
        inventory.setItem(16, fun.getDisplayItem());

        if(playerMenuUtility.isAdmin()){
            MICategory staff = new MICategory(ItemType.CATEGORY, "Staff",PubWsCategory.STAFF);
            inventory.setItem(22, staff.getDisplayItem());
        }

    }
}
