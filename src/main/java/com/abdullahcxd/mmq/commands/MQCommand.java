package com.abdullahcxd.mmq.commands;

import com.abdullahcxd.mmq.utils.LoggingUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MQCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (getCommandInfo().isAsPlayer() && !(sender instanceof Player)) {
            LoggingUtility.send(sender, "&c&lYou must be a player to use this command.");
            return false;
        }

        if (!sender.hasPermission("mmq.commands." + getCommandInfo().getPermission())) {
            LoggingUtility.send(sender, "&c&lYou do not have permission to use this command.");
            return false;
        }

        if (args.length < getCommandInfo().getMinArgs()) {
            LoggingUtility.send(sender, "&c&lNot enough arguments: " + getCommandInfo().getUsage());
            return false;
        }

        return onExecute(sender, command, new ArrayList<String>(Arrays.asList(args)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return tabComplete(sender, command, new ArrayList<>(Arrays.asList(args)));
    }

    public List<String> tabComplete(CommandSender sender, Command command, List<String> args) {
        return List.of();
    }

    public abstract MQCommandInfo getCommandInfo();

    public abstract boolean onExecute(CommandSender sender, Command command, List<String> args);

}
