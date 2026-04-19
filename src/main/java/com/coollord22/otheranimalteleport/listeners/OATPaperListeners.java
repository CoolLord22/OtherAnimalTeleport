package com.coollord22.otheranimalteleport.listeners;

import com.coollord22.otheranimalteleport.OATTeleportHandler;
import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import com.coollord22.otheranimalteleport.assets.Verbosity;
import org.bukkit.entity.Player;
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
		Player player = event.getPlayer();
		boolean toSendWorldGroupLeft = false;
		boolean toSendBlockedRegionLeft = false;

		if(!plugin.enabled || event.isCancelled()) {
			plugin.log.logInfo("Event was cancelled/plugin was disabled, ignoring teleport.", Verbosity.HIGH);
			return;
		} else if(event.getTo() == null) {
			plugin.log.logInfo("Teleport to-location was null, skipping this event.", Verbosity.HIGH);
			return;
		} else if(plugin.config.ignoreCauses.contains(event.getCause())) {
			plugin.log.logInfo("Teleport reason was set to be ignored, skipping this event.", Verbosity.HIGH);
			return;
		} else if(!player.hasPermission("otheranimalteleport.player.use")) {
			plugin.log.logInfo("Player use permission check failed.", Verbosity.HIGH);
			return;
		} else if(!plugin.common.checkWorldGroup(event)) {
			plugin.log.logInfo("World group check failed. Will send player not_in_world_group notification", Verbosity.HIGHEST);
			toSendWorldGroupLeft = true;
		} else if(!plugin.common.allowedRegion(event.getFrom()) || !plugin.common.allowedRegion(event.getTo())) {
			plugin.log.logInfo("Blocked region check failed. Will send player blocked_region notification", Verbosity.HIGHEST);
			toSendBlockedRegionLeft = true;
		} else {
			OATTeleportHandler.TeleportResult result = teleportHandler.handle(player, event.getFrom(), event.getTo());

			if(plugin.config.failedTeleportMessage != null && !plugin.config.failedTeleportMessage.isEmpty() && result.toSendError) {
				plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.failedTeleportMessage, event);
			}
			if(result.toSendTamedLeft || result.toSendLeft) {
				if(result.toSendTamedLeft && plugin.config.leftTamedEntityMessage != null && !plugin.config.leftTamedEntityMessage.isEmpty())
					plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.leftTamedEntityMessage, event);
				else if(result.toSendLeft && plugin.config.leftEntityMessage != null && !plugin.config.leftEntityMessage.isEmpty())
					plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.leftEntityMessage, event);
			}
			return;
		}

		// Notification for world-group / blocked-region (no entity processing happened)
		if(toSendBlockedRegionLeft && plugin.config.blockedRegionLeftMessage != null && !plugin.config.blockedRegionLeftMessage.isEmpty())
			plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.blockedRegionLeftMessage, event);
		else if(toSendWorldGroupLeft && plugin.config.notInWorldGroupMessage != null && !plugin.config.notInWorldGroupMessage.isEmpty())
			plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.notInWorldGroupMessage, event);
	}
}
