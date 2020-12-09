package me.vaperion.plugins.permissions;

import org.bukkit.entity.Player;

public interface HubRankProvider {
    String getRank(Player player);
}
