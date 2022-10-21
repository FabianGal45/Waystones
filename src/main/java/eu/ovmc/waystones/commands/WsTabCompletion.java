package eu.ovmc.waystones.commands;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.database.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WsTabCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("waystones.admin")){
            if(args.length==1){
                List<String> arguments = new ArrayList<>();

                arguments.add("open");
                arguments.add("add");

                return arguments;
            }
            else if(args[0].equals("open") && args.length == 2 || args[0].equals("add") && args.length == 3){
                List<String> playerNames = new ArrayList<>();

                //adding the online players
//            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
//            Bukkit.getServer().getOnlinePlayers().toArray(players);
//            for(int i = 0; i < players.length; i++){
//                playerNames.add(players[i].getName());
//            }
                System.out.println("Arg 0: "+args[0]);

                //adding the users that are registered in the database
                SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
                ArrayList<User> userArrayList = jdbc.getAllUsersFromDB();
                for(User user : userArrayList){
                    playerNames.add(user.getUserName());
                }

                return playerNames;
            }
            else if(args[0].equals("add") && args.length == 2){
                List<String> arguments = new ArrayList<>();

                System.out.println("Arg 0: "+args[0]);
                arguments.add("private");
                arguments.add("public");

                return arguments;
            }
            else if(args[0].equals("add") && args.length==4){
                List<String> arguments = new ArrayList<>();

                arguments.add("1");
                arguments.add("2");
                arguments.add("5");

                return arguments;
            }
        }


        return null;
    }
}
