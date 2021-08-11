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
		Chunk fromChunk = from.getChunk();
		if(plugin.toUseTickets) 
			fromChunk.addPluginChunkTicket(plugin);

		plugin.log.logInfo("Attempting to null the leash holder.", Verbosity.HIGHEST);
		((LivingEntity) ent).setLeashHolder(null);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.log.logInfo("Protecting entity with damage resistance.", Verbosity.HIGHEST);
				((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5));
				plugin.log.logInfo("Teleporting entity " + ent.getType() + " with ID: " + ent.getEntityId(), Verbosity.HIGH);
				ent.teleport(to);
				plugin.log.logInfo("Re-attaching leash holder as " + p.getName() + ".", Verbosity.HIGHEST);
				((LivingEntity) ent).setLeashHolder(p);
				if(plugin.toUseTickets) 
					fromChunk.removePluginChunkTicket(plugin);
			}
		}.runTaskLater(plugin, 2);
	}

	public static void teleportEnt(Entity ent, Location from, Location to, Player p, OtherAnimalTeleport plugin) {
		Chunk fromChunk = from.getChunk();
		if(plugin.toUseTickets) 
			fromChunk.addPluginChunkTicket(plugin);

		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.log.logInfo("Protecting entity with damage resistance.", Verbosity.HIGHEST);
				((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5));
				plugin.log.logInfo("Teleporting entity" + ent.getType() + " with ID: " + ent.getEntityId(), Verbosity.HIGH);
				ent.teleport(to);

				if(plugin.toUseTickets) 
					fromChunk.removePluginChunkTicket(plugin);
			}
		}.runTaskLater(plugin, 2);
	}
}
