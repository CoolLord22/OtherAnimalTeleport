package com.coollord22.otheranimalteleport.assets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.coollord22.otheranimalteleport.OtherAnimalTeleport;

public class Updater {

	private final OtherAnimalTeleport plugin;
	private String localPluginVersion;
	private String spigotPluginVersion;

	private static final int ID = 63497;
	private static final String ERR_MSG = ChatColor.RED + "Update checking faile!";

	public Updater(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
		this.localPluginVersion = plugin.getDescription().getVersion();
	}

	public void checkForUpdate(Player p) {
		List<String> UPDATE_MSG = new ArrayList<String>();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			//Request current spigot version
			try {
				final HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + ID).openConnection();
				connection.setRequestMethod("GET");
				spigotPluginVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
			} catch(final IOException e) {
				UPDATE_MSG.add(ERR_MSG);
				e.printStackTrace();
			}

			if (localPluginVersion.equals(spigotPluginVersion)) {
				UPDATE_MSG.add(ChatColor.GREEN + "Hooray! You're running the latest version!");
			}

			else if(spigotPluginVersion.contains("b")) {
				Double spigot = Double.parseDouble(spigotPluginVersion.substring(0, spigotPluginVersion.indexOf("-b")));
				Double local = Double.parseDouble(localPluginVersion.substring(0, localPluginVersion.indexOf("-b")));
				if(spigot > local) {
					UPDATE_MSG.add(ChatColor.YELLOW + "A new release is available! " + ChatColor.RED + "Latest Version: " + ChatColor.GREEN + spigotPluginVersion + ChatColor.RED + " Your Version: " + ChatColor.GREEN + localPluginVersion);
					UPDATE_MSG.add(ChatColor.YELLOW + "Please download latest version from: " + ChatColor.GREEN + "https://www.spigotmc.org/resources/63497/updates");
				} else if(spigot.equals(local) && Integer.parseInt(spigotPluginVersion.substring(spigotPluginVersion.indexOf("b") + 1)) > Integer.parseInt(localPluginVersion.substring(localPluginVersion.indexOf("b") + 1))) {
					UPDATE_MSG.add(ChatColor.YELLOW + "A new build is available! " + ChatColor.RED + "Latest Version: " + ChatColor.GREEN + spigotPluginVersion + ChatColor.RED + " Your Version: " + ChatColor.GREEN + localPluginVersion);
					UPDATE_MSG.add(ChatColor.YELLOW + "Please download latest version from: " + ChatColor.GREEN + "https://www.spigotmc.org/resources/63497/updates");
				}
			}
			
			if(!UPDATE_MSG.isEmpty()) {
				for(String line : UPDATE_MSG) {
					if(p == null) 
						if(!line.contains("Hooray")) 
							plugin.log.logWarning(ChatColor.stripColor(line));
					if(p != null)
						plugin.common.sendMessage(plugin.config.usePrefix, p, line);
				}
			}
		});
	}
}
