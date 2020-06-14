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

public class OATListeners implements Listener {

	private final OtherAnimalTeleport plugin;
	private final DecimalFormat df = new DecimalFormat("#.#");

	public OATListeners(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinUpdateChecker(PlayerJoinEvent event) throws InterruptedException {
		Player p = event.getPlayer();
		if(p.hasPermission("otheranimalteleport.admin.updates") && plugin.config.globalUpdateChecking)
			plugin.updateChecker.checkForUpdate(p);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		boolean sameGroup = false;
		World fromWorld = event.getFrom().getWorld();
		World toWorld = event.getTo().getWorld();
		if(fromWorld.equals(toWorld)) {
			sameGroup = true;
		} else {
			for(Set<World> worldList : plugin.config.worldGroup) {
				if(worldList.contains(fromWorld) && worldList.contains(toWorld)) {
					sameGroup = true;
					break;
				}
			}
		}
		if(plugin.enabled && !event.isCancelled() && sameGroup) {
			if(event.getPlayer().hasPermission("otheranimalteleport.player.use")) {
				int radius = plugin.config.radius;
				boolean toSendError = false;
				for(Entity ent : event.getFrom().getWorld().getNearbyEntities(event.getFrom(), radius, radius, radius)) {
					if(plugin.config.allowedEnts.contains(ent.getType())) {
						if(((LivingEntity) ent).isLeashed() && ((LivingEntity) ent).getLeashHolder().equals(event.getPlayer())) {
							OATMethods.teleportLeashedEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
						}
						else if(ent instanceof Sittable) {
							if(((Tameable) ent).isTamed() && ((Tameable) ent).getOwner().equals(event.getPlayer()) && !((Sittable) ent).isSitting()) {
								OATMethods.teleportEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
							}
						} else {
							toSendError = true;
						}
					} 
				}
				if(plugin.config.failedTeleportMessage != null) {
					if(!plugin.config.failedTeleportMessage.isEmpty() && toSendError) {
						plugin.common.sendMessage(plugin.config.usePrefix, event.getPlayer(), plugin.config.failedTeleportMessage
								.replaceAll("%x", df.format(event.getFrom().getBlockX()))
								.replaceAll("%y", df.format(event.getFrom().getBlockY()))
								.replaceAll("%z", df.format(event.getFrom().getBlockZ())));
					}
				}
			}
		}	
	}
}
