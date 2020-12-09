package me.vaperion.plugins.listeners;

import me.vaperion.plugins.utils.CountCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class PMListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord"))
            return;

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String command = in.readUTF();

            if (command.equals("PlayerCount")) {
                String server = in.readUTF();
                int amount = in.readInt();
                if (server.equalsIgnoreCase("ALL")) {
                    CountCache.GLOBAL_COUNT = amount;
                }
            }
        } catch (Exception e) {
        }
    }

}
