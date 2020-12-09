package me.vaperion.plugins.utils.config.configs;

import me.vaperion.plugins.Hub;
import me.vaperion.plugins.utils.Configuration;
import me.vaperion.plugins.utils.Variable;
import me.vaperion.plugins.utils.config.ConfPath;
import me.vaperion.plugins.utils.config.LoadAfterConfLoad;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {
    @ConfPath(path = "settings.server-id", confName = "config")
    public String serverID = "";
    @ConfPath(path = "settings.website", confName = "config")
    public String websiteLink = "";
    @ConfPath(path = "settings.store", confName = "config")
    public String storeLink = "";
    @ConfPath(path = "queue.provider", confName = "config")
    public String queueProvider = "";
    @ConfPath(path = "queue.built-in.send-delay", confName = "config")
    public int queueSendDelay = 500;

    public List<Variable> variableList = new ArrayList<>();
    public Variable getVariable(String name) {
        return variableList.stream().filter(var -> var.isVar(name)).findFirst().orElse(null);
    }

    @LoadAfterConfLoad
    public void setVariables() {
        variableList.clear();
        FileConfiguration conf = Hub.getInstance().getSettingsConfig().getConfig();
        for (String key : conf.getConfigurationSection("variables").getKeys(false)) {
            String variablePath = "variables." + key,
                    variableName = key,
                    variableType = conf.getString(variablePath + ".type"),
                    variableFallback = conf.getString(variablePath + ".fallback");

            Map<String, String> resultsMap = new HashMap<>();
            for (String k : conf.getConfigurationSection(variablePath + ".results").getKeys(false)) {
                String condition = conf.getString(variablePath + ".results." + k + ".condition"),
                        text = conf.getString(variablePath + ".results." + k + ".text");

                resultsMap.put(condition, text);
            }

            String scope = "";
            if (variableType.contains("::")) {
                scope = variableType.split("::", 2)[0];
                variableType = variableType.split("::", 2)[1];
            }

            variableList.add(new Variable(variablePath, variableName, scope, variableType, variableFallback, resultsMap));
        }
        Configuration.settings = this;
    }
}