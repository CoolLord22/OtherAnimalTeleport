package com.coollord22.otheranimalteleport;

import com.coollord22.otheranimalteleport.assets.Verbosity;
import org.bukkit.Location;
import org.bukkit.entity.*;

public class OATTeleportHandler {

	private final OtherAnimalTeleport plugin;

	public OATTeleportHandler(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
	}

	/**
	 * Result of processing a teleport, containing flags used by the caller
	 * to send appropriate player notifications.
	 */
	public static class TeleportResult {
		public boolean toSendError = false;
		public boolean toSendLeft = false;
		public boolean toSendTamedLeft = false;
	}

	/**
	 * Processes a player teleport: iterates nearby entities and teleports
	 * leashed / tamed ones as configured. Does not depend on any specific
	 * event type – just the player and the two locations.
	 *
	 * @param player the teleporting player
	 * @param from   the origin location
	 * @param to     the destination location
	 * @return a {@link TeleportResult} with notification flags for the caller
	 */
	public TeleportResult handle(Player player, Location from, Location to) {
		TeleportResult result = new TeleportResult();

		final boolean leashPerm = player.hasPermission("otheranimalteleport.player.teleportleashed");
		final boolean tamePerm  = player.hasPermission("otheranimalteleport.player.teleportpets");

		handleChunkTickets(from, to, false);

		for (Entity ent : from.getWorld().getNearbyEntities(from, plugin.config.radius, plugin.config.radius, plugin.config.radius)) {
			String entID = "[%s-%d] ".formatted(ent.getType().toString(), ent.getEntityId());
			plugin.log.logInfo(entID + "Detected entity near player teleport event. Beginning checks...", Verbosity.HIGHEST);

			if (plugin.config.entityMap.get(ent.getType()) == null || !plugin.config.entityMap.get(ent.getType())) {
				plugin.log.logInfo(entID + "Entity-type check failed (not allowed, or undefined in config), skipping...", Verbosity.HIGHEST);
				continue;
			}
			if (!(ent instanceof LivingEntity livingEntity)) {
				plugin.log.logInfo(entID + "Entity was not an instance of a living entity, skipping...", Verbosity.HIGHEST);
				continue;
			}
			if (!livingEntity.isLeashed()) {
				if (!(ent instanceof Tameable tameable)) {
					plugin.log.logInfo(entID + "Entity was not leashed AND not a tameable type, Will send player entity_left notification.", Verbosity.HIGHEST);
					result.toSendLeft = true;
					continue;
				}
				if (tameable.isTamed() && tamePerm) {
					if (tameable.getOwner() == null || !tameable.getOwner().equals(player)) {
						plugin.log.logInfo(entID + "Tameable entity owner check failed, skipping...", Verbosity.HIGHEST);
						continue;
					} else if ((tameable instanceof Sittable sittable) && sittable.isSitting()) {
						plugin.log.logInfo(entID + "Tamed entity was sitting. Will send player tamed_entity_left notification.", Verbosity.HIGHEST);
						result.toSendTamedLeft = true;
						continue;
					}
					try {
						plugin.log.logInfo(entID + "Pet checks passed. Attempting to teleport entity.", Verbosity.HIGH);
						OATMethods.teleportTamedEnt(tameable, to, plugin);
					} catch (Exception e) {
						plugin.log.logInfo(entID + "Teleport reached exception. Will send player fail_teleport error.", Verbosity.HIGHEST);
						result.toSendError = true;
					}
				}
			} else if (leashPerm) {
				if (!livingEntity.getLeashHolder().equals(player)) {
					plugin.log.logInfo(entID + "Leash holder check failed. skipping...", Verbosity.HIGHEST);
					continue;
				}
				try {
					OATMethods.teleportLeashedEnt(livingEntity, to, player, plugin);
				} catch (Exception e) {
					plugin.log.logInfo(entID + "Teleport reached exception. Will send player fail_teleport notification.", Verbosity.HIGHEST);
					result.toSendError = true;
				}
			}
		}

		handleChunkTickets(from, to, true);
		return result;
	}

	private void handleChunkTickets(Location from, Location to, boolean remove) {
		if (plugin.toUseTickets) {
			if (remove) {
				plugin.foliaLib.getScheduler().runAtLocationLater(from, () -> {
					plugin.log.logInfo("Removing from-chunk ticket.", Verbosity.HIGHEST);
					from.getChunk().addPluginChunkTicket(plugin);
				}, 30L);
				plugin.foliaLib.getScheduler().runAtLocationLater(to, () -> {
					plugin.log.logInfo("Removing to-chunk ticket.", Verbosity.HIGHEST);
					to.getChunk().removePluginChunkTicket(plugin);
				}, 30L);
			} else {
				plugin.log.logInfo("Adding chunk tickets.", Verbosity.HIGHEST);
				from.getChunk().addPluginChunkTicket(plugin);
				to.getChunk().addPluginChunkTicket(plugin);
			}
		}
	}
}

