package me.vaperion.plugins.utils;

import me.vaperion.plugins.Hub;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {

    public static String transformLine(Player player, String line) {
        line = line.replace("%global%", String.valueOf(CountCache.GLOBAL_COUNT));

        line = line.replace("%name%", player.getName());
        line = line.replace("%colored_name%", Hub.getInstance().getRankHandler().getRankForPlayer(player).getColor() + player.getName());
        line = line.replace("%rank%", Hub.getInstance().getRankHandler().getRankForPlayer(player).getDisplay());
        line = line.replace("%rank_color%", Hub.getInstance().getRankHandler().getRankForPlayer(player).getColor());
        line = line.replace("%colored_rank%", Hub.getInstance().getRankHandler().getRankForPlayer(player).getColoredDisplay());

        line = line.replace("%website%", Configuration.settings.websiteLink);
        line = line.replace("%store%", Configuration.settings.storeLink);
        return line;
    }

    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
