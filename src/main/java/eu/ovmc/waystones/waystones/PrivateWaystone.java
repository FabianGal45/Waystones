package eu.ovmc.waystones.waystones;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
            loc = loc.set((int)loc.getX()+0.5, loc.getY(), (int)loc.getZ()+0.5);
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
            player.teleport(loc);
        }
        else{
            player.sendMessage("This teleportation is unsafe!");
        }

    }

    private boolean isSpaceAbove(Location loc){
        Material above1 = loc.add(0.0,1.0,0.0).getBlock().getType();
        Material above2 = loc.add(0.0,2.0,0.0).getBlock().getType();

        return above1.equals(Material.AIR) && above2.equals(Material.AIR);
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
