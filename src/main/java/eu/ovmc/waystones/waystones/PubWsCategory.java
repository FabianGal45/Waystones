package eu.ovmc.waystones.waystones;

import org.bukkit.Material;

public enum PubWsCategory {
    STAFF(Material.AMETHYST_BLOCK),
    PLAYER_HOME(Material.OAK_WOOD),
    MOB_FARM(Material.MAGMA_BLOCK),
    SHOP(Material.RAW_GOLD_BLOCK),
    WAYPOINT(Material.SEA_LANTERN), //For places like End Nether or overworld you want to share
    PVP(Material.CRIMSON_HYPHAE),
    FUN(Material.NOTE_BLOCK),
    DEFAULT(Material.NETHERITE_BLOCK),
    EVENT(Material.FLETCHING_TABLE),
    BROKEN(Material.CRACKED_STONE_BRICKS),
    SELECTED(Material.ENDER_EYE);

    private final Material material;

    PubWsCategory(Material material){
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }
}
