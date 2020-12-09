package io.github.thatkawaiisam.assemble;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssembleThread extends Thread {

    private Assemble assemble;

    AssembleThread(Assemble assemble) {
        this.assemble = assemble;
        this.start();
    }

    @Override
    public void run() {
        while(true) {
            try {
                tick();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            try {
                sleep(assemble.getTicks() * 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        for (Player player : this.assemble.getPlugin().getServer().getOnlinePlayers()) {
            final AssembleBoard board = this.assemble.getBoards().get(player.getUniqueId());
            if (board == null) {
                continue;
            }
            final Scoreboard scoreboard = board.getScoreboard();
            final Objective objective = board.getObjective();


            final String title = ChatColor.translateAlternateColorCodes('&', this.assemble.getAdapter().getTitle(player));

            if (!objective.getDisplayName().equals(title)) {
                objective.setDisplayName(title);
            }

            List<String> lines = this.assemble.getAdapter().getLines(player);
            final List<String> newLines = lines != null ? new ArrayList<>(this.assemble.getAdapter().getLines(player)) : null;

            if (newLines == null || newLines.isEmpty()) {
                board.getEntries().forEach(AssembleBoardEntry::remove);
                board.getEntries().clear();
            } else {
                if (!this.assemble.getAssembleStyle().isDecending()) {
                    Collections.reverse(newLines);
                }

                if (board.getEntries().size() > newLines.size()) {
                    for (int i = newLines.size(); i < board.getEntries().size(); i++) {
                        final AssembleBoardEntry entry = board.getEntryAtPosition(i);

                        if (entry != null) {
                            entry.remove();
                        }
                    }
                }

                int cache = this.assemble.getAssembleStyle().getStartNumber();
                for (int i = 0; i < newLines.size(); i++) {
                    AssembleBoardEntry entry = board.getEntryAtPosition(i);

                    final String line = ChatColor.translateAlternateColorCodes('&', newLines.get(i));

                    if (entry == null) {
                        entry = new AssembleBoardEntry(board, line);
                    }

                    entry.setText(line);
                    entry.setup();
                    entry.send(
                            this.assemble.getAssembleStyle().isDecending() ? cache-- : cache++
                    );
                }
            }

            if (player.getScoreboard() != scoreboard) {
                player.setScoreboard(scoreboard);
            }
        }
    }
}
