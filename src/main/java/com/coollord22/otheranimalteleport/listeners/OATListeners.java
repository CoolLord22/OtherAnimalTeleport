package com.coollord22.otheranimalteleport.listeners;

import com.coollord22.otheranimalteleport.OATMethods;
import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import com.coollord22.otheranimalteleport.assets.Verbosity;
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
		Player p = event.getPlayer();
		if(p.hasPermission("otheranimalteleport.admin.updates") && plugin.config.globalUpdateChecking) {
			plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.updateChecker.checkForUpdate(p), 15L);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		if(plugin.enabled && !event.isCancelled()) {
			if(plugin.config.ignoreCauses.contains(event.getCause())) {
				plugin.log.logInfo("Teleport reason was set to be ignored, skipping this event.", Verbosity.HIGHEST);
				return;
			}

			if(event.getPlayer().hasPermission("otheranimalteleport.player.use")) {
				plugin.log.logInfo("Player use permission check passed, running world checks.", Verbosity.HIGH);

				if(plugin.common.checkWorldGroup(event)) {
					plugin.log.logInfo("World group check passed, gathering nearby entities.", Verbosity.HIGH);
					int radius = plugin.config.radius;
					boolean toSendError = false;
					boolean toSendLeft = false;
					boolean toSendLeashedLeft = false;
					boolean toSendTamedLeft = false;

					for(Entity ent : event.getFrom().getWorld().getNearbyEntities(event.getFrom(), radius, radius, radius)) {
						String entID = "[Ent-" + ent.getEntityId() + "] ";
						plugin.log.logInfo(entID + "Found a(n) " + ent.getType() + ". Checking if type is allowed.", Verbosity.HIGH);
						if(plugin.config.entityMap.get(ent.getType()) != null && plugin.config.entityMap.get(ent.getType())) {
							plugin.log.logInfo(entID + "Entity-type check passed, checking player permissions.", Verbosity.HIGHEST);
							if(ent instanceof LivingEntity && event.getPlayer().hasPermission("otheranimalteleport.player.teleportleashed")) {
								plugin.log.logInfo(entID + "Player leashed permissions check passed, checking leash owner.", Verbosity.HIGHEST);
								if(((LivingEntity) ent).isLeashed()) {
									if(((LivingEntity) ent).getLeashHolder().equals(event.getPlayer())) {
										try {
											plugin.log.logInfo(entID + "Leash owner passed. Attempting to teleport entity.", Verbosity.HIGH);
											OATMethods.teleportLeashedEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
											continue;
										} catch(Exception e) {
											plugin.log.logInfo(entID + "Teleport reached exception. Sending player error.", Verbosity.HIGHEST);
											toSendError = true;
											continue;
										}
									}
									plugin.log.logInfo(entID + "Leash owner check failed. Sending player notification.", Verbosity.HIGHEST);
									toSendLeashedLeft = true;
								}
								plugin.log.logInfo(entID + "Left behind. Sending player notification.", Verbosity.HIGHEST);
								toSendLeft  = true;
							}
							if(ent instanceof Tameable && event.getPlayer().hasPermission("otheranimalteleport.player.teleportpets")) {
								plugin.log.logInfo(entID + "Player pet permissions check passed, checking pet owner.", Verbosity.HIGHEST);
								if(((Tameable) ent).isTamed() && ((Tameable) ent).getOwner() != null) {
									if(((Tameable) ent).getOwner().equals(event.getPlayer())) {
										if(ent instanceof Sittable && !((Sittable) ent).isSitting()) {
											try {
												plugin.log.logInfo(entID + "Pet checks passed. Attempting to teleport entity.", Verbosity.HIGH);
												OATMethods.teleportEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
												continue;
											} catch(Exception e) {
												plugin.log.logInfo(entID + "Teleport reached exception. Sending player error.", Verbosity.HIGHEST);
												toSendError = true;
												continue;
											}
										}
									}
									plugin.log.logInfo(entID + "Tamed entity owner matching / sitting check failed. Sending player notification.", Verbosity.HIGHEST);
									toSendTamedLeft  = true;
								}
								plugin.log.logInfo(entID + "Untamed tameable entity left behind.", Verbosity.HIGHEST);
							}
						}
					}

					if(plugin.config.failedTeleportMessage != null && !plugin.config.failedTeleportMessage.isEmpty() && toSendError) {
						plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.failedTeleportMessage, event);
					}

					if(toSendLeft || toSendLeashedLeft || toSendTamedLeft) {
						if(toSendLeashedLeft && plugin.config.leftLeashedEntityMessage != null && !plugin.config.leftLeashedEntityMessage.isEmpty())
							plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.leftLeashedEntityMessage, event);
						else if(toSendTamedLeft && plugin.config.leftTamedEntityMessage != null && !plugin.config.leftTamedEntityMessage.isEmpty())
							plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.leftTamedEntityMessage, event);
						else if(plugin.config.leftEntityMessage != null && !plugin.config.leftEntityMessage.isEmpty())
							plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.leftEntityMessage, event);
					}
				}
			}
			return; // Because we ran through the code, no need to send cancelled message
		}
		plugin.log.logInfo("Event was cancelled/plugin was disabled, ignoring teleport.", Verbosity.HIGHEST);
	}
}
