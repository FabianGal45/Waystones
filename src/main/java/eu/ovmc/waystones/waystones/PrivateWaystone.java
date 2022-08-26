package eu.ovmc.waystones.waystones;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class PrivateWaystone {
    private String location;
    private String owner;

    private String name;

    public PrivateWaystone() {
        location = null;
        owner = null;
        name = null;
    }

    public PrivateWaystone(String location, String owner, String name) {
        this.location = location;
        this.owner = owner;
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public Location getParsedLocation() {
        Location loc = null;

        // https://www.spigotmc.org/threads/get-locations-pitch-and-yaw.528194/
        String s = location;
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
