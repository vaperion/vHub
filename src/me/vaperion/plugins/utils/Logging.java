package me.vaperion.plugins.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logging {

    public static void info(String message) {
        info(true, message);
    }

    public static void info(boolean showPrefix, String message) {
        Bukkit.getConsoleSender().sendMessage((showPrefix ? "§8[§a§lvHub§8] §a" : "§a") + ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void error(String message) {
        error(true, message);
    }

    public static void error(boolean showPrefix, String message) {
        Bukkit.getConsoleSender().sendMessage((showPrefix ? "§8[§c§lvHub§8] §c" : "§c") + ChatColor.translateAlternateColorCodes('&', message));
    }

}
