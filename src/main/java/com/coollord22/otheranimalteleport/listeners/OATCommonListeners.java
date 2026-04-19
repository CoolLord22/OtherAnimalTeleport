package com.coollord22.otheranimalteleport.listeners;

import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OATCommonListeners implements Listener {

    private final OtherAnimalTeleport plugin;

    public OATCommonListeners(OtherAnimalTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinUpdateChecker(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("otheranimalteleport.admin.updates") && plugin.config.globalUpdateChecking) {
            plugin.foliaLib.getScheduler().runLater(() -> plugin.updateChecker.checkForUpdate(player), 15L);
        }
    }

}
