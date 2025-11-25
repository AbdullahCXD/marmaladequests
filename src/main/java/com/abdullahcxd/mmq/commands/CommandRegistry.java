package com.abdullahcxd.mmq.commands;

import com.abdullahcxd.mmq.MarmaladeQuests;
import com.abdullahcxd.mmq.api.MarmaladeAPI;
import org.bukkit.command.PluginCommand;

public class CommandRegistry {

    public static void registerCommand(MQCommand cmd) {
        MarmaladeQuests plugin = MarmaladeAPI.getPlugin();
        MQCommandInfo info = cmd.getCommandInfo();
        PluginCommand command = plugin.getCommand(info.getName());

        if (command == null) return;

        command.setExecutor(cmd);
        command.setTabCompleter(cmd);
        command.setDescription(info.getDescription());
        command.setPermission(info.getPermission());
        command.setUsage(info.getUsage());
    }

}
