package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.events.ChatInputHandler;
import eu.ovmc.waystones.menusystem.PaginatedMenu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
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

import java.text.DecimalFormat;
import java.util.*;

public class WaystonesSplitMenu extends PaginatedMenu {

    private final int MAX_PRIVATE = 7;
    private final int MAX_PUBLIC = 14;
    private int indexPubWs = 0;
    private int prevIndexWs;
    private final ArrayList<Integer> PUBLIC_WS_SLOTS;


    public WaystonesSplitMenu(PlayerMenuUtility playerMenuUtility, int page) {
        super(playerMenuUtility, page);

        //define new border for the blank slots aside from the default one in PaginatedMenu
        blankSlots = new ArrayList<>();
        Collections.addAll(blankSlots, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53);

        PUBLIC_WS_SLOTS = new ArrayList<>();
        Collections.addAll(PUBLIC_WS_SLOTS, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
    }

    @Override
    public Component getMenuName() {
        if(playerMenuUtility.getClickedOnWs() != null){
            String waystoneOwnerUUid = playerMenuUtility.getClickedOnWs().getOwner();
            User user = WaystonesPlugin.getPlugin().getJdbc().getUserFromDB(waystoneOwnerUUid);
            return Component.text( user.getUserName()+ "'s Waystone"); //todo fix this with openAs
        }else{
            return Component.text("OPEN AS");
        }

    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        commonMenuHandlers(e);

        Player player = (Player) e.getWhoClicked();

        ArrayList<PrivateWaystone> privateWaystones = playerMenuUtility.getPrivateWaystones();
        ArrayList<PublicWaystone> publicWaystones = playerMenuUtility.getPublicWaystones();
        Economy econ = WaystonesPlugin.getEcon();

        Material currentItem = e.getCurrentItem().getType();



        if(currentItem.equals(Material.EMERALD_BLOCK)){

            //Grab the index from the NBT data of the block
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
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
                        selected.safeTeleport(player, playerMenuUtility);
                        inventory.close();
                    }
                }, 5);
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
                //if the player is the owner of the PublicWaystone then open the right menu
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

                //if the player is not the owner of the waystone make him pay the cost
                if(!player.getUniqueId().toString().equals(selected.getOwner())){
                    //pay the cost before teleporting
                    EconomyResponse withdraw = econ.withdrawPlayer(player,selected.getCost());
                    OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(selected.getOwner()));
                    EconomyResponse deposit = econ.depositPlayer(owner, selected.getCost());

                    if(withdraw.transactionSuccess() && deposit.transactionSuccess()){
                        if(selected.getCost()>0){
                            player.sendMessage(Component.text("You have paid ", NamedTextColor.GRAY)
                                    .append(Component.text(selected.getCost() + " Diamonds",NamedTextColor.AQUA))
                                    .append(Component.text(" to ", NamedTextColor.GRAY))
                                    .append(Component.text(Objects.requireNonNull(owner.getName()))));
                        }
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(WaystonesPlugin.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                //safety feature
                                selected.safeTeleport(player, playerMenuUtility);
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
                            selected.safeTeleport(player, playerMenuUtility);
                            inventory.close();
                        }
                    },5);
                }


            }


        }
        else if(currentItem.equals(Material.LIME_CONCRETE) || currentItem.equals(Material.BLACK_CONCRETE)){
            //Grab the index from the NBT data of the block
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
            PrivateWaystone selected;

            if(currentItem.equals(Material.LIME_CONCRETE)){
                selected = privateWaystones.get(index);
            }
            else{
                selected = publicWaystones.get(index);
            }


            if(e.getClick() == ClickType.RIGHT){
                System.out.println("Player: "+ player.getUniqueId() + " selected owner: "+ selected.getOwner());
                //if the player is the owner of the PublicWaystone then open the right menu
                if(selected instanceof PublicWaystone){
                    if(player.getUniqueId().toString().equals(selected.getOwner()) || player.hasPermission("waystones.admin")){
                        new PublicWaystoneEditMenu(playerMenuUtility, selected).open();
                    }else{
                        if(!WaystonesPlugin.getPlugin().getJdbc().hasPlayerRated(player, (PublicWaystone) selected)){
                            new PublicWaystoneRateEditMenu(playerMenuUtility, selected).open();
                        }else{
                            player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
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

            if(e.getSlot()>16){
                PublicWaystone selected = publicWaystones.get(index);
                player.sendMessage(Component.text("This waystone has been damaged! ("+selected.getParsedLocation(selected.getLocation()).getBlockX()+", "+selected.getParsedLocation(selected.getLocation()).getBlockY()+", "+ selected.getParsedLocation(selected.getLocation()).getBlockZ()+")",
                        TextColor.fromHexString("#802f45")));
            }
            else{
                PrivateWaystone selected = privateWaystones.get(index);
                player.sendMessage(Component.text("This waystone has been damaged! ("+selected.getParsedLocation(selected.getLocation()).getBlockX()+", "+selected.getParsedLocation(selected.getLocation()).getBlockY()+", "+ selected.getParsedLocation(selected.getLocation()).getBlockZ()+")",
                        TextColor.fromHexString("#802f45")));
            }

        }
        else if(currentItem.equals(Material.GRAY_DYE)){
            User user = playerMenuUtility.getUser();
            boolean purchaseSuccess = user.purchaseWaystone(playerMenuUtility);
            if(purchaseSuccess){
                super.open();
            }

        }
        else if(currentItem.equals(Material.BARRIER)){
            String itemName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(e.getCurrentItem().getItemMeta().displayName()));
            player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);
            if (itemName.equals("Back")) {
                if (page == 0) {
                    player.sendMessage("This is the first page.");
                } else {
                    if(oppenedByAdmin == null){
                        page = page - 1;
                        super.open();
                    }
                    else{
                        page = page - 1;
                        super.openAs(oppenedByAdmin);
                    }

                }
//            ArrayList<SplitMenu> GUIs = plugin.getArrGUIs();
            }
        }
        else if(currentItem.equals(Material.ARROW)){
//            System.out.println("Next page was selected");
            player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);

            User user = playerMenuUtility.getUser();
//            System.out.println(">>>> "+ (user.getAllowedPrivWs())+ " < "+ (MAX_PRIVATE * (page + 1)));
            if(user.getAllowedPrivWs() < (MAX_PRIVATE * (page + 1))){ //get allowed < page max
                if(oppenedByAdmin == null){ //if this menu has no admin assigned that might have oppened it then run it for the player
                    page = page + 1;
                    new PublicWsMenu(playerMenuUtility, page, indexPubWs).open();
                } else{ //if there is an admin that has oppened this menu then continue openning for the admin
                    page = page + 1;
                    new PublicWsMenu(playerMenuUtility, page, indexPubWs).openAs(oppenedByAdmin);
                }
            }
            else{
                if(oppenedByAdmin == null){ //if this menu has no admin assigned that might have oppened it then run it for the player
                    page = page + 1;
                    super.open();
                } else{ //if there is an admin that has oppened this menu then continue openning for the admin
                    page = page + 1;
                    super.openAs(oppenedByAdmin);
                }
            }

        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        ArrayList<PrivateWaystone> privateWaystones = playerMenuUtility.getPrivateWaystones();
        ArrayList<PublicWaystone> publicWaystones = playerMenuUtility.getPublicWaystones();
        Economy econ = WaystonesPlugin.getEcon();

        //loop for each slot available to private waystones (7)
        for(int i = 0; i < MAX_PRIVATE; i++) {
            prevIndexWs = MAX_PRIVATE * page + i;//7*0+1= 1 | 7*0+2= 2 | ... | 7*1+1= 8 | 7*1+2= 9

            //If finished placing the existing waystones, before running out of space start placing the dyes
            if(prevIndexWs >= privateWaystones.size()){
                User user = playerMenuUtility.getUser();
                int pu = user.getAllowedPrivWs()-privateWaystones.size();//the amount of purchased and unused waystones

                //loop 7 times
                for(int j = 0; j< MAX_PRIVATE; j++){

                    //the position at which the dye to be placed
                    int dyePos = prevIndexWs -(MAX_PRIVATE * page)+10;// If there are 5 waystones in the first menu: 5-(7*0)+10 = 15

                    //if green dye have reached the pu AND the dyePos <= 16 (16 being the last available position in the GUI)
//                    System.out.println(">>>>>  "+ indexPrivWs +" - " + privateWaystones.size() +" < pu: "+pu);
                    if(prevIndexWs - privateWaystones.size() < pu && dyePos <= 16){
                        ItemStack limeDye = new ItemStack(Material.LIME_DYE);
                        ItemMeta limeMeta = limeDye.getItemMeta();
                        limeMeta.displayName(Component.text("Available").color(TextColor.fromCSSHexString("#93cf98")).decoration(TextDecoration.ITALIC, false));
                        limeDye.setItemMeta(limeMeta);

                        inventory.setItem(dyePos ,limeDye);
                    }
                    else{
                        //if there is still space, add a grey dye
//                        System.out.println("indexPrivWs: "+ indexPrivWs + " size: "+ privateWaystones.size() + " PU: "+ pu + " allowed: "+user.getAllowedPrivWs());
                        if(prevIndexWs < MAX_PRIVATE*(page+1) && user.getAllowedPrivWs() == prevIndexWs){

                            DecimalFormat formatter = new DecimalFormat("#,###");

                            ItemStack grayDye = new ItemStack(Material.GRAY_DYE);
                            ItemMeta grayMeta = grayDye.getItemMeta();
                            grayMeta.displayName(Component.text("Buy more").color(NamedTextColor.WHITE));
                            List<Component> loreArray = new ArrayList<>();

                            double discount = user.getDiscount(playerMenuUtility);

                            if(discount>0){
                                Component discountComp = Component.text("-" +Math.round(discount*100)+"% ",NamedTextColor.AQUA);

                                Component oldPrice = Component.text(formatter.format(user.getCostOfNextWs()),NamedTextColor.DARK_GRAY)
                                        .decoration(TextDecoration.STRIKETHROUGH, true);

                                Component newPrice = Component.text(" "+econ.format(Math.round(user.getCostOfNextWs() * (1-discount))), NamedTextColor.AQUA)
                                        .decoration(TextDecoration.STRIKETHROUGH, false);

                                Component displayWithDiscount = Component.text("Cost: ",NamedTextColor.GRAY)
                                        .append(discountComp)
                                        .append(oldPrice)
                                        .append(newPrice);

                                loreArray.add(displayWithDiscount);
                            }
                            else{
                                loreArray.add(Component.text("Cost: ", NamedTextColor.GRAY)
                                        .append(Component.text(econ.format(user.getCostOfNextWs()), NamedTextColor.WHITE)));
                            }
                            loreArray.add(Component.text(""));
                            if(Bukkit.getServer().getPluginManager().getPlugin("VotingPlugin")!=null){ // if voting plugin has been installed.
                                loreArray.add(Component.text("1 Vote = 1 Point = 1% Discount", NamedTextColor.DARK_GRAY));
                            }
                            loreArray.add(Component.text("Balance: ", NamedTextColor.DARK_GRAY).append(Component.text(econ.format(econ.getBalance(playerMenuUtility.getOwner())), NamedTextColor.DARK_GRAY)));
                            grayMeta.lore(loreArray);
                            grayDye.setItemMeta(grayMeta);


                            inventory.addItem(grayDye);
                            prevIndexWs--;
                        }
//                        System.out.println("INDEX>>> "+ indexPrivWs);
                        break;
                    }
                    prevIndexWs++;
                }
//                System.out.println("INDEX>>> "+ indexPrivWs);
                break; //If the index has reached the number of waystones.
            }

            PrivateWaystone ws = privateWaystones.get(prevIndexWs);
            if (ws != null){
                ItemStack privateWs= new ItemStack(Material.EMERALD_BLOCK);

                Block blockTop = ws.getParsedLocation(ws.getLocation()).getBlock();
                Block blockUnder = ws.getParsedLocation(ws.getLocation()).subtract(0.0,1.0,0.0).getBlock();

                boolean damagedWs = !(blockTop.getType().equals(Material.LODESTONE) && blockUnder.getType().equals(Material.EMERALD_BLOCK));

                //if this is the waystone he opened the menu from, make it lime green
                if(playerMenuUtility.getClickedOnWs() != null){
                    if(ws.getLocation().equals(playerMenuUtility.getClickedOnWs().getLocation())){
                        //Creates the LimeConcretePowder block item
                        privateWs = new ItemStack(Material.LIME_CONCRETE);
                    }
                    else if(damagedWs){//if waystone is damaged, mark it with a cracked stone
                        privateWs = new ItemStack(Material.CRACKED_STONE_BRICKS);
                    }

                }

                ItemMeta ptivateWsMeta = privateWs.getItemMeta();

                //Sets the name
                if(ws.getName() == null){
                    ptivateWsMeta.displayName(Component.text("Null")
                            .decoration(TextDecoration.ITALIC, false));
                }
                else{
                    ptivateWsMeta.displayName(Component.text(ws.getName())
                            .decoration(TextDecoration.ITALIC, false));
                }

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
                Component location = Component.text(worldName +": ", NamedTextColor.DARK_PURPLE)
                        .append(Component.text(ws.getParsedLocation(ws.getLocation()).getBlockX()+", "+ ws.getParsedLocation(ws.getLocation()).getBlockY()+", "+ws.getParsedLocation(ws.getLocation()).getBlockZ(), NamedTextColor.LIGHT_PURPLE));

                Component blank = Component.text("");

                Component lClick = Component.text("L-Click: ", NamedTextColor.DARK_GRAY)
                        .append(Component.text("Teleport", NamedTextColor.GRAY));

                Component rClick = Component.text("R-Click: ", NamedTextColor.DARK_GRAY)
                        .append(Component.text("Edit", NamedTextColor.GRAY));

                loreArray.add(location);
                loreArray.add(blank);
                loreArray.add(lClick);
                loreArray.add(rClick);
                ptivateWsMeta.lore(loreArray);

                //Stores the index of the waystone from the waystones list into the NBT meta of that file so that it can be identified when clicked.
                ptivateWsMeta.getPersistentDataContainer().set(new NamespacedKey(WaystonesPlugin.getPlugin(), "index"), PersistentDataType.INTEGER, prevIndexWs);

                //Upates the meta with the provided one
                privateWs.setItemMeta(ptivateWsMeta);

                //Add the item to the inventory
                inventory.addItem(privateWs);
            }
        }

        for(int i = 0; i < MAX_PUBLIC; i++) {
            indexPubWs = MAX_PUBLIC * page + i;
//            System.out.println("INDEX>>> "+ indexPrivWs);
//            System.out.println("Index PUB: "+indexPubWs + " Page: "+ page);
            if(indexPubWs >= publicWaystones.size()){
                indexPubWs--;
                break;
            }
            PublicWaystone ws = publicWaystones.get(indexPubWs);
            if (ws != null) {
                ItemStack publicWs = new ItemStack(Material.NETHERITE_BLOCK);

                Block blockTop = ws.getParsedLocation(ws.getLocation()).getBlock();
                Block blockUnder = ws.getParsedLocation(ws.getLocation()).subtract(0.0,1.0,0.0).getBlock();

                boolean damagedWs = !(blockTop.getType().equals(Material.LODESTONE) && blockUnder.getType().equals(Material.NETHERITE_BLOCK));
                int cost = ws.getCost();

                //if this is the waystone he clicked on make it Black Concrete
                if(playerMenuUtility.getClickedOnWs() != null){
                    if(ws.getLocation().equals(playerMenuUtility.getClickedOnWs().getLocation())){
                        //Creates the LimeConcretePowder block item
                        publicWs = new ItemStack(Material.BLACK_CONCRETE);
                    }
                    else if(damagedWs){//if waystone is damaged, mark it with a cracked stone
                        publicWs = new ItemStack(Material.CRACKED_STONE_BRICKS);
                    }
                }

                ItemMeta publicMeta = publicWs.getItemMeta();

                //Sets the name
                if(ws.getName() == null){
                    publicMeta.displayName(Component.text("Null").decoration(TextDecoration.ITALIC, false));
                }
                else{
                    publicMeta.displayName(Component.text(ws.getName()).decoration(TextDecoration.ITALIC, false));
                }

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

                User user = WaystonesPlugin.getPlugin().getJdbc().getUserFromDB(ws.getOwner());
                Component location =Component.text(worldName +": ",NamedTextColor.DARK_PURPLE)
                        .append(Component.text(ws.getParsedLocation(ws.getLocation()).getBlockX()+", "+ ws.getParsedLocation(ws.getLocation()).getBlockY()+", "+ws.getParsedLocation(ws.getLocation()).getBlockZ(), NamedTextColor.LIGHT_PURPLE));
                Component owner = Component.text("Owner: ", NamedTextColor.DARK_PURPLE)
                        .append(Component.text(user.getUserName(), NamedTextColor.LIGHT_PURPLE));
                Component rating = Component.text("Rating: ",NamedTextColor.DARK_PURPLE)
                        .append(Component.text(ws.getRating(), NamedTextColor.LIGHT_PURPLE)
                                .append(Component.text("/",NamedTextColor.DARK_PURPLE))
                                .append(Component.text("5",NamedTextColor.LIGHT_PURPLE)));
                Component costComp = Component.text("Cost: ",NamedTextColor.DARK_PURPLE)
                        .append(Component.text(econ.format(ws.getCost()),NamedTextColor.AQUA));
                Component blank = Component.text("");

                Component lClick;
                if(cost>0 && !playerMenuUtility.getUser().getUuid().equals(ws.getOwner())){
                    lClick = Component.text("L-Click: ", NamedTextColor.DARK_GRAY)
                            .append(Component.text("Pay & Teleport", NamedTextColor.GRAY));
                }else{
                    lClick = Component.text("L-Click: ", NamedTextColor.DARK_GRAY)
                            .append(Component.text("Teleport", NamedTextColor.GRAY));
                }

                Component rClick;
                if(ws.getOwner().equals(playerMenuUtility.getOwnerUUID().toString()) || playerMenuUtility.isAdmin()){
                    rClick = Component.text("R-Click: ", NamedTextColor.DARK_GRAY)
                            .append(Component.text("Edit", NamedTextColor.GRAY));
                }else{
                    rClick = Component.text("R-Click: ", NamedTextColor.DARK_GRAY)
                            .append(Component.text("Rate", NamedTextColor.GRAY));

                }

                loreArray.add(location);
                loreArray.add(owner);
                loreArray.add(rating);
                if(cost>0){
                    loreArray.add(costComp);
                }
                loreArray.add(blank);
                loreArray.add(lClick);
                loreArray.add(rClick);

                publicMeta.lore(loreArray);




                //Stores the index of the waystone from the waystones list into the NBT meta of that file so that it can be identified when clicked.
                publicMeta.getPersistentDataContainer().set(new NamespacedKey(WaystonesPlugin.getPlugin(), "index"), PersistentDataType.INTEGER, indexPubWs);

                //Updates the meta with the provided one
                publicWs.setItemMeta(publicMeta);

                //Add the item to the inventory
                int pos = PUBLIC_WS_SLOTS.get(i);
                inventory.setItem(pos, publicWs);

            }

        }

        addCompass(playerMenuUtility);
        addMenuPageButtons(publicWaystones.size());
    }

    private void addMenuPageButtons(int pubWsSize){
        if(prevIndexWs + 1 >= MAX_PRIVATE * (page+1) || pubWsSize > MAX_PUBLIC * (page +1)){
            inventory.setItem(50, makeItem(Material.ARROW, "Next Page"));
        }
        if(page != 0){
            inventory.setItem(48, makeItem(Material.BARRIER, "Back"));
        }

    }

}
