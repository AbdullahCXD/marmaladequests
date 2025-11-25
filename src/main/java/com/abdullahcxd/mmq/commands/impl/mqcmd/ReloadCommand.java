package com.abdullahcxd.mmq.commands.impl.mqcmd;

import com.abdullahcxd.mmq.api.MarmaladeAPI;
import com.abdullahcxd.mmq.commands.MQCommand;
import com.abdullahcxd.mmq.commands.MQCommandInfo;
import com.abdullahcxd.mmq.utils.LoggingUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends MQCommand {
    @Override
    public MQCommandInfo getCommandInfo() {
        return MQCommandInfo.builder()
                .name("reload")
                .description("Reloads the plugin configurations")
                .permission("marmalade.commands.reload")
                .usage("/marmalade reload")
                .build();
    }

    @Override
    public boolean onExecute(CommandSender sender, Command command, List<String> args) {

        MarmaladeAPI.reload();

        LoggingUtility.send(sender, "&aReloaded configurations successfully!");

        return true;
    }
}
