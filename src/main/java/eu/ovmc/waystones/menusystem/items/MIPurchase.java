package eu.ovmc.waystones.menusystem.items;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import java.text.DecimalFormat;


public class MIPurchase extends MenuItem {

    public MIPurchase(Material displayItem, ItemType menuItemType, String name) {
        super(displayItem, menuItemType, name);
    }

    public MIPurchase(Material displayItem, ItemType menuItemType) {
        super(displayItem, menuItemType);
    }

    public void setLoreDescription(User user, PlayerMenuUtility playerMenuUtility){
        Economy econ = WaystonesPlugin.getEcon();
        DecimalFormat formatter = new DecimalFormat("#,###");
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

            loreDescription.add(displayWithDiscount);
        }
        else{
            loreDescription.add(Component.text("Cost: ", NamedTextColor.GRAY)
                    .append(Component.text(econ.format(user.getCostOfNextWs()), NamedTextColor.WHITE)));
        }
        loreDescription.add(Component.text(""));
        if(Bukkit.getServer().getPluginManager().getPlugin("VotingPlugin")!=null){ // if voting plugin has been installed.
            loreDescription.add(Component.text("1 Vote = 1 Point = 1% Discount", NamedTextColor.DARK_GRAY));
        }
        loreDescription.add(Component.text("Balance: ", NamedTextColor.DARK_GRAY).append(Component.text(econ.format(econ.getBalance(playerMenuUtility.getOwner())), NamedTextColor.DARK_GRAY)));
        displayItemMeta.lore(loreDescription);
        displayItem.setItemMeta(displayItemMeta);
    }


}
