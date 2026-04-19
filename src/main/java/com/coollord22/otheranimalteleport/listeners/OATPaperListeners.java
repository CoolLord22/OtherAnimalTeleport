package com.coollord22.otheranimalteleport.listeners;

import com.coollord22.otheranimalteleport.OATTeleportHandler;
import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import com.coollord22.otheranimalteleport.assets.Verbosity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OATPaperListeners implements Listener {

	private final OtherAnimalTeleport plugin;
	private final OATTeleportHandler teleportHandler;

	public OATPaperListeners(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
		this.teleportHandler = new OATTeleportHandler(plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		teleportHandler.checkAndHandle(event.getPlayer(), event.getFrom(), event.getTo(), event.getCause());
	}
}
