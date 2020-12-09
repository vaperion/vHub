package me.vaperion.plugins.rank;

import lombok.Getter;
import me.vaperion.plugins.Hub;
import me.vaperion.plugins.utils.Logging;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Getter
public class RankHandler {
    private HashMap<String, Rank> ranks;

    public RankHandler() {
        this.ranks = new HashMap<>();
        loadRanks();
    }

    public void loadRanks() {
        FileConfiguration config = Hub.getInstance().getSettingsConfig().getConfig();
        try {
            config.getConfigurationSection("permissions.ranks").getKeys(false).forEach(key -> {
                String path = "permissions.ranks." + key + ".";
                Rank rank = new Rank.Builder(key)
                    .setColor(config.getString(path + "color"))
                    .setColoredDisplay(config.getString(path + "coloredDisplay"))
                    .setDisplay(config.getString(path + "display"))
                    .setPriority(config.getInt(path + "priority"))
                    .build();
                this.ranks.put(key, rank);
            });
            Logging.info(true, "Successfully loaded " + ranks.size() + " ranks!");
        } catch (Exception e) {
            Logging.error(true, "Failed to load the ranks from the config.");
            Logging.error(true, "Error: " + e.getLocalizedMessage());
        }
    }

    public Rank getRankForPlayer(Player player) {
        return ranks.getOrDefault(Hub.getInstance().getRankProvider().getRank(player), new Rank("default"));
    }
}
