package com.coollord22.otheranimalteleport;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OtherAnimalCommand implements TabExecutor {
	private final OtherAnimalTeleport plugin;

	public OtherAnimalCommand(OtherAnimalTeleport plugin) {
		this.plugin = plugin;
		plugin.getCommand("oat").setExecutor(this);
		plugin.getCommand("oat").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("oat") && args.length > 0) {
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("otheranimalteleport.admin.reloadconfig")) {
				cmdReload(sender);
				return true;
			} else if(args[0].equalsIgnoreCase("help") && sender.hasPermission("otheranimalteleport.player.help")) {
				cmdHelp(sender);
				return true;
			}
        }
        return false;
    }

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(command.getName().equalsIgnoreCase("oat")) {
			if(args.length == 1) {
				return StringUtil.copyPartialMatches(args[0], Arrays.asList("reload", "help"), new ArrayList<>());
			}
		}
		return null;
	}

	private String getName(CommandSender sender) {
		if (sender instanceof ConsoleCommandSender)
			return "CONSOLE";
		else if (sender instanceof Player)
			return sender.getName();
		else
			return "UNKNOWN";
	}

	private void cmdHelp(CommandSender sender) {
		List<String> result = new ArrayList<>();
		result.add("&b---------========== Help Page ==========---------");
		result.add(" &a/oat help&7: see this help page");
		result.add(" &a/oat reload&7: reload config");
		result.add("");
		for(String msg : result) {
			plugin.common.sendMessage(false, sender, ChatColor.translateAlternateColorCodes('&', msg));
		}
	}

	private void cmdReload(CommandSender sender) {
		plugin.config.load(sender);
		plugin.common.sendMessage(true, sender, "&aOtherAnimalTeleport config reloaded.");
		plugin.log.logInfo("Config reloaded by " + getName(sender) + ".");
	}
}
