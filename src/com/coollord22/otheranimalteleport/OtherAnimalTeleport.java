package com.coollord22.otheranimalteleport;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class OtherAnimalTeleport extends JavaPlugin implements Listener {
	public static OtherAnimalTeleport plugin;
	public static OtherAnimalConfig config;
	public Log log = null;

	boolean enabled;

	public OtherAnimalTeleport() {
		plugin = this;
	}

	@Override
	public void onEnable() { 
		plugin = this;
		plugin.enabled = true;
		registerListeners();
		initConfig();
		initLogger();
		registerCommands();
	}

	private void registerCommands() {
		this.getCommand("oat").setExecutor(new OtherAnimalCommand(this));
	}

	private void initLogger() {
		// Set plugin name & version, this must be at the start of onEnable
		// Used in log messages throughout
		this.log = new Log(this);
	}

	private void initConfig() {
		getDataFolder().mkdirs();
		config = new OtherAnimalConfig(this);
		config.load(null);
	}

	private void registerListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event) {
		if(enabled) {
			if(!event.isCancelled()) {
				if(event.getPlayer().hasPermission("otheranimalteleport.player.use")) {
					int radius = OtherAnimalConfig.radius;
					
					for(Entity ent : event.getFrom().getWorld().getNearbyEntities(event.getFrom(), radius, radius, radius)) {
						if(ent instanceof Animals) {
							if(((Animals) ent).isLeashed() && ((Animals) ent).getLeashHolder().equals(event.getPlayer())) {
								teleportEnt(ent, event.getFrom(), event.getTo(), event.getPlayer());
							}
							if(ent instanceof Parrot || ent instanceof Wolf || ent instanceof Ocelot) {
								if(((Tameable) ent).isTamed() && ((Tameable) ent).getOwner().equals(event.getPlayer())) {
									teleportEnt(ent, event.getFrom(), event.getTo(), event.getPlayer());
								}
							}
						}
					}
				}
			}
		}
	}

	public void teleportEnt(Entity ent, Location from, Location to, Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5));
				ent.teleport(to);
			}
		}.runTaskLater(plugin, 1);
		new BukkitRunnable() {
			@Override
			public void run() {
				int radius = OtherAnimalConfig.radius;
				List<Entity> nearbyEntities = (List<Entity>) from.getWorld().getNearbyEntities(from, radius, radius, radius);
				for(Entity item : nearbyEntities) {
					if(item instanceof Item)
						if(((Item) item).getItemStack().getType().equals(Material.LEAD))
							item.remove();
				}
				
				((Animals) ent).setLeashHolder(p);
			}
		}.runTaskLater(plugin, 1);
	}
}
