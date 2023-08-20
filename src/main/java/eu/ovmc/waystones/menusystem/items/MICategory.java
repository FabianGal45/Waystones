package eu.ovmc.waystones.menusystem.items;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.waystones.PubWsCategory;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class MICategory extends MenuItem {

    private PubWsCategory category;

    public MICategory(ItemType menuItemType, String name, PubWsCategory category) {
        super(category.getMaterial(), menuItemType, name);
        this.category = category;
        saveCategoryToNBT(category);
    }

}
