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

public class Ws implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;



            if(args.length==0){
                WaystonesPlugin plugin = WaystonesPlugin.getPlugin();
                player.sendMessage("Custom plugin made for OVMC. "+ plugin.getDescription().getVersion());
            }
            else if (args[0].equals("purchase")) {
                PlayerMenuUtility playerMenuUtility = WaystonesPlugin.getPlayerMenuUtility(player);
                if(playerMenuUtility != null){
                    User user = playerMenuUtility.getUser();
                    user.purchaseWaystone(playerMenuUtility);
                }
                else{
                    player.sendMessage(Component.text("Something went wrong when purchasing a waystone", NamedTextColor.RED)); //Todo: Test this
                }
            }
            else{
                player.sendMessage(Component.text("This command does not exist", NamedTextColor.DARK_RED));
            }


        }


        return false;
    }
}
