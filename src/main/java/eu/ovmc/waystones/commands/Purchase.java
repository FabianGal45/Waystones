package eu.ovmc.waystones.commands;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Purchase implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;

            if(args.length==0){
                SQLiteJDBC jdbc = new SQLiteJDBC();
                Economy econ = WaystonesPlugin.getEcon();
                PlayerMenuUtility pmu = WaystonesPlugin.getPlayerMenuUtility(player);

                if(pmu != null){
                    User user = pmu.getUser();
                    int purchasedPrivateWs = user.getPurchasedPrivateWs();
                    int cost = user.getCostOfNextWs();
                    EconomyResponse r = econ.withdrawPlayer(player, cost);

                    if(r.transactionSuccess()){
                        purchasedPrivateWs++;
                        user.setPurchasedPrivateWs(purchasedPrivateWs);
                        jdbc.updateUser(user);

                        player.sendMessage(Component.text("You purchased a waystone for ", NamedTextColor.GREEN)
                                .append(Component.text( econ.format(cost), NamedTextColor.GREEN)));
                    }
                    else{
                        player.sendMessage(Component.text("You don't have ", NamedTextColor.DARK_RED)
                                .append(Component.text( econ.format(user.getCostOfNextWs()), NamedTextColor.RED)));
                    }
                }
                else{
                    player.sendMessage(Component.text("You have not placed any waystone. Try placing one", NamedTextColor.RED)); //Todo: Test this
                }


            }


        }


        return false;
    }
}
