package me.vaperion.plugins.utils;

import lombok.Getter;
import lombok.Setter;
import me.vaperion.plugins.utils.config.configs.Language;
import me.vaperion.plugins.utils.config.configs.Scoreboard;
import me.vaperion.plugins.utils.config.configs.Settings;

@Getter
@Setter
public class Configuration {

    public static Language language;
    public static Settings settings;
    public static Scoreboard scoreboard;

}
