package com.coollord22.otheranimalteleport.assets;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import com.coollord22.otheranimalteleport.OtherAnimalTeleport;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.text.DecimalFormat;
import java.util.Set;

public class OATCommon {
	private final OtherAnimalTeleport plugin;
    private final DecimalFormat df = new DecimalFormat("#.#");

	public OATCommon(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
	}
	
    public static Verbosity getConfigVerbosity(YamlConfiguration config) {
        String verb_string = config.getString("verbosity", "normal");
        if (verb_string.equalsIgnoreCase("low"))
            return Verbosity.LOW;
        else if (verb_string.equalsIgnoreCase("high"))
            return Verbosity.HIGH;
        else if (verb_string.equalsIgnoreCase("highest"))
            return Verbosity.HIGHEST;
        else if (verb_string.equalsIgnoreCase("extreme"))
            return Verbosity.EXTREME;
        else
            return Verbosity.NORMAL;
    }
	
    public void sendMessage(boolean usePrefix, CommandSender s, String msg) {
    	if(usePrefix)
    		s.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + msg));
    	else
    		s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public void sendMessage(boolean usePrefix, String msg, PlayerTeleportEvent event) {
        sendMessage(usePrefix, event.getPlayer(), msg
                .replaceAll("%x", df.format(event.getFrom().getBlockX()))
                .replaceAll("%y", df.format(event.getFrom().getBlockY()))
                .replaceAll("%z", df.format(event.getFrom().getBlockZ())));
    }

    public boolean checkWorldGroup(PlayerTeleportEvent event) {
        World fromWorld = event.getFrom().getWorld();
        World toWorld = event.getTo().getWorld();

        // Blocked region check
        if(plugin.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            if(plugin.config.blockedRegions.containsKey(toWorld)) {
                for(ProtectedRegion region : plugin.config.blockedRegions.get(toWorld)) {
                    if(region.contains(BlockVector3.at(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ()))) {
                        plugin.log.logInfo("Player teleporting into a blocked region; ignoring entity checks.", Verbosity.HIGHEST);
                        return false;
                    }
                }
            }
        }

        // World Group checks
        if(fromWorld.equals(toWorld)) {
            return true;
        } else {
            for(Set<World> worldList : plugin.config.worldGroup) {
                if(worldList.contains(fromWorld) && worldList.contains(toWorld)) {
                    return true;
                }
            }
        }
        plugin.log.logInfo("From and To worlds were not found in same group, ending checks.", Verbosity.HIGH);
        return false;
    }
}
