package com.coollord22.otheranimalteleport;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.coollord22.otheranimalteleport.assets.Log;
import com.coollord22.otheranimalteleport.assets.OATConfig;
import com.coollord22.otheranimalteleport.listeners.OATListeners;

public class OtherAnimalTeleport extends JavaPlugin {
	public static OtherAnimalTeleport plugin;
	public static OATConfig config;
	public Log log = null;

	public boolean enabled;

	public OtherAnimalTeleport() {
		plugin = this;
	}

	@Override
	public void onEnable() { 
		plugin = this;
		registerListeners();
		initConfig();
		initLogger();
		registerCommands();
		plugin.enabled = true;
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
		config = new OATConfig(this);
		config.load(null);
	}

	private void registerListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new OATListeners(this), this);
	}
}
