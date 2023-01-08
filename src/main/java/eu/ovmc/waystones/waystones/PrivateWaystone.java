package eu.ovmc.waystones.waystones;

import com.google.gson.stream.JsonToken;
import eu.ovmc.waystones.WaystonesPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PrivateWaystone {
    private String location;
    private String owner;
    private String name;
    private String tpLocation;

    public PrivateWaystone() {
        location = null;
        owner = null;
        name = null;
        tpLocation = null;
    }

    public PrivateWaystone(String location, String owner, String name, String tpLocation) {
        this.location = location;
        this.owner = owner;
        this.name = name;
        this.tpLocation = tpLocation;
    }

    public String getLocation() {
        return location;
    }

    public Location getParsedLocation(String s) {
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

    public void safeTeleport(Player player){

        //Teleports player above the selected waystone
        Location loc = null;
        boolean safe = true;

        for(int i = 0; i<5; i++){
            safe = true;
            loc = getParsedLocation(tpLocation);
            //Centers the player on the block
            float yaw = loc.getYaw();
            float pitch = loc.getPitch();
            loc = loc.set(centerCoordinate(loc.getX()), loc.getY(), centerCoordinate(loc.getZ()));
            loc.setYaw(yaw);
            loc.setPitch(pitch-18);

            Block landingBlock = loc.getBlock().getLocation().subtract(0.0, (double) i, 0.0).getBlock();

            //if there is a block to land on
            if(!landingBlock.getType().equals(Material.AIR)){
                //position the player once a landing block has been found
                loc.set(landingBlock.getX()+0.5, landingBlock.getY()+1, landingBlock.getZ()+0.5);

                //if the block is dangerous and there isn't enough space above for the player then it's unsafe
                if((landingBlock.getType().equals(Material.LAVA)
                        || landingBlock.getType().equals(Material.FIRE)
                        || landingBlock.getType().equals(Material.NETHER_PORTAL)
                        || landingBlock.getType().equals(Material.STONECUTTER)) || !isSpaceAbove(landingBlock.getLocation())){
                    //check if there is space above lodestone
                    if(isSpaceAbove(getParsedLocation(location))){
                        //set location above the lodestone
                        loc = getParsedLocation(location).add(0.5, 1.0 ,0.5);
                    }
                    else {
                        //warn player it is unsafe and they cannot teleport there.
                        safe = false;
                    }
                }
                break;
            }
            else{
                //check if there is space above lodestone
                if(isSpaceAbove(getParsedLocation(location))){
                    //set location above the lodestone
                    loc = getParsedLocation(location).add(0.5, 1.0 ,0.5);
                }
                else {
                    //warn player it is unsafe and they cannot teleport there.
                    safe = false;
                }
            }
        }

        if(safe){
            player.teleportAsync(loc);
            player.playSound(loc, Sound.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1,1);
        }
        else{
            player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
            player.sendMessage("This teleportation is unsafe!");
        }

    }

    private double centerCoordinate(double n){
        if(n < 0){
            n = (int)n - 0.5;
        }
        else{
            n = (int)n + 0.5;
        }
        return n;
    }


    private boolean isSpaceAbove(Location loc){
        Material above1 = loc.add(0.0,1.0,0.0).getBlock().getType();
        Material above2 = loc.add(0.0,2.0,0.0).getBlock().getType();
        boolean spaceAbove = above1.equals(Material.AIR) && above2.equals(Material.AIR);

        return spaceAbove;
    }

    public String getTpLocation() {
        return tpLocation;
    }

    public void setTpLocation(String tpLocation) {
        this.tpLocation = tpLocation;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
