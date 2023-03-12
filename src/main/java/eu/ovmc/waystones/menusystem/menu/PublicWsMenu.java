package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.PaginatedMenu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PublicWsMenu extends PaginatedMenu {
    private final int MAX_PUBLIC_WS = 28;
    private int indexPubWs = 0;
    private ArrayList<Integer> publicWsSlots;
    private int carriedIndexPubWs;

    public PublicWsMenu(PlayerMenuUtility playerMenuUtility, int page, int carriedIndexPubWs) {
        super(playerMenuUtility, page);

        this.carriedIndexPubWs = carriedIndexPubWs;

        publicWsSlots = new ArrayList<>();
        Collections.addAll(publicWsSlots, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
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
        commonMenuHandlers(e);

        Player player = (Player) e.getWhoClicked();
        ArrayList<PublicWaystone> publicWaystones = playerMenuUtility.getPublicWaystones();
        Material currentItem = e.getCurrentItem().getType();

        if(currentItem.equals(Material.BARRIER)){
            player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);
            if(page == 1){
                if(oppenedByAdmin == null){
                    new WaystonesSplitMenu(playerMenuUtility, 0).open();
                }else{
                    new WaystonesSplitMenu(playerMenuUtility, 0).openAs(oppenedByAdmin);
                }
            }
            else{
                if(oppenedByAdmin == null){
                    page = page - 1;
                    super.open();
                }else{
                    page = page - 1;
                    super.openAs(oppenedByAdmin);
                }
            }
        }
        else if(currentItem.equals(Material.ARROW)){//Make it more precise player can click on any arrow including personal inventory.
//            System.out.println("Next page was selected");
            player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);
            page = page + 1;
            if (oppenedByAdmin == null) {
                super.open();
            }else{
                super.openAs(oppenedByAdmin);
            }
        }
        else if(currentItem.equals(Material.NETHERITE_BLOCK)){
            //Grab the index from the NBT data of the block
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
//            System.out.println("NBT: index: "+index);
            PublicWaystone selected = publicWaystones.get(index);

            if(e.getClick() == ClickType.RIGHT){
//                System.out.println("Player: "+ player.getUniqueId() + " selected owner: "+ selected.getOwner());
                openEditMenu(player, selected);
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
                openEditMenu(player, selected);
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

            if(e.getClick() == ClickType.RIGHT){
//                System.out.println("Player: "+ player.getUniqueId() + " selected owner: "+ selected.getOwner());
                openEditMenu(player, selected);
            }
            else{
                player.sendMessage(Component.text("This waystone has been damaged! ("+selected.getParsedLocation(selected.getLocation()).getBlockX()+", "+selected.getParsedLocation(selected.getLocation()).getBlockY()+", "+ selected.getParsedLocation(selected.getLocation()).getBlockZ()+")",
                        TextColor.fromHexString("#802f45")));
            }

        }

    }

    private void openEditMenu(Player player, PublicWaystone selected) {
        if(player.getUniqueId().toString().equals(selected.getOwner()) || player.hasPermission("waystones.admin")){
            new PublicWaystoneEditMenu(playerMenuUtility, selected).open();
        }else{
            if(!WaystonesPlugin.getPlugin().getJdbc().hasPlayerRated(player,selected)){
                new PublicWaystoneRateEditMenu(playerMenuUtility, selected).open();
            }else{
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
            }
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        ArrayList<PublicWaystone> publicWaystones = playerMenuUtility.getPublicWaystones();

        for(int i = 0; i < MAX_PUBLIC_WS; i++) {
            indexPubWs = (MAX_PUBLIC_WS * page) + i - (carriedIndexPubWs + 1); // (28*1)+0+(0+1)=29 | (28*1)+1+(0+1)=30 | (28*1)+2+(0+1)=31
//            System.out.println("indexPubWs: "+ indexPubWs + " = ("+ MAX_PUBLIC_WS +" * "+ page + ") + "+ i + " - ("+carriedIndexPubWs + "+ 1)  | Page: "+ page + " PublicWaystones Size: "+ publicWaystones.size());

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


                if(ws.getOwner().equals(playerMenuUtility.getOwnerUUID().toString())){
                    publicMeta.addEnchant(Enchantment.DAMAGE_ALL,0, true);
                    publicMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
        addCompass(playerMenuUtility);
    }

    private void addMenuPageButtons(int pubWsSize){
//        System.out.println("pubWsSize: "+ pubWsSize +" " + (pubWsSize - prevIndexPubWs -1 ) + " > "+ (maxPublicWs * (page+1)) );
        if(pubWsSize - 1 > MAX_PUBLIC_WS * page){
            inventory.setItem(50, makeItem(Material.ARROW, "Next Page"));
        }

        inventory.setItem(48, makeItem(Material.BARRIER, "Back"));


    }
}
