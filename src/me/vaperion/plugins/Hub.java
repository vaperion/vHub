package me.vaperion.plugins;

import com.minnymin.command.CommandFramework;
import io.github.thatkawaiisam.assemble.Assemble;
import lombok.Getter;
import me.marvin.simplequeue.SimpleQueueHandler;
import me.marvin.simplequeue.provider.QueuePriorityProvider;
import me.marvin.simplequeue.queue.Queue;
import me.vaperion.plugins.adapters.ScoreboardAdapter;
import me.vaperion.plugins.commands.HubCommands;
import me.vaperion.plugins.commands.QueueAdminCommands;
import me.vaperion.plugins.commands.QueueCommands;
import me.vaperion.plugins.configs.ItemsConfig;
import me.vaperion.plugins.listeners.GeneralListener;
import me.vaperion.plugins.listeners.PMListener;
import me.vaperion.plugins.metrics.Metrics;
import me.vaperion.plugins.permissions.HubRankProvider;
import me.vaperion.plugins.permissions.implementer.DefaultImplementer;
import me.vaperion.plugins.permissions.implementer.VaultImplementer;
import me.vaperion.plugins.queue.HubQueueProvider;
import me.vaperion.plugins.queue.impl.SimpleProvider;
import me.vaperion.plugins.rank.RankHandler;
import me.vaperion.plugins.utils.Configuration;
import me.vaperion.plugins.utils.CountCache;
import me.vaperion.plugins.utils.Logging;
import me.vaperion.plugins.utils.QueueInfoTask;
import me.vaperion.plugins.utils.config.Config;
import me.vaperion.plugins.utils.config.configs.Language;
import me.vaperion.plugins.utils.config.configs.Scoreboard;
import me.vaperion.plugins.utils.config.configs.Settings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotConfig;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Map;
import java.util.Objects;

@Getter
public class Hub extends JavaPlugin {
    @Getter private static Hub instance;

    private Config settingsConfig;
    private HubQueueProvider queueProvider;
    private SimpleQueueHandler<Player> builtInQueue;
    private HubRankProvider rankProvider;
    private RankHandler rankHandler;
    private CommandFramework commandFramework;
    private Assemble assemble;
    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;
        metrics = new Metrics(this);

        if (!detectPermissions()) {
            Logging.error("No permissions plugin was detected. You can set your own implementer with calling Hub#setPermsImplementer.");
            setPermsImplementer(new DefaultImplementer());
        }

        settingsConfig = new Config("config", this);
        settingsConfig.saveDefaultConfig();
        ItemsConfig.load();

        assemble = new Assemble(this, new ScoreboardAdapter());
        rankHandler = new RankHandler();

        commandFramework = new CommandFramework(this);
        commandFramework.registerCommands(new HubCommands());

        getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PMListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        loadMessages();

        initializeQueue();
        commandFramework.registerCommands(new QueueCommands());
        if (builtInQueue != null) commandFramework.registerCommands(new QueueAdminCommands());

        assemble.setup();

        if (SpigotConfig.bungee)
            getServer().getScheduler().runTaskTimer(this, () -> updatePlayerCount("ALL"), 0, 20);
        else
            getServer().getScheduler().runTaskTimer(this, () -> CountCache.GLOBAL_COUNT = Bukkit.getOnlinePlayers().size(), 0, 20);
    }

    @Override
    public void onDisable() {
        if (queueProvider instanceof SimpleProvider) {
            SimpleProvider provider = (SimpleProvider) queueProvider;

            settingsConfig.getConfig().set("queues", null);
            settingsConfig.saveConfig();

            int i = 0;
            for (Map.Entry<String, Queue<Player>> entry : provider.getHandler().getQueues().entrySet()) {
                settingsConfig.getConfig().set("queues." + i + ".server", entry.getKey());
                settingsConfig.getConfig().set("queues." + i + ".limit", entry.getValue().getLimit());
                i++;
            }
            settingsConfig.saveConfig();
        }
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        assemble.cleanup();
        assemble = null;
        instance = null;
    }

    public void loadMessages() {
        settingsConfig.loadConfig();
        settingsConfig.loadAnnotatedValues(Scoreboard.class);
        settingsConfig.loadAnnotatedValues(Settings.class);
        settingsConfig.loadAnnotatedValues(Language.class);
    }

    private void initializeQueue() {
        switch (Configuration.settings.queueProvider.toLowerCase()) {
            case "built-in": {
                builtInQueue = new SimpleQueueHandler<>(Configuration.settings.queueSendDelay, new QueuePriorityProvider<Player>() {
                    @Override
                    public String getProviderName() {
                        return "built-in-provider";
                    }

                    @Override
                    public int getEntryPriority(Player player) {
                        return rankHandler.getRankForPlayer(player).getPriority();
                    }
                }, (player, q, p) -> sendPlayer(player, q.getId()), Objects::isNull);

                Bukkit.getScheduler().runTaskTimerAsynchronously(this, new QueueInfoTask(), 20, 30 * 20);
                queueProvider = new SimpleProvider(builtInQueue);

                if (settingsConfig.getConfig().isConfigurationSection("queues")) {
                    for (String key : settingsConfig.getConfig().getConfigurationSection("queues").getKeys(false)) {
                        ConfigurationSection section = settingsConfig.getConfig().getConfigurationSection("queues." + key);
                        String server = section.getString("server");
                        int limit = section.getInt("limit");
                        queueProvider.createQueue(server);
                        ((SimpleProvider) queueProvider).getQueue(server).setLimit(limit);
                    }
                }
                break;
            }
        }
    }

    private boolean detectPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") != null && getServer().getPluginManager().isPluginEnabled("Vault")) {
            Logging.info("Found Vault! Trying to load permission & chat manager...");
            setPermsImplementer(new VaultImplementer());
        }
        return rankProvider != null;
    }

    private void setPermsImplementer(HubRankProvider implementer) {
        rankProvider = implementer;
        Logging.info("Using permission implementer " + implementer.getClass().getSimpleName()+ ".");
    }

    public void updatePlayerCount(String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("PlayerCount");
            out.writeUTF(server);

            Bukkit.getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPlayer(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);

            player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
