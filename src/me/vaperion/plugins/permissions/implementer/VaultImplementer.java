package me.vaperion.plugins.permissions.implementer;

import me.vaperion.plugins.permissions.HubRankProvider;
import me.vaperion.plugins.utils.Logging;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultImplementer implements HubRankProvider {
    private Permission permission;

    public VaultImplementer() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null) permission = rsp.getProvider();
        else Logging.error("Vault does not have a registered permission manager.");
    }

    @Override
    public String getRank(Player player) {
        return permission.getPrimaryGroup(player);
    }
}
