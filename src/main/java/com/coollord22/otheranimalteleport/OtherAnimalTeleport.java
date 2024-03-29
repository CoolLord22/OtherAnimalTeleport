package com.coollord22.otheranimalteleport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.coollord22.otheranimalteleport.assets.Log;
import com.coollord22.otheranimalteleport.assets.OATCommon;
import com.coollord22.otheranimalteleport.assets.OATConfig;
import com.coollord22.otheranimalteleport.assets.Updater;
import com.coollord22.otheranimalteleport.assets.Verbosity;
import com.coollord22.otheranimalteleport.listeners.OATListeners;

import net.md_5.bungee.api.ChatColor;

public class OtherAnimalTeleport extends JavaPlugin {
	public static OtherAnimalTeleport plugin;
	public OATConfig config;
	public OATCommon common;
	public Updater updateChecker;
	public Log log = null;
	public Metrics metrics = null;
	public final int pluginID = 8020;

	public boolean enabled;
	public boolean toUseTickets = false;

	public OtherAnimalTeleport() {
		plugin = this;
	}

	@Override
	public void onEnable() { 
		plugin = this;
		initCommon();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			initConfig();
			registerCommands();
			registerListeners();
			if(plugin.config.globalUpdateChecking) {
				updateChecker = new Updater(plugin);
				updateChecker.checkForUpdate(null);
			}
			plugin.log.logInfo(ChatColor.GREEN + "AnimalTeleport has been enabled!", Verbosity.LOW);
			plugin.enabled = true;

			String[] serverVersion = (Bukkit.getBukkitVersion().split("-")[0]).split("\\.");
			if(Integer.parseInt(serverVersion[0]) >= 1)
				if(Integer.parseInt(serverVersion[1]) >= 14) {
					toUseTickets = true;
					plugin.log.logInfo(ChatColor.RED + "Found server version " + serverVersion[0] + "." + serverVersion[1] + " >= 1.14, using chunk tickets!", Verbosity.HIGH);
				}
		}, 1L);
		writeNames(EntityType.class);
	}


	private void initCommon() {
		// Set plugin name & version, this must be at the start of onEnable
		// Used in log messages throughout
		this.log = new Log(this);
		this.common = new OATCommon(this);
	}

	private void initConfig() {
		getDataFolder().mkdirs();
		config = new OATConfig(this);
		config.load(null);
	}

	private void registerListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new OATListeners(this), this);
	}

	private void registerCommands() {
		new OtherAnimalCommand(this);
	}

	public void initMetrics() {
		if(metrics != null)
			metrics.shutdown();
		metrics = new Metrics(plugin, pluginID);
		Map<String, Integer> allowedEnts = new HashMap<>();
		Map<String, Integer> deniedEnts = new HashMap<>();
		for(Map.Entry<EntityType, Boolean> entry : config.entityMap.entrySet()) {
			if(entry.getValue())
				allowedEnts.put(entry.getKey().toString(), 1);
			else
				deniedEnts.put(entry.getKey().toString(), 1);
		}
		metrics.addCustomChart(new AdvancedPie("allowed_entities", () -> allowedEnts));
		metrics.addCustomChart(new AdvancedPie("denied_entities", () -> deniedEnts));
	}

	public static void writeNames(Class<? extends Enum<?>> e) {
		writeNames(e.getSimpleName(), e);
	}

	public static void writeNames(String filename, Class<? extends Enum<?>> e) {
		List<String> list = new ArrayList<>();

		for (Enum<?> stuff : e.getEnumConstants()) {
			list.add(stuff.toString());
		}

		try {
			BufferedWriter out;
			File folder = plugin.getDataFolder();
			File configFile = new File(folder.getAbsolutePath() + File.separator + "known_" + filename + ".txt");
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
			out = new BufferedWriter(new FileWriter(configFile));
			Collections.sort(list);
			for(String mat : list)
				out.write(mat + "\n");
			out.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

}
