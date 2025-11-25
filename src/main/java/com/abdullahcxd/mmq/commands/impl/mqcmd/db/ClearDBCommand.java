package com.abdullahcxd.mmq.commands.impl.mqcmd.db;

import com.abdullahcxd.mmq.commands.MQCommand;
import com.abdullahcxd.mmq.commands.MQCommandInfo;
import com.abdullahcxd.mmq.confirmation.Confirmation;
import com.abdullahcxd.mmq.database.Databases;
import com.abdullahcxd.mmq.utils.LoggingUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ClearDBCommand extends MQCommand {

    // don't convert this to a field in the method, it'll be created every single command dispatch
    private final String confirmationId = "CLEAR_DB_CONFIRMATION";

    @Override
    public MQCommandInfo getCommandInfo() {
        return MQCommandInfo.builder()
                .name("clear")
                .description("Clears the whole database")
                .permission("marmalade.commands.cleardb")
                .usage("/marmalade database clear")
                .build();
    }

    @Override
    public boolean onExecute(CommandSender sender, Command command, List<String> args) {

        if (!Confirmation.willConfirmOnNext(confirmationId)) {

            LoggingUtility.send(sender, "&e&lWarning!");
            LoggingUtility.send(sender, "&e&lYou'll lose all data contained in the database");
            LoggingUtility.send(sender, "&e&lMake sure to backup everything necessary before running this command!");
            LoggingUtility.send(sender, "");
            LoggingUtility.send(sender, "&e&lTo confirm, run this command again!");

            Confirmation.createNew(confirmationId);

        } else {

            Databases.clearAll();
            LoggingUtility.send(sender, "&a&lCleared the databases successfully.");

            Confirmation.removeConfirmation(confirmationId);

        }

        return true;
    }
}
