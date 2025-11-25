package com.abdullahcxd.mmq.commands.impl.mqcmd.db;

import com.abdullahcxd.mmq.commands.MQCommandInfo;
import com.abdullahcxd.mmq.commands.MQSubcommand;

public class DatabaseCommand extends MQSubcommand {

    public DatabaseCommand() {
        super();
        this.registerSubcommand(new ClearDBCommand());
    }

    @Override
    public MQCommandInfo getCommandInfo() {
        return MQCommandInfo.builder()
                .name("database")
                .description("Database management")
                .permission("marmalade.commands.database")
                .usage("/marmalade database <subcommand> [...args]")
                .minArgs(1)
                .build();
    }
}
