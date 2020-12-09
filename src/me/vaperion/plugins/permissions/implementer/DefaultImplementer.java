package me.vaperion.plugins.permissions.implementer;

import me.vaperion.plugins.permissions.HubRankProvider;
import org.bukkit.entity.Player;

public class DefaultImplementer implements HubRankProvider {
    @Override
    public String getRank(Player player) {
        return "default";
    }
}
