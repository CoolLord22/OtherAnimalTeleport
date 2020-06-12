package com.coollord22.otheranimalteleport.listeners;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.coollord22.otheranimalteleport.OATMethods;
import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import com.coollord22.otheranimalteleport.assets.OATConfig;

public class OATListeners implements Listener {

	private final OtherAnimalTeleport plugin;

	public OATListeners(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		boolean sameGroup = false;
		World fromWorld = event.getFrom().getWorld();
		World toWorld = event.getTo().getWorld();
		if(fromWorld.equals(toWorld)) {
			sameGroup = true;
		} else {
			for(List<String> worldList : OATConfig.worldGroup) {
				if(worldList.contains(fromWorld.getName()) && worldList.contains(toWorld.getName())) {
					sameGroup = true;
					break;
				}
			}
		}
		if(plugin.enabled && !event.isCancelled() && sameGroup) {
			if(event.getPlayer().hasPermission("otheranimalteleport.player.use")) {
				int radius = OATConfig.radius;

				for(Entity ent : event.getFrom().getWorld().getNearbyEntities(event.getFrom(), radius, radius, radius)) {
					if(ent instanceof Animals) {
						if(((Animals) ent).isLeashed() && ((Animals) ent).getLeashHolder().equals(event.getPlayer())) {
							OATMethods.teleportLeashedEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
						}

						else if(ent instanceof Sittable) {
							if(((Tameable) ent).isTamed() && ((Tameable) ent).getOwner().equals(event.getPlayer()) && !((Sittable) ent).isSitting()) {
								OATMethods.teleportEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
							}
						}
					}
				}
			}
		}	
	}
}
