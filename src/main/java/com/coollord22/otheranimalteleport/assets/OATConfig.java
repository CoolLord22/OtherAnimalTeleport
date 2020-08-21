package com.coollord22.otheranimalteleport.assets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class OATConfig {
	private final OtherAnimalTeleport plugin;

	protected static Verbosity 		verbosity = Verbosity.NORMAL;
	public boolean 					gColorLogMessages;
	public boolean					globalUpdateChecking;
	public boolean 					usePrefix = true;

	public int 						radius;

	public List<Set<World>> 		worldGroup = new ArrayList<Set<World>>();
	public List<EntityType> 		allowedEnts = new ArrayList<EntityType>();
	public HashMap<World, Set<ProtectedRegion>> blockedRegions = new HashMap<>();

	public String 					prefix;
	public String 					failedTeleportMessage;

	public OATConfig(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
	}

	public void load(CommandSender sender) {
		List<String> result = new ArrayList<String>();
		try { 
			firstRun();
			loadConfig();
		} catch (FileNotFoundException e) {
			if (verbosity.exceeds(Verbosity.HIGH)) e.printStackTrace();
			result.add("Config file not found!");
			result.add("The error was:\n" + e.toString());
			result.add("You can fix the error and reload with /orr.");
			sendMessage(sender, result);
		} catch (IOException e) {
			if (verbosity.exceeds(Verbosity.HIGH)) e.printStackTrace();
			result.add("There was an IO error which has forced OtherDrops to abort loading!");
			result.add("The error was:\n" + e.toString());
			result.add("You can fix the error and reload with /orr.");
			sendMessage(sender, result);
		} catch (InvalidConfigurationException e) {
			if (verbosity.exceeds(Verbosity.HIGH)) e.printStackTrace();
			result.add("Config is invalid!");
			result.add("The error was:\n" + e.toString());
			result.add("You can fix the error and reload with /orr.");
			sendMessage(sender, result);
		} catch (NullPointerException e) {
			result.add("Config load failed!");
			result.add("The error was:\n" + e.toString());
			if (verbosity.exceeds(Verbosity.NORMAL)) e.printStackTrace();
			result.add("Please try the latest version & report this issue to the developer if the problem remains.");
			sendMessage(sender, result);
		} catch (Exception e) {
			if (verbosity.exceeds(Verbosity.HIGH)) e.printStackTrace();
			result.add("Config load failed!  Something went wrong.");
			result.add("The error was:\n" + e.toString());
			result.add("If you can fix the error, reload with /orr.");
			sendMessage(sender, result);
		}
	}

	private void sendMessage(CommandSender sender, List<String> result) {
		if (sender != null) {
			sender.sendMessage(result.toArray(new String[0]));
		}
		plugin.log.logInfo(result);
	}

	private void firstRun() throws Exception {
		List<String> files = new ArrayList<String>();
		files.add("config.yml");

		for (String filename : files) {
			File file = new File(plugin.getDataFolder(), filename);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				copy(plugin.getResource(filename), file);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadConfig() throws FileNotFoundException, IOException, InvalidConfigurationException {
		String filename = "config.yml";
		File global = new File(plugin.getDataFolder(), filename);
		YamlConfiguration globalConfig = YamlConfiguration.loadConfiguration(global);
		// Make sure config file exists (even for reloads - it's possible this
		// did not create successfully or was deleted before reload)
		if (!global.exists()) {
			try {
				global.createNewFile(); 
				plugin.log.logInfo("Created a config file " + plugin.getDataFolder() + "\\" + filename + ", please edit it!");
				globalConfig.save(global);
			} catch (IOException ex) {
				plugin.log.logWarning(plugin.getDescription().getName() + ": could not generate " + filename + ". Are the file permissions OK?");
			} catch (Exception e) {
				plugin.log.logWarning(plugin.getDescription().getName() + ": could not generate " + filename + ". Are the file permissions OK?");
			}
		}

		// Load in the values from the configuration file
		globalConfig.load(global);

		worldGroup.clear();
		allowedEnts.clear();
		blockedRegions.clear();

		verbosity = OATCommon.getConfigVerbosity(globalConfig);
		globalUpdateChecking = globalConfig.getBoolean("update_checker", true);
		gColorLogMessages = globalConfig.getBoolean("color_log_messages", true);
		radius = globalConfig.getInt("radius", 2);

		usePrefix = globalConfig.getBoolean("use_prefix", true);
		prefix = globalConfig.getString("prefix", "&7[&aOtherAnimalTeleport&7] ");

		//messages
		failedTeleportMessage = globalConfig.getString("fail_teleport", "&7An entity could not be teleported and is located near (&c%x&7, &c%y&7, &c%z&7).");

		if(globalConfig.contains("blocked_regions")) {
			for(String input : globalConfig.getStringList("blocked_regions")) {
				String[] splitRegion = input.split("@", 2);
				if(splitRegion.length != 2) {
					plugin.log.logWarning("Improper world@region formatting, please check input again (" + input + ")! Skipping...");
					continue;
				}
				
				World regionWorld = Bukkit.getWorld(splitRegion[0]);
				if(regionWorld == null) {
					plugin.log.logWarning("Unrecognized world for blocked_regions, please check world name (" + input + ")! Skipping...");
					continue;
				}
				
				RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(regionWorld));
				ProtectedRegion regionToAdd = regionManager.getRegion(splitRegion[1]);
				if(regionToAdd == null) {
					plugin.log.logWarning("Unrecognized region for blocked_regions, please check region name (" + input + ")! Skipping...");
					continue;
				}
				
				Set<ProtectedRegion> regionList = new HashSet<>();
				if(blockedRegions.containsKey(regionWorld)) {
					regionList = blockedRegions.get(regionWorld);
				}
				regionList.add(regionToAdd);
				blockedRegions.put(regionWorld, regionList);

			}
		}

		if(globalConfig.contains("world_groups")) {
			for(Object input : globalConfig.getList("world_groups")) {
				Set<World> worldList = new HashSet<World>();
				for(String inputWorld : (ArrayList<String>)input) {
					boolean foundMatch = false;
					for(World knownWorld : Bukkit.getWorlds()) {
						if(knownWorld.getName().equalsIgnoreCase(inputWorld)) {
							foundMatch = true;
							worldList.add(knownWorld);
						}
					}
					if(!foundMatch) {
						plugin.log.logWarning("Unrecognized world name specified (" + inputWorld + ")! Skipping...");
					}
				}
				worldGroup.add(worldList);
			}
		}

		if(globalConfig.contains("allowed_entities")) {
			for(String input : globalConfig.getStringList("allowed_entities")) {
				if(input.equals("ANY") || input.equals("ALL")) {
					for(EntityType entType : EntityType.values()) {
						if(entType.isAlive() && !(entType.equals(EntityType.valueOf("PLAYER"))))
							allowedEnts.add(entType);
					}
				} 
				else {
					boolean foundMatch = false;
					for(EntityType entType : EntityType.values()) {
						if(input.equalsIgnoreCase(entType.toString())) {
							foundMatch = true;
							allowedEnts.add(entType);
						}
					}
					if(!foundMatch) {
						plugin.log.logWarning("Unrecognized entity type (" + input + ")! Skipping...");
					}
				}
			}
		}

		plugin.log.logInfo("Loaded global config (" + global + ") with (verbosity=" + verbosity + ")", Verbosity.HIGHEST);
	}

	public static Verbosity getVerbosity() {
		return verbosity;
	}

	public static void setVerbosity(Verbosity verbosity) {
		OATConfig.verbosity = verbosity;
	}

	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
