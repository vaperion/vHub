package me.vaperion.plugins.configs;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.vaperion.plugins.Hub;
import me.vaperion.plugins.utils.ItemBuilder;
import me.vaperion.plugins.utils.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemsConfig {

	public static Table<Integer, ItemStack, String> itemTable = HashBasedTable.create();
	public static String selectorTitle = "";
	public static int selectorSlots = 0;
	public static Table<Integer, ItemStack, String> selectorTable = HashBasedTable.create();

	public static void load() {
		Config config = Hub.getInstance().getSettingsConfig();
		if (!config.getConfig().isConfigurationSection("items")) return;
		for (String key : config.getConfig().getConfigurationSection("items").getKeys(false)) {
			ConfigurationSection section = config.getConfig().getConfigurationSection("items." + key);
			int slot = -1;
			try {
				slot = Integer.parseInt(key);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (slot < 0) continue;
			String  name = translate(section.getString("name")),
					action = section.getString("action");
			Material material = Material.getMaterial(section.getString("item"));
			short durability = ((short) section.getInt("durability"));
			List<String> lore = translate(section.getStringList("lore"));

			itemTable.put(
					slot,
					new ItemBuilder(material)
							.data(durability)
							.displayName(name)
							.lore(lore).build(),
					action
			);
		}

		selectorTitle = translate(config.getConfig().getString("selector.title"));
		selectorSlots = config.getConfig().getInt("selector.size");
		for (String key : config.getConfig().getConfigurationSection("selector.items").getKeys(false)) {
			ConfigurationSection section = config.getConfig().getConfigurationSection("selector.items." + key);
			int slot = -1;
			try {
				slot = Integer.parseInt(key);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (slot < 0) continue;
			String  name = translate(section.getString("name")),
					server = section.getString("server");
			Material material = Material.getMaterial(section.getString("item"));
			short durability = ((short) section.getInt("durability"));
			List<String> lore = translate(section.getStringList("lore"));

			selectorTable.put(
					slot,
					new ItemBuilder(material)
							.data(durability)
							.displayName(name)
							.lore(lore).build(),
					server
			);
		}
	}

	private static String translate(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	private static List<String> translate(List<String> list) {
		List<String> colorized = new ArrayList<>();
		for (String s : list) {
			colorized.add(translate(s));
		}
		return colorized;
	}

}
