package com.coollord22.otheranimalteleport.listeners;

import java.text.DecimalFormat;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.coollord22.otheranimalteleport.OATMethods;
import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import com.coollord22.otheranimalteleport.assets.Verbosity;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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
			if(plugin.config.ignoreUnknownCauses && event.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
				plugin.log.logInfo("Ignore unknown cause was enabled, ignoring this event.", Verbosity.HIGHEST);
				return;
			}

			/*TODO: Wait for this to be added into 1.19.3*/
			try {
				if(event.getCause() == PlayerTeleportEvent.TeleportCause.DISMOUNT) {
					plugin.log.logInfo("Player dismounted vehicle, ignoring teleport event.", Verbosity.HIGHEST);
					return;
				}
			} catch(NoSuchFieldError err) {
				plugin.log.logInfo("Version < 1.19.3 so no DISMOUNT cause found.", Verbosity.HIGHEST);
			}

			if(event.getPlayer().hasPermission("otheranimalteleport.player.use")) {
				plugin.log.logInfo("Player use permission check passed, running world checks.", Verbosity.HIGH);

				if(plugin.common.checkWorldGroup(event)) {
					plugin.log.logInfo("World group check passed, gathering nearby entities.", Verbosity.HIGH);
					int radius = plugin.config.radius;
					boolean toSendError = false;
					boolean toSendLeft = false;

					for(Entity ent : event.getFrom().getWorld().getNearbyEntities(event.getFrom(), radius, radius, radius)) {
						plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Found a(n) " + ent.getType() + ". Checking if type is allowed.", Verbosity.HIGH);
						if(plugin.config.entityMap.get(ent.getType()) != null && plugin.config.entityMap.get(ent.getType())) {
							plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Entity-type check passed, checking player permissions.", Verbosity.HIGHEST);
							if(ent instanceof LivingEntity && event.getPlayer().hasPermission("otheranimalteleport.player.teleportleashed")) {
								plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Player leashed permissions check passed, checking leash owner.", Verbosity.HIGHEST);
								if(((LivingEntity) ent).isLeashed() && ((LivingEntity) ent).getLeashHolder().equals(event.getPlayer())) {
									try {
										plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Leash owner passed. Attempting to teleport entity.", Verbosity.HIGH);
										OATMethods.teleportLeashedEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
										continue;
									} catch(Exception e) {
										plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Teleport reached exception. Sending player error.", Verbosity.HIGHEST);
										toSendError = true;
										continue;
									}
								}
								plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Left behind. Sending player notification.", Verbosity.HIGHEST);
								toSendLeft  = true;
							}
							if(ent instanceof Tameable && event.getPlayer().hasPermission("otheranimalteleport.player.teleportpets")) {
								plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Player pet permissions check passed, checking pet owner.", Verbosity.HIGHEST);
								if(((Tameable) ent).isTamed() && ((Tameable) ent).getOwner() != null && ((Tameable) ent).getOwner().equals(event.getPlayer())) {
									if(ent instanceof Sittable && !((Sittable) ent).isSitting()) {
										try {
											plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Pet checks passed. Attempting to teleport entity.", Verbosity.HIGH);
											OATMethods.teleportEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
											continue;
										} catch(Exception e) {
											plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Teleport reached exception. Sending player error.", Verbosity.HIGHEST);
											toSendError = true;
											continue;
										}
									}
								}
								plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Left behind. Sending player notification.", Verbosity.HIGHEST);
								toSendLeft  = true;
							}
						}
					}

					if(plugin.config.failedTeleportMessage != null && !plugin.config.failedTeleportMessage.isEmpty() && toSendError) {
						plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.failedTeleportMessage, event);
					}

					if(plugin.config.leftEntityMessage != null && !plugin.config.leftEntityMessage.isEmpty() && toSendLeft) {
						plugin.common.sendMessage(plugin.config.usePrefix, plugin.config.leftEntityMessage, event);
					}
				}
			}
			return; // Because we ran through the code, no need to send cancelled message
		}
		plugin.log.logInfo("Event was cancelled/plugin was disabled, ignoring teleport.", Verbosity.HIGHEST);
	}
}
