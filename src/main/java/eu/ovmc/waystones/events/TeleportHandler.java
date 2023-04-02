package eu.ovmc.waystones.events;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TeleportHandler { //This class is meant to keep all teleportation related methods in one place to keep it organized

    public static void safeTeleport(Player player, PlayerMenuUtility playerMenuUtility, Location tpLocation){
        Location loc = TeleportHandler.getSafeLocation(tpLocation);
        System.out.println("SafeTeleport loc: "+loc);
        if (loc != null) {//If the place is safe to teleport
            checkTpMaterialCost(player, loc, playerMenuUtility);
        }else{
            player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
            playerMenuUtility.setNextTpLocation(tpLocation);
            WaystonesPlugin.getPlugin().getChatInputHandler().addToUnsafeTPMap(player,playerMenuUtility);
            player.sendMessage(Component.text("This teleportation is unsafe!", NamedTextColor.RED)
                    .append(Component.text(" [✔]", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Accept and teleport anyways")))
                            .clickEvent(ClickEvent.runCommand("/ws acceptUnsafeTP")))
                    .append(Component.text(" [X]", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Cancel")))
                            .clickEvent(ClickEvent.runCommand("/ws cancelUnsafeTP"))));
        }

    }

    public static void unsafeTeleport(Player player, Location tpLocation){
        System.out.println("Unsafe teleport: " + tpLocation);
        System.out.println("player Death: "+ player.getLastDeathLocation());
        PlayerMenuUtility playerMenuUtility =  WaystonesPlugin.getPlayerMenuUtility(player);
        checkTpMaterialCost(player, tpLocation, playerMenuUtility);
    }

    private static void checkTpMaterialCost(Player player, Location tpLocation, PlayerMenuUtility playerMenuUtility) {
        if(playerMenuUtility.getTpCostMaterial() != null) {//if the player has to pay an item in order to teleport
            Inventory pInventory = player.getInventory();
            Material tpCostMaterial = playerMenuUtility.getTpCostMaterial();
            playerMenuUtility.setTpCostMaterial(null);

            if (pInventory.contains(tpCostMaterial)) {// if the player has an Echo Shard
                pInventory.removeItem(new ItemStack(tpCostMaterial, 1));
                System.out.println("CheckTpMaterialCost tplocation: "+tpLocation);
                teleport(player,tpLocation);
            } else {
                player.sendMessage(Component.text("You need an ", NamedTextColor.DARK_RED)
                        .append(Component.text(tpCostMaterial.name(), NamedTextColor.RED)
                                .append(Component.text(" to teleport.", NamedTextColor.DARK_RED))));
            }
        }
        else{
            teleport(player,tpLocation);
        }
    }

    private static void teleport(Player player, Location tpLocation){
        player.teleportAsync(tpLocation);
        player.playSound(tpLocation, Sound.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1,1);
    }

    public static Location getParsedLocation(String s) {
        Location loc = null;

        // https://www.spigotmc.org/threads/get-locations-pitch-and-yaw.528194/
        String[] parts = s.split(",");
        if (parts.length == 6) {
            World w = Bukkit.getServer().getWorld(parts[0].replace("world=CraftWorld{", "").replace("}", "").replace("Location{name=", ""));
            double x = Double.parseDouble(parts[1].replace("x=", ""));
            double y = Double.parseDouble(parts[2].replace("y=", ""));
            double z = Double.parseDouble(parts[3].replace("z=", ""));
            float pitch = (int) Double.parseDouble(parts[4].replace("pitch=", ""));
            float yaw = (int) Double.parseDouble(parts[5].replace("yaw=", "").replace("}", ""));
            loc = new Location(w, x, y, z);
            loc.setPitch(pitch);
            loc.setYaw(yaw);

        }
        return loc;
    }

    public static Location getSafeLocation(Location location){
        System.out.println("Original location : "+ location);
        Location loc = null;

        for(int i = 0; i<5; i++){
            Block landingBlock = location.subtract(0.0, (double) i, 0.0).getBlock();
            System.out.println("Landing block: "+ landingBlock.getLocation());

            //if there is a block to land on
            if(!landingBlock.getType().isAir()){
                //position the player once a landing block has been found
                loc = location;
                loc.set(centerCoordinate(location.getX()), location.getY()+1, centerCoordinate(location.getZ()));
                loc.setPitch(0);//makes the player look straight ahead
                System.out.println("X & Z: "+ location.getX() + " | "+ location.getZ());
                System.out.println("Centered loc "+ loc);

                //if the block is dangerous and there isn't enough space above for the player then it's unsafe
                if(!isSpaceAbove(landingBlock.getLocation())
                        || (landingBlock.getType().equals(Material.LAVA)
                        || landingBlock.getType().equals(Material.FIRE)
                        || landingBlock.getType().equals(Material.NETHER_PORTAL)
                        || landingBlock.getType().equals(Material.STONECUTTER))){//landing block is acting up it should not be the values it says
                    //UNSAFE
                    System.out.println("lava: "+landingBlock.getType().equals(Material.LAVA));
                    System.out.println("Fire: "+landingBlock.getType().equals(Material.FIRE));
                    System.out.println("Nether portal: "+landingBlock.getType().equals(Material.NETHER_PORTAL));
                    System.out.println("Stone cutter: "+landingBlock.getType().equals(Material.STONECUTTER));
                    loc = null;
                }
                break;
            }
        }
        return loc;
    }

    private static double centerCoordinate(double n){
        if(n < 0){
            n = (int)n - 0.5;
        }
        else{
            n = (int)n + 0.5;
        }
        return n;
    }

    private static boolean isSpaceAbove(Location loc){
        Material above1 = loc.add(0.0,1.0,0.0).getBlock().getType();
        Material above2 = loc.add(0.0,2.0,0.0).getBlock().getType();
        boolean spaceAbove = above1.isAir() && above2.isAir();

        System.out.println("Space above 1: "+ above1 +" | "+ loc.add(0.0,1.0,0.0));
        System.out.println("Space above 2: "+ above2 +" | "+ loc.add(0.0,2.0,0.0));
        System.out.println("Space Above: "+ spaceAbove);

        return spaceAbove;
    }

}
