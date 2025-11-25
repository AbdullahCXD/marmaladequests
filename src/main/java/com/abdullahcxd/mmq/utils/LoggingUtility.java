package com.abdullahcxd.mmq.utils;

import com.github.lalyos.jfiglet.FigletFont;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LoggingUtility {

    public static final String PREFIX = "&6Marmalade&6&lQuests";

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(colorize(PREFIX + " " + message));
    }

    public static void send(String message) {
        send(Bukkit.getConsoleSender(), message);
    }

    public static String separator(int length) {
        return "=".repeat(length);
    }

    public static void plugin(String message) {
        send(message);
    }

    public static String indent(int length) {
        return " ".repeat(length);
    }

    @SneakyThrows
    public static void figlet(String text, char color) {
        String all = FigletFont.convertOneLine(text);
        for (String line : all.split("\n")) {
            plugin(indent(3) + "&" + color + line);
        }
    }

}
