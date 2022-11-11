package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.ChatInputHandler;
import eu.ovmc.waystones.menusystem.EditMenuUtility;
import eu.ovmc.waystones.menusystem.Menu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EditMenu extends Menu {
    PrivateWaystone selected;

    public EditMenu(PlayerMenuUtility playerMenuUtility, PrivateWaystone selected) {
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
        Player player = (Player) e.getWhoClicked();
        Material currentItem = e.getCurrentItem().getType();
//        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

        if(currentItem.equals(Material.NAME_TAG)){
            player.sendMessage("Enter new name for: "+ selected.getName());
            inventory.close();

            ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();
            EditMenuUtility editMenuUtility = new EditMenuUtility(selected,this);
            chatInputHandler.changeWsName(player, editMenuUtility); //pass the player to be added to the list and the menu to be oppened later
        }

    }

    @Override
    public void setMenuItems() {
        fillWithBlack();
        //Formula for spreading the items evenly in the inventory
        // 8 / 2 = 4 - Middle
        // 4 - items = x
        // x + 1 = startingLocation
        // addToArray(startingLocation)
        // for(items - 1) { addToArray(startingLocation + 2) }

        if(selected instanceof PublicWaystone){ // if the waystone is a public waystone
            ItemStack nameTag = new ItemStack(Material.NAME_TAG);
            ItemMeta nameTagMeta = nameTag.getItemMeta();
            TextComponent ntName = Component.text("Name");
            nameTagMeta.displayName(ntName);
            nameTag.setItemMeta(nameTagMeta);
            inventory.setItem(10, nameTag);

            ItemStack diamond = new ItemStack(Material.DIAMOND);
            ItemMeta dMeta = diamond.getItemMeta();
            TextComponent dName = Component.text("Price");
            dMeta.displayName(dName);
            diamond.setItemMeta(dMeta);
            inventory.setItem(12, diamond);

            ItemStack chest = new ItemStack(Material.CHEST);
            ItemMeta cMeta = chest.getItemMeta();
            TextComponent cName = Component.text("Category");
            cMeta.displayName(cName);
            chest.setItemMeta(cMeta);
            inventory.setItem(14, chest);

            ItemStack redstpmeBlock = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta rbMeta = redstpmeBlock.getItemMeta();
            TextComponent rbName = Component.text("Delete");
            rbMeta.displayName(rbName);
            redstpmeBlock.setItemMeta(rbMeta);
            inventory.setItem(16, redstpmeBlock);

        }
        else{ // if the Waystone is a Private waystone
            ItemStack nameTag = new ItemStack(Material.NAME_TAG);
            ItemMeta nameTagMeta = nameTag.getItemMeta();
            TextComponent ntName = Component.text("Name");
            nameTagMeta.displayName(ntName);
            nameTag.setItemMeta(nameTagMeta);
            inventory.setItem(11, nameTag);

            ItemStack emeraldBlock = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta ebMeta = emeraldBlock.getItemMeta();
            TextComponent ebName = Component.text("Add to my list");
            ebMeta.displayName(ebName);
            emeraldBlock.setItemMeta(ebMeta);
            inventory.setItem(13, emeraldBlock);

            ItemStack redstoneBlock = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta rbMeta = redstoneBlock.getItemMeta();
            TextComponent rbName = Component.text("Delete");
            rbMeta.displayName(rbName);
            redstoneBlock.setItemMeta(rbMeta);
            inventory.setItem(15, redstoneBlock);
        }

    }

    public void fillWithBlack(){
        for(int i=0; i<this.getSlots();i++) {
            ItemStack blackPanel = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta bpMeta = blackPanel.getItemMeta();
            TextComponent bpName = Component.text(" ");
            bpMeta.displayName(bpName);
            blackPanel.setItemMeta(bpMeta);
            inventory.setItem(i, blackPanel);
        }
    }
}
