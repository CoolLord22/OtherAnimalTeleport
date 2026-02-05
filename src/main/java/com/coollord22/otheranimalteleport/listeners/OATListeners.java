package com.coollord22.otheranimalteleport.listeners;

import com.coollord22.otheranimalteleport.OATMethods;
import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import com.coollord22.otheranimalteleport.assets.Verbosity;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OATListeners implements Listener {

	private final OtherAnimalTeleport plugin;

	public OATListeners(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinUpdateChecker(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(player.hasPermission("otheranimalteleport.admin.updates") && plugin.config.globalUpdateChecking) {
			plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.updateChecker.checkForUpdate(player), 15L);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		boolean toSendError = false;
		boolean toSendLeft = false;
		boolean toSendTamedLeft = false;
		boolean toSendWorldGroupLeft = false;
		boolean toSendBlockedRegionLeft = false;
		if(!plugin.enabled || event.isCancelled()) {
			plugin.log.logInfo("Event was cancelled/plugin was disabled, ignoring teleport.", Verbosity.HIGH);
		} else if(plugin.config.ignoreCauses.contains(event.getCause())) {
			plugin.log.logInfo("Teleport reason was set to be ignored, skipping this event.", Verbosity.HIGH);
		} else if(!player.hasPermission("otheranimalteleport.player.use")) {
			plugin.log.logInfo("Player use permission check failed.", Verbosity.HIGH);
		} else if(!plugin.common.checkWorldGroup(event)) {
			plugin.log.logInfo("World group check failed. Will send player not_in_world_group notification", Verbosity.HIGHEST);
			toSendWorldGroupLeft = true;
		} else if(!plugin.common.allowedRegion(event.getFrom()) || !plugin.common.allowedRegion(event.getTo())) {
			plugin.log.logInfo("Blocked region check failed. Will send player blocked_region notification", Verbosity.HIGHEST);
			toSendBlockedRegionLeft = true;
		} else { // assume this teleport is valid and all checks have passed
			final boolean leashPerm = player.hasPermission("otheranimalteleport.player.teleportleashed");
			final boolean tamePerm = player.hasPermission("otheranimalteleport.player.teleportpets");

			handleChunkTickets(event.getFrom().getChunk(), event.getTo().getChunk(), false); // remove chunk tickets
			// For all nearby entities, check and process accordingly
			for(Entity ent : event.getFrom().getWorld().getNearbyEntities(event.getFrom(), plugin.config.radius, plugin.config.radius, plugin.config.radius)) {
				String entID = "[%s-%d] ".formatted(ent.getType().toString(), ent.getEntityId());
				plugin.log.logInfo(entID + "Detected entity near player teleport event. Beginning checks...", Verbosity.HIGHEST);
				if(plugin.config.entityMap.get(ent.getType()) == null || !plugin.config.entityMap.get(ent.getType())) {
					plugin.log.logInfo(entID + "Entity-type check failed (not allowed, or undefined in config), skipping...", Verbosity.HIGHEST);
					continue;
				}
				if(!(ent instanceof LivingEntity livingEntity)) {
					plugin.log.logInfo(entID + "Entity was not an instance of a living entity, skipping...", Verbosity.HIGHEST);
					continue;
				}
				if(!livingEntity.isLeashed()) {
					if(!(ent instanceof Tameable tameable)) {
						plugin.log.logInfo(entID + "Entity was not leashed AND not a tameable type. Will send player entity_left notification.", Verbosity.HIGHEST);
						toSendLeft = true;
						continue;
					}
					if(tameable.isTamed() && tamePerm) { // Process tamed entity teleport since it was not leashed but is tameable
						if(tameable.getOwner() == null || !tameable.getOwner().equals(player)) {
							plugin.log.logInfo(entID + "Tameable entity owner check failed, skipping...", Verbosity.HIGHEST);
							continue;
						} else if((tameable instanceof Sittable sittable) && sittable.isSitting()) {
							plugin.log.logInfo(entID + "Tamed entity was sitting. Will send player tamed_entity_left notification.", Verbosity.HIGHEST);
							toSendTamedLeft  = true;
							continue;
						}
						try {
							plugin.log.logInfo(entID + "Pet checks passed. Attempting to teleport entity.", Verbosity.HIGH);
							OATMethods.teleportTamedEnt(tameable, event.getTo(), plugin);
                        } catch(Exception e) {
							plugin.log.logInfo(entID + "Teleport reached exception. Will send player fail_teleport error.", Verbosity.HIGHEST);
							toSendError = true;
                        }
					}
				} else if(leashPerm) { // Process leashed entity teleport
					if(!livingEntity.getLeashHolder().equals(player)) {
						plugin.log.logInfo(entID + "Leash holder check failed. skipping...", Verbosity.HIGHEST);
						continue;
					}
					try {
						OATMethods.teleportLeashedEnt(livingEntity, event.getTo(), player, plugin);
                    } catch(Exception e) {
						plugin.log.logInfo(entID + "Teleport reached exception. Will send player fail_teleport notification.", Verbosity.HIGHEST);
						toSendError = true;
                    }
				}
			}
			handleChunkTickets(event.getFrom().getChunk(), event.getTo().getChunk(), true); // remove chunk tickets
		}
		// player notification handling
		if(plugin.config.failedTeleportMessage != null && !plugin.config.failedTeleportMessage.isEmpty() && toSendError) {
			plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.failedTeleportMessage, event);
		}
		if(toSendTamedLeft || toSendBlockedRegionLeft || toSendWorldGroupLeft || toSendLeft) {
			if(toSendBlockedRegionLeft && plugin.config.blockedRegionLeftMessage != null && !plugin.config.blockedRegionLeftMessage.isEmpty())
				plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.blockedRegionLeftMessage, event);
			else if(toSendWorldGroupLeft && plugin.config.notInWorldGroupMessage != null && !plugin.config.notInWorldGroupMessage.isEmpty())
				plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.notInWorldGroupMessage, event);
			if(toSendTamedLeft && plugin.config.leftTamedEntityMessage != null && !plugin.config.leftTamedEntityMessage.isEmpty())
				plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.leftTamedEntityMessage, event);
			else if(toSendLeft && plugin.config.leftEntityMessage != null && !plugin.config.leftEntityMessage.isEmpty())
				plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.leftEntityMessage, event);
		}
	}

	private void handleChunkTickets(Chunk from, Chunk to, boolean remove) {
		if (plugin.toUseTickets) {
			if(remove) {
				// Remove chunk tickets 30 ticks after just to ensure entities have been fully processed
				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					plugin.log.logInfo("Removing chunk tickets.", Verbosity.HIGHEST);
					from.removePluginChunkTicket(plugin);
					to.removePluginChunkTicket(plugin);
				}, 30L);
			} else {
				// Create chunk tickets one time before entities are gathered and teleported
				plugin.log.logInfo("Adding chunk tickets.", Verbosity.HIGHEST);
				from.addPluginChunkTicket(plugin);
				to.addPluginChunkTicket(plugin);
			}
		}
	}
}
