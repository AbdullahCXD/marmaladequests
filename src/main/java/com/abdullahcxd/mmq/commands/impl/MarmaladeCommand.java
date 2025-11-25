package com.abdullahcxd.mmq.commands.impl;

import com.abdullahcxd.mmq.commands.MQCommandInfo;
import com.abdullahcxd.mmq.commands.MQSubcommand;
import com.abdullahcxd.mmq.commands.impl.mqcmd.ReloadCommand;
import com.abdullahcxd.mmq.commands.impl.mqcmd.db.DatabaseCommand;

public class MarmaladeCommand extends MQSubcommand {

    public MarmaladeCommand() {
        super();
        this
                .registerSubcommand(new DatabaseCommand())
                .registerSubcommand(new ReloadCommand());
    }

    @Override
    public MQCommandInfo getCommandInfo() {
        return MQCommandInfo.builder()
                .name("marmalade")
                .description("The main command for the MarmaladeQuests Plugin")
                .permission("marmalade.commands.*")
                .usage("/marmalade <subcommand> [...args]")
                .minArgs(1)
                .build();
    }
}
