package com.coollord22.otheranimalteleport.listeners;

import com.coollord22.otheranimalteleport.OATTeleportHandler;
import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import io.canvasmc.canvas.event.EntityPostTeleportAsyncEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class OATCanvasListeners implements Listener {

	private final OtherAnimalTeleport plugin;
	private final OATTeleportHandler teleportHandler;

	public OATCanvasListeners(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
		this.teleportHandler = new OATTeleportHandler(plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPostTeleport(EntityPostTeleportAsyncEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		teleportHandler.checkAndHandle(player, event.getFrom(), event.getTo(), event.getCause());
	}
}

