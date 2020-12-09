package me.vaperion.plugins.utils.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@SuppressWarnings("unused")
public class Config {
    private File file;
    private Plugin plugin;
    private String name;
    private FileConfiguration config;

    public Config(String name, Plugin plugin) {
        this.name = name;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder() + File.separator + name + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveDefaultConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        if (!file.exists()) {
            plugin.saveResource(name + ".yml", false);
        }
        loadConfig();
    }

    public void loadConfig() {
        try {
            config.load(file);
        } catch (Exception e) {
            getPlugin().getLogger().severe("An error occurred while tried to load " + getName() + ".yml!");
        }
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (Exception e) {
            getPlugin().getLogger().severe("An error occurred while tried to save " + getName() + ".yml!");
        }
    }

    public void loadAnnotatedValues(Class<?> clazz) {

        Object instance;
        try {
            instance = clazz.newInstance();
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to get a new instance of " + clazz.getSimpleName());
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfPath.class)) {
                String path = field.getAnnotation(ConfPath.class).path();
                String confName = field.getAnnotation(ConfPath.class).confName();

                if (!confName.equalsIgnoreCase(name)) return;
                if (!field.isAccessible()) field.setAccessible(true);

                try {
                    if (field.getType().isAssignableFrom(String.class)) {
                        field.set(instance, config.getString(path));
                    } else if (field.getType().isAssignableFrom(boolean.class)) {
                        field.set(instance, config.getBoolean(path));
                    } else if (field.getType().isAssignableFrom(byte.class)) {
                        Number integerN = config.getInt(path);
                        field.set(instance, integerN.byteValue());
                    } else if (field.getType().isAssignableFrom(short.class)) {
                        Number integerN = config.getInt(path);
                        field.set(instance, integerN.shortValue());
                    } else if (field.getType().isAssignableFrom(int.class)) {
                        field.set(instance, config.getInt(path));
                    } else if (field.getType().isAssignableFrom(long.class)) {
                        field.set(instance, config.getLong(path));
                    } else if (field.getType().isAssignableFrom(float.class)) {
                        Number doubleN = config.getDouble(path);
                        field.set(instance, doubleN.floatValue());
                    } else if (field.getType().isAssignableFrom(double.class)) {
                        field.set(instance, config.getDouble(path));
                    } else if (field.getType().isAssignableFrom(List.class)) {
                        field.set(instance, config.getList(path));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(LoadAfterConfLoad.class)) {
                if (!method.isAccessible()) method.setAccessible(true);
                try {
                    method.invoke(instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
