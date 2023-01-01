package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.ChatInputHandler;
import eu.ovmc.waystones.menusystem.Menu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditMenu extends Menu {
    PrivateWaystone selected;

    public EditMenu(PlayerMenuUtility playerMenuUtility, PrivateWaystone selected) {
        super(playerMenuUtility);
        this.selected = selected;
    }

    @Override
    public Component getMenuName() {
        if(playerMenuUtility.getOwner().getUniqueId().toString().equals(selected.getOwner()) || playerMenuUtility.getOwner().hasPermission("waystones.admin")) {
            return Component.text("Edit: " + selected.getName());
        }
        else{
            return Component.text("Rate: " + selected.getName());
        }
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        System.out.println("EditMenu - Thread: "+ Thread.currentThread().getName()+"; "+Thread.currentThread().getName());
        Player player = (Player) e.getWhoClicked();
        Material currentItem = e.getCurrentItem().getType();
        ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();
//        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

        if(currentItem.equals(Material.NAME_TAG)){ // Change name of waystone
            chatInputHandler.addToRenameMap(player,this); //pass the player to be added to the list and the menu to be oppened later

            player.sendMessage(Component.text("Enter new name: ", NamedTextColor.GRAY)
                    .append(Component.text(" [X]", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Or type \"cancel\"")))
                            .clickEvent(ClickEvent.runCommand("/ws cancelNameChange"))));

            inventory.close();
        }
        else if(currentItem.equals(Material.REDSTONE_BLOCK)){
            chatInputHandler.addToRemoveMap(player, this);

            player.sendMessage(Component.text("You or anyone else will not be able to teleport or use this waystone.", NamedTextColor.RED));

            player.sendMessage(Component.text("Are you sure? ", NamedTextColor.RED)
                    .append(Component.text(" [✔] ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Accept")))
                            .clickEvent(ClickEvent.runCommand("/ws confirmWsRemoval")))
                    .append(Component.text(" [X]", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Cancel")))
                            .clickEvent(ClickEvent.runCommand("/ws cancelWsRemoval"))));

            inventory.close();
        }
        else if(currentItem.equals(Material.DIAMOND)){
            chatInputHandler.addToCostMap(player,this);

            player.sendMessage(Component.text("Enter a price: ", NamedTextColor.GRAY)
                    .append(Component.text(" [X]", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Or type \"cancel\"")))
                            .clickEvent(ClickEvent.runCommand("/ws cancelCostChange"))));
            inventory.close();
        }
        else if (currentItem.equals(Material.NETHER_STAR)){
            //Grab the index stored in the item metadata
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));

            WaystonesPlugin.getPlugin().getJdbc().regRate((PublicWaystone) selected, player, index);

            inventory.close();
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
            //If the player is the owner of the waystone they are trying to edit
            if(playerMenuUtility.getOwner().getUniqueId().toString().equals(selected.getOwner()) || playerMenuUtility.getOwner().hasPermission("waystones.admin")){
                ItemStack nameTag = new ItemStack(Material.NAME_TAG);
                ItemMeta nameTagMeta = nameTag.getItemMeta();
                TextComponent ntName = Component.text("Rename");
                nameTagMeta.displayName(ntName);
                nameTag.setItemMeta(nameTagMeta);
                inventory.setItem(11, nameTag);

                ItemStack diamond = new ItemStack(Material.DIAMOND);
                ItemMeta dMeta = diamond.getItemMeta();
                TextComponent dName = Component.text("Cost");
                dMeta.displayName(dName);
                List<Component> loreArray = new ArrayList<>();
                loreArray.add(Component.text("Cost: "+ ((PublicWaystone)selected).getCost() + " Diamonds"));
                dMeta.lore(loreArray);
                diamond.setItemMeta(dMeta);
                inventory.setItem(13, diamond);

                ItemStack redstpmeBlock = new ItemStack(Material.REDSTONE_BLOCK);
                ItemMeta rbMeta = redstpmeBlock.getItemMeta();
                TextComponent rbName = Component.text("Remove");
                rbMeta.displayName(rbName);
                redstpmeBlock.setItemMeta(rbMeta);
                inventory.setItem(15, redstpmeBlock);
            }
            else{
                for(int i=1;i<=5;i++){
                    ItemStack star = new ItemStack(Material.NETHER_STAR);
                    ItemMeta sMeta = star.getItemMeta();
                    TextComponent sName;
                    if(i==1){
                        sName = Component.text(i +" Star");
                    }
                    else {
                        sName = Component.text(i +" Stars");
                    }
                    sMeta.displayName(sName);
                    sMeta.getPersistentDataContainer().set(new NamespacedKey(WaystonesPlugin.getPlugin(), "index"), PersistentDataType.INTEGER, i);
                    star.setItemMeta(sMeta);
                    inventory.setItem((10 + i), star);
                }
            }
        }
        else{ // if the Waystone is a Private waystone
            ItemStack nameTag = new ItemStack(Material.NAME_TAG);
            ItemMeta nameTagMeta = nameTag.getItemMeta();
            TextComponent ntName = Component.text("Rename");
            nameTagMeta.displayName(ntName);
            nameTag.setItemMeta(nameTagMeta);
            inventory.setItem(12, nameTag);

            ItemStack redstoneBlock = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta rbMeta = redstoneBlock.getItemMeta();
            TextComponent rbName = Component.text("Remove");
            rbMeta.displayName(rbName);
            redstoneBlock.setItemMeta(rbMeta);
            inventory.setItem(14, redstoneBlock);
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


    public PrivateWaystone getSelected() {
        return selected;
    }
}
