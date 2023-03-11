package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.PaginatedPublicWsMenu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PublicWsMenu extends PaginatedPublicWsMenu {

    public PublicWsMenu(PlayerMenuUtility playerMenuUtility, int page, int prevIndexPubWs) {
        super(playerMenuUtility, page, prevIndexPubWs);
    }

    @Override
    public Component getMenuName() {
        return Component.text("Public Waystones");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ArrayList<PublicWaystone> publicWaystones = playerMenuUtility.getPublicWaystones();
        Material currentItem = e.getCurrentItem().getType();

        if(currentItem.equals(Material.BARRIER)){
            player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);
            if(page == 0){
                if(adminOpenedMenu == null){
                    new WaystonesSplitMenu(playerMenuUtility, startingPage-1).open();
                }else{
                    new WaystonesSplitMenu(playerMenuUtility, startingPage-1).openAs(adminOpenedMenu);
                }
            }
            else{
                if(adminOpenedMenu == null){
                    page = page - 1;
                    super.open();
                }else{
                    page = page - 1;
                    super.openAs(adminOpenedMenu);
                }
            }
        }
        else if(currentItem.equals(Material.ARROW)){//Make it more precise player can click on any arrow including personal inventory.
//            System.out.println("Next page was selected");
            player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);
            page = page + 1;
            if (adminOpenedMenu == null) {
                super.open();
            }else{
                super.openAs(adminOpenedMenu);
            }
        }
        else if(currentItem.equals(Material.NETHERITE_BLOCK)){
            player.sendMessage("You Clicked Netherite block!");
            //Grab the index from the NBT data of the block
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
//            System.out.println("NBT: index: "+index);
            PublicWaystone selected = publicWaystones.get(index);

            if(e.getClick() == ClickType.RIGHT){
//                System.out.println("Player: "+ player.getUniqueId() + " selected owner: "+ selected.getOwner());
                if(player.getUniqueId().toString().equals(selected.getOwner()) || player.hasPermission("waystones.admin")){
                    new PublicWaystoneEditMenu(playerMenuUtility, selected).open();
                }else{
                    if(!WaystonesPlugin.getPlugin().getJdbc().hasPlayerRated(player,selected)){
                        new PublicWaystoneRateEditMenu(playerMenuUtility, selected).open();
                    }else{
                        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
                    }
                }
            }
            else{
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, SoundCategory.BLOCKS, 1, 1);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(WaystonesPlugin.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        //safety feature
                        selected.safeTeleport(player, playerMenuUtility);
                    }
                },5);
            }
        }
        else if(currentItem.equals(Material.BLACK_CONCRETE)){
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
//            System.out.println("NBT: index: "+index);
            PublicWaystone selected = publicWaystones.get(index);

            if(e.getClick() == ClickType.RIGHT){
//                System.out.println("Player: "+ player.getUniqueId() + " selected owner: "+ selected.getOwner());
                if(player.getUniqueId().toString().equals(selected.getOwner()) || player.hasPermission("waystones.admin")){
                    new PublicWaystoneEditMenu(playerMenuUtility, selected).open();
                }else{
                    if(!WaystonesPlugin.getPlugin().getJdbc().hasPlayerRated(player,selected)){
                        new PublicWaystoneRateEditMenu(playerMenuUtility, selected).open();
                    }else{
                        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
                    }
                }
            }
            else{
                player.sendMessage("You are already at this location");
                player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
            }
        }
        else if(currentItem.equals(Material.CRACKED_STONE_BRICKS)){
            //Grab the index from the NBT data of the block
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));

            PublicWaystone selected = publicWaystones.get(index);
            player.sendMessage(Component.text("This waystone has been damaged! ("+selected.getParsedLocation(selected.getLocation()).getBlockX()+", "+selected.getParsedLocation(selected.getLocation()).getBlockY()+", "+ selected.getParsedLocation(selected.getLocation()).getBlockZ()+")",
                    TextColor.fromHexString("#802f45")));
        }

    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        ArrayList<PublicWaystone> publicWaystones = playerMenuUtility.getPublicWaystones();

        for(int i = 0; i < maxPublicWs; i++) {
//            System.out.println("indexPubWs: "+ indexPubWs + " = prevIndexPubWs: "+ prevIndexPubWs+" * page: "+ page + " + i:"+ i);
            indexPubWs = (maxPublicWs * page) + i + (prevIndexWs +1);
//            System.out.println("Index PUB: "+indexPubWs + " Page: "+ page);
            if(indexPubWs >= publicWaystones.size()){
//                System.out.println("STOP!");
                indexPubWs--;
                break;
            }
            PublicWaystone ws = publicWaystones.get(indexPubWs);
            if (ws != null) {
                ItemStack publicWs = new ItemStack(Material.NETHERITE_BLOCK);

                Block blockTop = ws.getParsedLocation(ws.getLocation()).getBlock();
                Block blockUnder = ws.getParsedLocation(ws.getLocation()).subtract(0.0,1.0,0.0).getBlock();

                boolean damagedWs = !(blockTop.getType().equals(Material.LODESTONE) && blockUnder.getType().equals(Material.NETHERITE_BLOCK));

                //if this is the waystone he clicked on make it lime green
                if(playerMenuUtility.getClickedOnWs() != null) {
                    if (ws.getLocation().equals(playerMenuUtility.getClickedOnWs().getLocation())) {
                        //Creates the LimeConcretePowder block item
                        publicWs = new ItemStack(Material.BLACK_CONCRETE);
                    } else if (damagedWs) {//if waystone is damaged, mark it with a cracked stone
                        publicWs = new ItemStack(Material.CRACKED_STONE_BRICKS);
                    } else {
                        //Creates the Emerald block item
                        publicWs = new ItemStack(Material.NETHERITE_BLOCK);
                    }
                }

                ItemMeta publicMeta = publicWs.getItemMeta();

                //Sets the name
                publicMeta.displayName(Component.text("Public Waystone").decoration(TextDecoration.ITALIC, false));

                //Creates the lore of the item
                List<Component> loreArray = new ArrayList<>();
                String worldName;
                if(ws.getParsedLocation(ws.getLocation()).getWorld().getName().equals("world")){
                    worldName = "World";
                } else if(ws.getParsedLocation(ws.getLocation()).getWorld().getName().equals("world_nether")) {
                    worldName = "Nether";
                }
                else if(ws.getParsedLocation(ws.getLocation()).getWorld().getName().equals("world_the_end")){
                    worldName = "End";
                }
                else{
                    worldName = "Unknown";
                }

                loreArray.add(Component.text(worldName +": "+ ws.getParsedLocation(ws.getLocation()).getBlockX()+", "+ ws.getParsedLocation(ws.getLocation()).getBlockY()+", "+ws.getParsedLocation(ws.getLocation()).getBlockZ()));
                publicMeta.lore(loreArray);

                //Stores the index of the waystone from the waystones list into the NBT meta of that file so that it can be identified when clicked.
                publicMeta.getPersistentDataContainer().set(new NamespacedKey(WaystonesPlugin.getPlugin(), "index"), PersistentDataType.INTEGER, indexPubWs);

                //Upates the meta with the provided one
                publicWs.setItemMeta(publicMeta);

                //Add the item to the inventory
                int pos = publicWsSlots.get(i);
                inventory.setItem(pos, publicWs);

            }

        }

        addMenuPageButtons(publicWaystones.size());
    }
}
