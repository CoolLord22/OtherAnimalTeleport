package com.coollord22.otheranimalteleport;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class OATMethods {
	public static void teleportLeashedEnt(Entity ent, Location from, Location to, Player p, OtherAnimalTeleport plugin) {
		((Animals) ent).setLeashHolder(null);
		new BukkitRunnable() {
			@Override
			public void run() {
				((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5));
				ent.teleport(to);
				
				((Animals) ent).setLeashHolder(p);
			}
		}.runTaskLater(plugin, 2);
	}

	public static void teleportEnt(Entity ent, Location from, Location to, Player p, OtherAnimalTeleport plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5));
				ent.teleport(to);
			}
		}.runTaskLater(plugin, 2);
	}
}
