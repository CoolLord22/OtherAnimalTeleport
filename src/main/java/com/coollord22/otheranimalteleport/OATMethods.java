package com.coollord22.otheranimalteleport;

import com.coollord22.otheranimalteleport.assets.Verbosity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class OATMethods {
	public static void teleportLeashedEnt(Entity ent, Location to, Player p, OtherAnimalTeleport plugin) {
		String entID = "[Ent-" + ent.getEntityId() + "] ";

		plugin.log.logInfo(entID + "Attempting to null the leash holder.", Verbosity.HIGHEST);
		((LivingEntity) ent).setLeashHolder(null);

		boolean invulnerable = ent.isInvulnerable();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.log.logInfo(entID + "Protecting entity with invulnerability and resistance.", Verbosity.HIGHEST);
				ent.setInvulnerable(true);

				plugin.log.logInfo(entID + "Teleporting entity " + ent.getType(), Verbosity.HIGH);
				ent.teleport(to);

				// Delay re-attaching of leash by 10 ticks to ensure entity pathfinding cant freeze
				new BukkitRunnable() {
					@Override
					public void run() {
						plugin.log.logInfo(entID + "Re-attaching leash holder as " + p.getName() + ".", Verbosity.HIGHEST);
						((LivingEntity)ent).setLeashHolder(p);
					}
				}.runTaskLater(plugin, 10L);

				undoInvulnerable(ent, invulnerable, plugin);
			}
		}.runTaskLater(plugin, 2L);
	}

	public static void teleportEnt(Entity ent, Location to, Player p, OtherAnimalTeleport plugin) {
		String entID = "[Ent-" + ent.getEntityId() + "] ";

		boolean invulnerable = ent.isInvulnerable();

		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.log.logInfo(entID + "Protecting entity with invulnerability and resistance.", Verbosity.HIGHEST);
				ent.setInvulnerable(true);

				plugin.log.logInfo(entID + "Teleporting entity " + ent.getType(), Verbosity.HIGH);
				ent.teleport(to);
				undoInvulnerable(ent, invulnerable, plugin);
			}
		}.runTaskLater(plugin, 2L);
	}

	private static void undoInvulnerable(Entity ent, boolean invulnerable, OtherAnimalTeleport plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.log.logInfo("[Ent-" + ent.getEntityId() + "] Reverting invulnerability status.", Verbosity.HIGHEST);
				ent.setInvulnerable(invulnerable);
			}
		}.runTaskLater(plugin, 30L);
	}
}
