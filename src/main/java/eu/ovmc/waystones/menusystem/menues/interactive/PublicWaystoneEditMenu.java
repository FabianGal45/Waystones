package eu.ovmc.waystones.menusystem.menues.interactive;

import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PublicWaystoneEditMenu extends EditMenu {
    public PublicWaystoneEditMenu(PlayerMenuUtility playerMenuUtility, PrivateWaystone selected) {
        super(playerMenuUtility, selected);
    }

    @Override
    public void setMenuItems(){
        fillAllWithBlack();

        ItemStack nameTag = new ItemStack(Material.NAME_TAG);
        ItemMeta nameTagMeta = nameTag.getItemMeta();
        TextComponent ntName = Component.text("Rename");
        nameTagMeta.displayName(ntName);
        nameTag.setItemMeta(nameTagMeta);
        inventory.setItem(10, nameTag);

        ItemStack diamond = new ItemStack(Material.DIAMOND);
        ItemMeta dMeta = diamond.getItemMeta();
        TextComponent dName = Component.text("Cost");
        dMeta.displayName(dName);
        List<Component> loreArray = new ArrayList<>();
        loreArray.add(Component.text("Cost: "+ ((PublicWaystone)selected).getCost() + " Diamonds"));
        dMeta.lore(loreArray);
        diamond.setItemMeta(dMeta);
        inventory.setItem(12, diamond);

        ItemStack redstpmeBlock = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta rbMeta = redstpmeBlock.getItemMeta();
        TextComponent rbName = Component.text("Remove");
        rbMeta.displayName(rbName);
        redstpmeBlock.setItemMeta(rbMeta);
        inventory.setItem(14, redstpmeBlock);

        ItemStack netheriteBlock = new ItemStack(Material.NETHERITE_BLOCK);
        ItemMeta nbMeta = netheriteBlock.getItemMeta();
        TextComponent nbName = Component.text("Category");
        nbMeta.displayName(nbName);
        netheriteBlock.setItemMeta(nbMeta);
        inventory.setItem(16, netheriteBlock);
    }
}
