package eu.ovmc.waystones.menusystem;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.handlers.ChatInputHandler;
import eu.ovmc.waystones.handlers.TeleportHandler;
import eu.ovmc.waystones.menusystem.items.ItemType;
import eu.ovmc.waystones.menusystem.items.MIRTDeathLocation;
import eu.ovmc.waystones.menusystem.items.MenuItem;
import eu.ovmc.waystones.menusystem.menues.interactive.EditMenu;
import eu.ovmc.waystones.menusystem.menues.interactive.PublicWaystoneEditMenu;
import eu.ovmc.waystones.menusystem.menues.interactive.PublicWaystoneRateEditMenu;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

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
            MenuItem bkackPanel = new MenuItem(Material.BLACK_STAINED_GLASS_PANE, ItemType.BLANK, " ");
            inventory.setItem(blankSlot, bkackPanel.getDisplayItem());
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

    protected void commonMenuHandlers(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        ArrayList<PrivateWaystone> privateWaystones = playerMenuUtility.getPrivateWaystones();
        ArrayList<PublicWaystone> publicWaystones = playerMenuUtility.getPublicWaystones();
        Economy econ = WaystonesPlugin.getEcon();
        ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
        NamespacedKey itemTypeKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "item_type");
        ItemType currentItemType = ItemType.valueOf(itemMeta.getPersistentDataContainer().get(itemTypeKey,PersistentDataType.STRING));


        if(currentItemType == ItemType.PRIVATE_WAYSTONE){

            //Grab the index from the NBT data of the block
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.INTEGER));

            PrivateWaystone selected = privateWaystones.get(index);
            playerMenuUtility.setSelected(selected);

//            System.out.println(WaystonesPlugin.getPlugin().getDescription().getVersion());

            if(e.getClick() == ClickType.RIGHT){
                if(playerMenuUtility.getOwner() != null){
                    new EditMenu(playerMenuUtility, selected).open();
                }
                else{
                    new EditMenu(playerMenuUtility, selected).openAs(oppenedByAdmin);
                }

            }
            else{
                if(playerMenuUtility.getClickedOnWs() != null) {
                    //Check for nearby players and ask them if they want to be teleported
                    Location waystoneLocation = playerMenuUtility.getClickedOnWs().getParsedLocation(playerMenuUtility.getClickedOnWs().getLocation());
                    List<Player> tpaPlayerList = new ArrayList<>();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        //if in the same world
                        if (p.getLocation().getWorld().equals(waystoneLocation.getWorld())) {
                            double distance = p.getLocation().distance(waystoneLocation);
                            if (p != player && p.getWorld() == waystoneLocation.getWorld() && distance <= 5) {
                                //                            System.out.println("Player Nearby detected!");
                                //                            System.out.println("Distance: " + distance);
                                tpaPlayerList.add(p);
                            }
                        }
                    }

                    ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();
                    //if there are nearby players
                    if (tpaPlayerList.size() > 0) {
                        player.sendMessage(Component.text("Do you want to teleport with nearby players? ", NamedTextColor.YELLOW)
                                .append(Component.text(" [✔]", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                        .hoverEvent(HoverEvent.showText(Component.text("Accept")))
                                        .clickEvent(ClickEvent.runCommand("/ws confirmTpWithOthers")))
                                .append(Component.text(" [X]", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD)
                                        .hoverEvent(HoverEvent.showText(Component.text("Cancel")))
                                        .clickEvent(ClickEvent.runCommand("/ws cancelTpWithOthers"))));
                        playerMenuUtility.setTpaList(tpaPlayerList);
                        chatInputHandler.addToTpaMap(player, playerMenuUtility);
                    }
                }

                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, SoundCategory.BLOCKS, 1, 1);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(WaystonesPlugin.getPlugin(), new Runnable() { //add some delay before teleporting
                    @Override
                    public void run() {
                        //safety feature
                        selected.safeTeleportToWs(player, playerMenuUtility);
                        inventory.close();
                    }
                }, 5);
            }
        }
        else if(currentItemType == ItemType.PUBLIC_WAYSTONE){
            //Grab the index from the NBT data of the block
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
//            System.out.println("NBT: index: "+index);
            PublicWaystone selected = publicWaystones.get(index);

            if(e.getClick() == ClickType.RIGHT){
//                System.out.println("Player: "+ player.getUniqueId() + " selected owner: "+ selected.getOwner());
                //if the player is the owner of the PublicWaystone then open the right menu
                if(player.getUniqueId().toString().equals(selected.getOwner()) || player.hasPermission("waystones.admin")){
                    new PublicWaystoneEditMenu(playerMenuUtility, selected).open();
                }else{
                    if(!WaystonesPlugin.getPlugin().getJdbc().hasPlayerRated(player,selected)){//player hasn't rated the waystone
                        new PublicWaystoneRateEditMenu(playerMenuUtility, selected).open();
                    }else{//player has already rated the waystons
                        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
                    }
                }
            }
            else{
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, SoundCategory.BLOCKS, 1, 1);

                //if the player is not the owner of the waystone make him pay the cost
                if(!player.getUniqueId().toString().equals(selected.getOwner())){
                    //pay the cost before teleporting
                    EconomyResponse withdraw = econ.withdrawPlayer(player,selected.getCost());
                    OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(selected.getOwner()));


                    if(withdraw.transactionSuccess()){
                        if(selected.getCost()>0){
                            EconomyResponse deposit = econ.depositPlayer(owner, selected.getCost());
                            if(deposit.transactionSuccess()){
                                player.sendMessage(Component.text("You have paid ", NamedTextColor.GRAY)
                                        .append(Component.text(selected.getCost() + " Diamonds",NamedTextColor.AQUA))
                                        .append(Component.text(" to ", NamedTextColor.GRAY))
                                        .append(Component.text(Objects.requireNonNull(owner.getName()))));
                            }
                        }
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(WaystonesPlugin.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                //safety feature
                                selected.safeTeleportToWs(player, playerMenuUtility);
                                inventory.close();
                            }
                        },5);
                    }else{
                        player.sendMessage(Component.text("You don't have ", NamedTextColor.RED)
                                .append(Component.text(selected.getCost() + " Diamonds")));
                    }
                }else{
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(WaystonesPlugin.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            //safety feature
                            selected.safeTeleportToWs(player, playerMenuUtility);
                            inventory.close();
                        }
                    },5);
                }


            }


        }
        else if(currentItemType == ItemType.OPENED_PRIVATE_WAYSTONE || currentItemType == ItemType.OPENED_PUBLIC_WAYSTONE){
            //Grab the index from the NBT data of the block
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
            PrivateWaystone selected;

            if(currentItemType == ItemType.OPENED_PRIVATE_WAYSTONE){
                selected = privateWaystones.get(index);
            }
            else{
                selected = publicWaystones.get(index);
            }


            if(e.getClick() == ClickType.RIGHT){
//                System.out.println("Player: "+ player.getUniqueId() + " selected owner: "+ selected.getOwner());
                //if the player is the owner of the PublicWaystone then open the right menu
                openEditMenu(player, selected);
            }
            else{
                player.sendMessage("You are already at this location");
                player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
            }


        }
        else if(currentItemType == ItemType.BROKEN){
            //Grab the index from the NBT data of the block
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));

            PrivateWaystone selected;

            if(e.getSlot()<17){
                selected = privateWaystones.get(index);
            }
            else{
                selected = publicWaystones.get(index);
            }

            if(e.getClick() == ClickType.RIGHT){
//                System.out.println("Player: "+ player.getUniqueId() + " selected owner: "+ selected.getOwner());
                //if the player is the owner of the PublicWaystone then open the right menu
                openEditMenu(player, selected);
            }
            else{
                player.sendMessage(Component.text("This waystone has been damaged! ("+selected.getParsedLocation(selected.getLocation()).getBlockX()+", "+selected.getParsedLocation(selected.getLocation()).getBlockY()+", "+ selected.getParsedLocation(selected.getLocation()).getBlockZ()+")",
                        TextColor.fromHexString("#802f45")));
            }

        }
        else if(currentItemType == ItemType.PURCHASE){
            User user = playerMenuUtility.getUser();
            boolean purchaseSuccess = user.purchaseWaystone(playerMenuUtility);
            if(purchaseSuccess){
                super.open();
            }

        }
        else if (currentItemType == ItemType.RETURN_TO_DEATH_LOCATION) {
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

            if (deathLocation != null) {//if the player has died before
                playerMenuUtility.setTpCostMaterial(Material.ECHO_SHARD);
                TeleportHandler.safeTeleport(player, playerMenuUtility, deathLocation);
            } else {
                player.sendMessage(Component.text("You haven't died yet. ", NamedTextColor.DARK_RED)
                        .append(Component.text("Group hug with creepers?", NamedTextColor.RED)));
            }
        }
    }

    private void openEditMenu(Player player, PrivateWaystone selected) {
        if(selected instanceof PublicWaystone){
            if(player.getUniqueId().toString().equals(selected.getOwner()) || player.hasPermission("waystones.admin")){
                new PublicWaystoneEditMenu(playerMenuUtility, selected).open();
            }else{
                if(!WaystonesPlugin.getPlugin().getJdbc().hasPlayerRated(player, (PublicWaystone) selected)){
                    new PublicWaystoneRateEditMenu(playerMenuUtility, selected).open();
                }else{
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
                }
            }
        }
        else{
            if(player.getUniqueId().toString().equals(selected.getOwner()) || player.hasPermission("waystones.admin")){
                new EditMenu(playerMenuUtility, selected).open();
            }
            else{
                player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
            }
        }
    }

    public void addCompass(PlayerMenuUtility playerMenuUtility){
        MIRTDeathLocation compassItem = new MIRTDeathLocation(Material.RECOVERY_COMPASS, ItemType.RETURN_TO_DEATH_LOCATION, "Death Location");
        compassItem.setLoreDescription(playerMenuUtility);
        inventory.setItem(49, compassItem.getDisplayItem());
    }


}
