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
		if(plugin.config.blockedRegions.containsKey(toWorld)) {
			for(ProtectedRegion region : plugin.config.blockedRegions.get(toWorld)) {
				if(region.contains(BlockVector3.at(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ()))) {
					plugin.log.logInfo("Player teleporting into a blocked region; ignoring entity checks.", Verbosity.HIGHEST);
					return;
				}
			}
		}
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
		if(!sameGroup)
			plugin.log.logInfo("From and To worlds were not found in same group, ending checks.", Verbosity.HIGH);
		if(plugin.enabled && !event.isCancelled() && sameGroup) {
			plugin.log.logInfo("From and To worlds were in same group, allowing permission check.", Verbosity.HIGHEST);
			if(event.getPlayer().hasPermission("otheranimalteleport.player.use")) {
				plugin.log.logInfo("Player permission check passed, gathering nearby entities.", Verbosity.HIGHEST);
				int radius = plugin.config.radius;
				boolean toSendError = false;
				boolean toSendLeft = false;
				for(Entity ent : event.getFrom().getWorld().getNearbyEntities(event.getFrom(), radius, radius, radius)) {
					plugin.log.logInfo("Found an entity to teleport: " + ent.getType() + " . Checking if it is allowed.", Verbosity.HIGHEST);
					if(plugin.config.allowedEnts.contains(ent.getType())) {
						plugin.log.logInfo("Entity check passed, seeing if player has leashed permissions.", Verbosity.HIGHEST);
						if(ent instanceof LivingEntity && event.getPlayer().hasPermission("otheranimalteleport.player.teleportleashed")) {
							if(((LivingEntity) ent).isLeashed() && ((LivingEntity) ent).getLeashHolder().equals(event.getPlayer())) {
								try {
									plugin.log.logInfo("Attempting to send leashed entity: " + ent.getType() + ".", Verbosity.HIGHEST);
									OATMethods.teleportLeashedEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
									continue;
								} catch(Exception e) {
									toSendError = true;
									continue;
								}
							}
							toSendLeft  = true;
						}
						if(ent instanceof Tameable && event.getPlayer().hasPermission("otheranimalteleport.player.teleportpets")) {
							if(((Tameable) ent).isTamed() && ((Tameable) ent).getOwner() != null && ((Tameable) ent).getOwner().equals(event.getPlayer())) {
								if(ent instanceof Sittable && !((Sittable) ent).isSitting()) {
									try {
										plugin.log.logInfo("Attempting to send pet entity: " + ent.getType() + ".", Verbosity.HIGHEST);
										OATMethods.teleportEnt(ent, event.getFrom(), event.getTo(), event.getPlayer(), plugin);
										continue;
									} catch(Exception e) {
										toSendError = true;
										continue;
									}
								}
							}
					}
					}
				}
			}
	}
}
