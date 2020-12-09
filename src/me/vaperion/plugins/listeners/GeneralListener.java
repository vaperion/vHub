package me.vaperion.plugins.listeners;

import me.marvin.simplequeue.SimpleQueueHandler;
import me.vaperion.plugins.Hub;
import me.vaperion.plugins.configs.ItemsConfig;
import me.vaperion.plugins.queue.impl.SimpleProvider;
import me.vaperion.plugins.utils.ChatUtils;
import me.vaperion.plugins.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GeneralListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        for (int i = 0; i < Configuration.language.joinClearchatAmount; i++) {
            e.getPlayer().sendMessage("");
        }
        if (Configuration.language.joinSendWelcome) {
            for (String line : Configuration.language.joinWelcomeMessage) {
                e.getPlayer().sendMessage(ChatUtils.colorize(ChatUtils.transformLine(e.getPlayer(), line)));
            }
        }
        e.getPlayer().getInventory().clear();
        e.getPlayer().setHealth(20);
        e.getPlayer().setFoodLevel(20);
        for (Integer row : ItemsConfig.itemTable.rowKeySet()) {
            e.getPlayer().getInventory().setItem(row, ItemsConfig.itemTable.row(row).keySet().toArray(new ItemStack[0])[0]);
        }
    }

    private void openSelectorGui(Player p) {
        Inventory inv = Bukkit.createInventory(p, ItemsConfig.selectorSlots, ItemsConfig.selectorTitle);

        for (Integer row : ItemsConfig.selectorTable.rowKeySet()) {
            inv.setItem(row, ItemsConfig.selectorTable.row(row).keySet().toArray(new ItemStack[0])[0]);
        }

        p.openInventory(inv);
        p.updateInventory();
    }

    private void handleSelectorClick(Player p, int slot) {
        Map<ItemStack, String> map = ItemsConfig.selectorTable.row(slot);
        if (map == null || map.isEmpty()) return;
        String server = map.values().toArray(new String[0])[0];
        if (server.equalsIgnoreCase("none")) return;
        p.closeInventory();
        Hub.getInstance().getQueueProvider().offer(p, server);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = ((Player) e.getWhoClicked());
        if (e.getClickedInventory() == null || e.getClickedInventory().getType() == InventoryType.PLAYER || e.getClickedInventory().getType() == InventoryType.CRAFTING) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            player.updateInventory();
            return;
        }
        if (e.getClickedInventory().getTitle().equals(ItemsConfig.selectorTitle) && e.getCurrentItem() != null) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            player.updateInventory();
            handleSelectorClick(player, e.getRawSlot());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction().name().contains("RIGHT")) {
            for (Integer row : ItemsConfig.itemTable.rowKeySet()) {
                Map<ItemStack, String> map = ItemsConfig.itemTable.row(row);
                if (e.getPlayer().getInventory().getHeldItemSlot() == row && map.values().toArray(new String[0])[0].equalsIgnoreCase("selector")) {
                    e.setCancelled(true);
                    e.setUseItemInHand(Event.Result.DENY);
                    p.updateInventory();
                    openSelectorGui(p);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
        e.getPlayer().updateInventory();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        e.setCancelled(true);
        e.setDamage(0.0D);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);

        if (Hub.getInstance().getQueueProvider() instanceof SimpleProvider) {
            SimpleQueueHandler<Player> handler = Hub.getInstance().getBuiltInQueue();
            handler.unQueueEntry(e.getPlayer());
        }
    }

}
