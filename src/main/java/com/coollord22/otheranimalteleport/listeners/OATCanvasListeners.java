package com.coollord22.otheranimalteleport.listeners;

import com.coollord22.otheranimalteleport.OATTeleportHandler;
import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import com.coollord22.otheranimalteleport.assets.Verbosity;
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

		if (!plugin.enabled) {
			plugin.log.logInfo("Plugin was disabled, ignoring teleport.", Verbosity.HIGH);
			return;
		} else if (event.getTo() == null) {
			plugin.log.logInfo("Teleport to-location was null, skipping this event.", Verbosity.HIGH);
			return;
		} else if (!player.hasPermission("otheranimalteleport.player.use")) {
			plugin.log.logInfo("Player use permission check failed.", Verbosity.HIGH);
			return;
		} else if (!plugin.common.checkWorldGroup(event.getFrom(), event.getTo())) {
			plugin.log.logInfo("World group check failed. Will send player not_in_world_group notification", Verbosity.HIGHEST);
			if (plugin.config.notInWorldGroupMessage != null && !plugin.config.notInWorldGroupMessage.isEmpty())
				plugin.common.sendMessage(plugin.config.usePrefix, player, plugin.config.notInWorldGroupMessage);
			return;
		} else if (!plugin.common.allowedRegion(event.getFrom()) || !plugin.common.allowedRegion(event.getTo())) {
			plugin.log.logInfo("Blocked region check failed. Will send player blocked_region notification", Verbosity.HIGHEST);
			if (plugin.config.blockedRegionLeftMessage != null && !plugin.config.blockedRegionLeftMessage.isEmpty())
				plugin.common.sendMessage(plugin.config.usePrefix, player, plugin.config.blockedRegionLeftMessage);
			return;
		}

		OATTeleportHandler.TeleportResult result = teleportHandler.handle(player, event.getFrom(), event.getTo());

		if (plugin.config.failedTeleportMessage != null && !plugin.config.failedTeleportMessage.isEmpty() && result.toSendError) {
			plugin.common.sendMessage(plugin.config.usePrefix, player, plugin.config.failedTeleportMessage);
		}
		if (result.toSendTamedLeft || result.toSendLeft) {
			if (result.toSendTamedLeft && plugin.config.leftTamedEntityMessage != null && !plugin.config.leftTamedEntityMessage.isEmpty())
				plugin.common.sendMessage(plugin.config.usePrefix, player, plugin.config.leftTamedEntityMessage);
			else if (result.toSendLeft && plugin.config.leftEntityMessage != null && !plugin.config.leftEntityMessage.isEmpty())
				plugin.common.sendMessage(plugin.config.usePrefix, player, plugin.config.leftEntityMessage);
		}
	}
}



