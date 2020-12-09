package me.vaperion.plugins.adapters;

import io.github.thatkawaiisam.assemble.AssembleAdapter;
import me.marvin.simplequeue.queue.QueueEntry;
import me.vaperion.plugins.Hub;
import me.vaperion.plugins.utils.ChatUtils;
import me.vaperion.plugins.utils.Configuration;
import me.vaperion.plugins.utils.CountCache;
import me.vaperion.plugins.utils.Variable;
import me.vaperion.plugins.utils.config.Config;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {
    @Override
    public String getTitle(Player player) {
        return Configuration.scoreboard.scoreboardTitle;
    }

    @Override
    public List<String> getLines(Player player) {
        if (Hub.getInstance().getQueueProvider().isQueued(player, null)) return transform(player, Configuration.scoreboard.scoreboardLinesInQueue, 1);
        return transform(player, Configuration.scoreboard.scoreboardLines, 0);
    }

    private List<String> transform(Player player, List<String> lines, int type) {
        List<String> copy = new ArrayList<>();
        for (String line : lines) {
            line = ChatUtils.transformLine(player, line);

            for (Variable variable : Configuration.settings.variableList) {
                if (!variable.getScope().isEmpty() && !variable.getScope().equalsIgnoreCase("provider")) continue;
                Object result = null;

                if (variable.getType().equalsIgnoreCase("global-players")) {
                    result = CountCache.GLOBAL_COUNT;
                }
                if (variable.getType().equalsIgnoreCase("rank-priority")) {
                    result = Hub.getInstance().getRankHandler().getRankForPlayer(player).getPriority();
                }

                line = line.replace(variable.getFormatted(), variable.getStringForResult(result));
            }

            if (type == 1) {
                String queue = Hub.getInstance().getQueueProvider().getQueueName(player);
                if (!queue.isEmpty()) {
                    line = line.replace("%queue_server%", queue);
                    line = line.replace("%queue_pos%", String.valueOf(Hub.getInstance().getQueueProvider().getPosition(player, queue)));
                    line = line.replace("%queue_max%", String.valueOf(Hub.getInstance().getQueueProvider().getSize(queue)));
                }
            }

            copy.add(line);
        }
        if (type != 2 && Configuration.scoreboard.scoreboardFooterEnabled) {
            String lastLine = copy.get(copy.size() - 1);
            boolean shouldPutBack = false;
            if (lastLine.contains("----")) {
                copy.remove(copy.size() - 1);
                shouldPutBack = true;
            }
            copy.addAll(transform(player, Configuration.scoreboard.scoreboardLinesFooter, 2));
            if (shouldPutBack) copy.add(lastLine);
        }
        return copy;
    }
}
