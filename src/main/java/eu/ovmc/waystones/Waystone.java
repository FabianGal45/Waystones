package eu.ovmc.waystones;
import org.bukkit.Location;

import java.util.UUID;

public class Waystone {
    private Location location;
    private UUID owner;

    public Waystone(Location location, UUID owner) {
        this.location = location;
        this.owner = owner;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}
