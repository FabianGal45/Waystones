package eu.ovmc.waystones.commands;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.menusystem.ChatInputHandler;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.menusystem.menu.WaystonesSplitMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Bukkit.getServer;

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
                    player.sendMessage(Component.text("Something went wrong when purchasing a waystone", NamedTextColor.RED)); //This might never be triggered
                }
            }
            else if (args[0].equals("add")) {
                if(player.hasPermission("waystones.admin")){
                    if(args[1].equals("public")){
                        if(args.length>3){

                            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                            SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

                            if(target.isOnline()){
                                System.out.println("UUID Online " + target.getUniqueId());

                                PlayerMenuUtility playerMenuUtility = WaystonesPlugin.getPlayerMenuUtility(target.getPlayer());
                                User user = playerMenuUtility.getUser();

                                try{
                                    user.setAcquiredPublicWs(user.getAcquiredPublicWs() + Integer.parseInt(args[3]));
                                    jdbc.updateUser(user);
                                    player.sendMessage("Added "+ args[3] + " public waystones to " + target.getName());
                                }
                                catch (NumberFormatException e){
                                    player.sendMessage(args[3]+" has to be a number.");
                                }


                            }else{
                                User user = jdbc.getUserFromDB(target.getUniqueId().toString());
                                if(user != null){
                                    try{
                                        user.setAcquiredPublicWs(user.getAcquiredPublicWs() + Integer.parseInt(args[3]));
                                        jdbc.updateUser(user);
                                        player.sendMessage("Added "+ args[3] + " public waystones to " + target.getName());
                                    }
                                    catch (NumberFormatException e){
                                        player.sendMessage(args[3]+" has to be a number.");
                                    }


                                }
                                else{
                                    player.sendMessage("This user does not exist in the database.");
                                }
                            }
                        }
                    }
                    else if(args[1].equals("private")){
                        if(args.length>3){

                            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                            SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

                            if(target.isOnline()){
                                System.out.println("UUID Online " + target.getUniqueId());

                                PlayerMenuUtility playerMenuUtility = WaystonesPlugin.getPlayerMenuUtility(target.getPlayer());
                                User user = playerMenuUtility.getUser();

                                if(user !=null){//if the user is registered on the database
                                    try{
                                        user.setAcquiredPrivateWs(user.getAcquiredPrivateWs() + Integer.parseInt(args[3]));
                                        jdbc.updateUser(user);
                                        player.sendMessage("Added "+ args[3] + " private waystones to " + target.getName());
                                    }
                                    catch (NumberFormatException e){
                                        player.sendMessage(args[3]+" has to be a number.");
                                    }

                                }else{
                                    player.sendMessage("This user does not exist in the database.");
                                }


                            }else{
                                User user = jdbc.getUserFromDB(target.getUniqueId().toString());
                                if(user != null){
                                    try{
                                        user.setAcquiredPrivateWs(user.getAcquiredPrivateWs() + Integer.parseInt(args[3]));
                                        jdbc.updateUser(user);
                                        player.sendMessage("Added "+ args[3] + " private waystones to " + target.getName());
                                    }
                                    catch (NumberFormatException e){
                                        player.sendMessage(args[3]+" has to be a number.");
                                    }
                                }
                                else{
                                    player.sendMessage("This user does not exist in the database.");
                                }
                            }
                        }
                    }
                    else{
                        player.sendMessage(Component.text("private or public?", NamedTextColor.DARK_RED));
                    }
                }else{
                    player.sendMessage(Component.text("You do not have permission.", NamedTextColor.DARK_RED));
                }
            }
            else if (args[0].equals("open")){
                if(player.hasPermission("waystones.admin")){

                    SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    User user = jdbc.getUserFromDB(target.getUniqueId().toString());

                    //if user exists in the database open a split menu as the player
                    if(user != null){
                        PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(target);
                        playerMenuUtility.setPrivateWaystones(jdbc.getAllPrivateWaystones(target.getUniqueId().toString()));
                        playerMenuUtility.setPublicWaystones(jdbc.getAllPublicWaystones());

                        new WaystonesSplitMenu(playerMenuUtility, 0).openAs(player);

                    }else{
                        player.sendMessage("This user does not exist in the database.");
                    }
                }
                else{
                    player.sendMessage(Component.text("You do not have permission.", NamedTextColor.DARK_RED));
                }
            }
            else if (args[0].equals("cancelNameChange")){
                ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();
                if(chatInputHandler.getRenameMap().containsKey(player)){
                    chatInputHandler.removePlayerFromRenameMap(player);
                }
            }
            else if (args[0].equals("confirmWsRemoval")){
                ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();
                if(chatInputHandler.getRemoveMap().containsKey(player)){
                    chatInputHandler.handleRemove(player);
                }
            }
            else if(args[0].equals("cancelWsRemoval")){
                ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();
                if(chatInputHandler.getRemoveMap().containsKey(player)){
                    chatInputHandler.removePlayerFromRemoveMap(player);
                }
            }
            else if(args[0].equals("cancelCostChange")){
                ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();
                if(chatInputHandler.getCostMap().containsKey(player)){
                    chatInputHandler.removePlayerFromCostMap(player);
                }
            }
            else{
                player.sendMessage(Component.text("This command does not exist", NamedTextColor.DARK_RED));
            }


        }
        else{
            ConsoleCommandSender console = getServer().getConsoleSender();
            if (args[0].equals("set")) {
                if (args[1].equals("public")) {
                    if (args.length > 3) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

                        if (target.isOnline()) {
                            System.out.println("UUID Online " + target.getUniqueId());

                            PlayerMenuUtility playerMenuUtility = WaystonesPlugin.getPlayerMenuUtility(target.getPlayer());
                            User user = playerMenuUtility.getUser();

                            if(user !=null){
                                try {
                                    user.setAcquiredPublicWs(Integer.parseInt(args[3]));
                                    jdbc.updateUser(user);
                                    console.sendMessage("Set " + args[3] + " public waystones to " + target.getName());
                                } catch (NumberFormatException e) {
                                    console.sendMessage(args[3] + " has to be a number.");
                                }
                            } else{
                                console.sendMessage("This user does not exist in the database.");
                            }

                        } else {
                            User user = jdbc.getUserFromDB(target.getUniqueId().toString());
                            if (user != null) {
                                try {
                                    user.setAcquiredPublicWs(Integer.parseInt(args[3]));
                                    jdbc.updateUser(user);
                                    console.sendMessage("Set " + args[3] + " public waystones to " + target.getName());
                                } catch (NumberFormatException e) {
                                    console.sendMessage(args[3] + " has to be a number.");
                                }


                            } else {
                                console.sendMessage("This user does not exist in the database.");
                            }
                        }
                    }
                } else if (args[1].equals("private")) {
                    if (args.length > 3) {

                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

                        if (target.isOnline()) {
                            System.out.println("UUID Online " + target.getUniqueId());

                            PlayerMenuUtility playerMenuUtility = WaystonesPlugin.getPlayerMenuUtility(target.getPlayer());
                            User user = playerMenuUtility.getUser();

                            try {
                                user.setAcquiredPrivateWs(Integer.parseInt(args[3]));
                                jdbc.updateUser(user);
                                console.sendMessage("Set " + args[3] + " private waystones to " + target.getName());
                            } catch (NumberFormatException e) {
                                console.sendMessage(args[3] + " has to be a number.");
                            }


                        } else {
                            User user = jdbc.getUserFromDB(target.getUniqueId().toString());
                            if (user != null) {
                                try {
                                    user.setAcquiredPrivateWs(Integer.parseInt(args[3]));
                                    jdbc.updateUser(user);
                                    console.sendMessage("Set " + args[3] + " private waystones to " + target.getName());
                                } catch (NumberFormatException e) {
                                    console.sendMessage(args[3] + " has to be a number.");
                                }


                            } else {
                                console.sendMessage("This user does not exist in the database.");
                            }
                        }
                    }
                } else {
                    console.sendMessage(Component.text("private or public?", NamedTextColor.DARK_RED));
                }
            }
            else if (args[0].equals("add")) {
                if(args[1].equals("public")){
                    if(args.length>3){

                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

                        if(target.isOnline()){
                            System.out.println("UUID Online " + target.getUniqueId());

                            PlayerMenuUtility playerMenuUtility = WaystonesPlugin.getPlayerMenuUtility(target.getPlayer());
                            User user = playerMenuUtility.getUser();

                            if(user != null){
                                try{
                                    user.setAcquiredPublicWs(user.getAcquiredPublicWs() + Integer.parseInt(args[3]));
                                    jdbc.updateUser(user);
                                    console.sendMessage("Added "+ args[3] + " public waystones to " + target.getName());
                                }
                                catch (NumberFormatException e){
                                    console.sendMessage(args[3]+" has to be a number.");
                                }
                            }
                            else{
                                console.sendMessage("This user does not exist in the database.");
                            }


                        }else{
                            User user = jdbc.getUserFromDB(target.getUniqueId().toString());
                            if(user != null){
                                try{
                                    user.setAcquiredPublicWs(user.getAcquiredPublicWs() + Integer.parseInt(args[3]));
                                    jdbc.updateUser(user);
                                    console.sendMessage("Added "+ args[3] + " public waystones to " + target.getName());
                                }
                                catch (NumberFormatException e){
                                    console.sendMessage(args[3]+" has to be a number.");
                                }


                            }
                            else{
                                console.sendMessage("This user does not exist in the database.");
                            }
                        }
                    }
                }
                else if(args[1].equals("private")){
                    if(args.length>3){

                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

                        if(target.isOnline()){
                            System.out.println("UUID Online " + target.getUniqueId());

                            PlayerMenuUtility playerMenuUtility = WaystonesPlugin.getPlayerMenuUtility(target.getPlayer());
                            User user = playerMenuUtility.getUser();

                            if(user != null){
                                try{
                                    user.setAcquiredPrivateWs(user.getAcquiredPrivateWs() + Integer.parseInt(args[3]));
                                    jdbc.updateUser(user);
                                    console.sendMessage("Added "+ args[3] + " private waystones to " + target.getName());
                                }
                                catch (NumberFormatException e){
                                    console.sendMessage(args[3]+" has to be a number.");
                                }
                            }
                            else{
                                console.sendMessage("This user does not exist in the database.");
                            }


                        }else{
                            User user = jdbc.getUserFromDB(target.getUniqueId().toString());
                            if(user != null){
                                try{
                                    user.setAcquiredPrivateWs(user.getAcquiredPrivateWs() + Integer.parseInt(args[3]));
                                    jdbc.updateUser(user);
                                    console.sendMessage("Added "+ args[3] + " private waystones to " + target.getName());
                                }
                                catch (NumberFormatException e){
                                    console.sendMessage(args[3]+" has to be a number.");
                                }


                            }
                            else{
                                console.sendMessage("This user does not exist in the database.");
                            }
                        }
                    }
                }
                else{
                    console.sendMessage(Component.text("private or public?", NamedTextColor.DARK_RED));
                }
            }
            else{
                console.sendMessage(Component.text("This command does not exist", NamedTextColor.DARK_RED));
            }
        }


        return false;
    }
}
