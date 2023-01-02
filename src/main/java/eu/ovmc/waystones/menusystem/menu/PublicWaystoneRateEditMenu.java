package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PublicWaystoneRateEditMenu extends EditMenu {
    public PublicWaystoneRateEditMenu(PlayerMenuUtility playerMenuUtility, PrivateWaystone selected) {
        super(playerMenuUtility, selected);
    }

    public Component getMenuName() {
        return Component.text("Rate: " + selected.getName());
    }

    @Override
    public void setMenuItems() {
        fillWithBlack();
        for(int i=1;i<=5;i++){
            ItemStack star = new ItemStack(Material.NETHER_STAR);
            ItemMeta sMeta = star.getItemMeta();
            TextComponent sName;
            if(i==1){
                sName = Component.text(i +" Star");
            }
            else {
                sName = Component.text(i +" Stars");
            }
            sMeta.displayName(sName);
            sMeta.getPersistentDataContainer().set(new NamespacedKey(WaystonesPlugin.getPlugin(), "index"), PersistentDataType.INTEGER, i);
            star.setItemMeta(sMeta);
            inventory.setItem((10 + i), star);
        }
    }

}
