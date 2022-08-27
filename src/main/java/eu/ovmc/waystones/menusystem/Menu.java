package eu.ovmc.waystones.menusystem;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Menu implements InventoryHolder {

    //Get the data of the player in the PlayerMenuUtility
    protected PlayerMenuUtility playerMenuUtility;

    protected Inventory inventory;


    public Menu(PlayerMenuUtility playerMenuUtility){
        this.playerMenuUtility = playerMenuUtility;
    }

    public abstract Component getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems();

    public void open(){
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();

        playerMenuUtility.getOwner().openInventory(inventory);
    }
    @Override
    public @NotNull Inventory getInventory(){
        return inventory;
    }

    public ItemStack makeItem(Material material, String displayName, String... lore){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.text(displayName));

        List<Component> loreList = new ArrayList<>();
        for(int i = 0; i<Arrays.asList(lore).size(); i++){
            loreList.add(Component.text(Arrays.asList(lore).get(i)));
        }
        itemMeta.lore(loreList);
        item.setItemMeta(itemMeta);

        return item;
    }

}