package com.abdullahcxd.mmq.commands;

import com.abdullahcxd.mmq.utils.LoggingUtility;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class MQSubcommand extends MQCommand {

    @Getter
    private final List<MQCommand> subcommands = new ArrayList<>();

    public MQSubcommand registerSubcommand(MQCommand subcommand) {
        subcommands.add(subcommand);
        return this;
    }

    public MQCommand searchSubcommand(String name) {
        for (MQCommand subcommand : subcommands) {
            if (subcommand.getCommandInfo().getName().equalsIgnoreCase(name)) {
                return subcommand;
            }
        }

        return null;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, List<String> args) {

        if (args.isEmpty()) {
            return super.tabComplete(sender, command, args);
        }

        String subcommandName = args.removeFirst();
        MQCommand cmd = this.searchSubcommand(subcommandName);

        if (cmd == null) {
            return super.tabComplete(sender, command, args);
        }

        return cmd.tabComplete(sender, command, args);

    }

    @Override
    public boolean onExecute(CommandSender sender, Command command, List<String> args) {

        if (args.isEmpty()) {
            this.showHelp(sender);
            return false;
        }

        String subcommandName = args.removeFirst();
        MQCommand cmd = this.searchSubcommand(subcommandName);

        if (cmd == null) {
            LoggingUtility.send(sender, "&c&lSubcommand wasn't found: " + subcommandName);
            return false;
        }

        return cmd.onExecute(sender, command, args);

    }

    public void showHelp(CommandSender sender) {
        LoggingUtility.send(sender, "&7&m-----------------&r &6" + getCommandInfo().getName().toUpperCase() + " HELP &7&m-----------------");
        LoggingUtility.send(sender, "");

        if (subcommands.isEmpty()) {
            LoggingUtility.send(sender, "&eNo subcommands available.");
        } else {
            for (MQCommand subcommand : subcommands) {
                MQCommandInfo info = subcommand.getCommandInfo();
                String usage = info.getUsage() != null ? info.getUsage() : info.getName();
                String description = info.getDescription() != null ? info.getDescription() : "No description";

                LoggingUtility.send(sender, "&e/" + usage + " &7- &f" + description);
            }
        }

        LoggingUtility.send(sender, "");
        LoggingUtility.send(sender, "&7&m------------------------------------------------");
    }
}
