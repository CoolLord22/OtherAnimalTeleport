package com.coollord22.otheranimalteleport;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.coollord22.otheranimalteleport.assets.Verbosity;

public class OATMethods {
	public static void teleportLeashedEnt(Entity ent, Location from, Location to, Player p, OtherAnimalTeleport plugin) {
		String entID = "[Ent-" + ent.getEntityId() + "] ";
		Chunk fromChunk = from.getChunk();
		Chunk toChunk = to.getChunk();
		if(plugin.toUseTickets) {
			fromChunk.addPluginChunkTicket(plugin);
			toChunk.addPluginChunkTicket(plugin);
		}

		plugin.log.logInfo(entID + "Attempting to null the leash holder.", Verbosity.HIGHEST);
		((LivingEntity) ent).setLeashHolder(null);

		boolean invulnerable = ent.isInvulnerable();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.log.logInfo(entID + "Protecting entity with invulnerability and resistance.", Verbosity.HIGHEST);
				ent.setInvulnerable(true);
				((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5));

				plugin.log.logInfo(entID + "Teleporting entity " + ent.getType(), Verbosity.HIGH);
				ent.teleport(to);

				plugin.log.logInfo(entID + "Re-attaching leash holder as " + p.getName() + ".", Verbosity.HIGHEST);
				((LivingEntity) ent).setLeashHolder(p);

				if(plugin.toUseTickets) {
					fromChunk.removePluginChunkTicket(plugin);
					toChunk.removePluginChunkTicket(plugin);
				}

				undoInvulnerable(ent, invulnerable, plugin);
			}
		}.runTaskLater(plugin, 2);
	}

	public static void teleportEnt(Entity ent, Location from, Location to, Player p, OtherAnimalTeleport plugin) {
		String entID = "[Ent-" + ent.getEntityId() + "] ";
		Chunk fromChunk = from.getChunk();
		Chunk toChunk = to.getChunk();
		if(plugin.toUseTickets) {
			fromChunk.addPluginChunkTicket(plugin);
			toChunk.addPluginChunkTicket(plugin);
		}

		boolean invulnerable = ent.isInvulnerable();

		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.log.logInfo(entID + "Protecting entity with invulnerability and resistance.", Verbosity.HIGHEST);
				ent.setInvulnerable(true);
				((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5));

				plugin.log.logInfo(entID + "Teleporting entity" + ent.getType(), Verbosity.HIGH);
				ent.teleport(to);

				if(plugin.toUseTickets) {
					fromChunk.removePluginChunkTicket(plugin);
					toChunk.removePluginChunkTicket(plugin);
				}

				undoInvulnerable(ent, invulnerable, plugin);
			}
		}.runTaskLater(plugin, 2);
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
