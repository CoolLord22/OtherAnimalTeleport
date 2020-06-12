package com.coollord22.otheranimalteleport.assets;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class OATCommon {
	public static String prefix = "&7[&aOtherAnimalTeleport&7] ";
	
    static public Verbosity getConfigVerbosity(YamlConfiguration config) {
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
	
    public static void sendMessage(boolean usePrefix, CommandSender s, String msg) {
    	if(usePrefix)
    		s.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
    	else
    		s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
    
    static public <E extends Enum<E>> E enumValue(Class<E> clazz, String name) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException e) {
        }
        return null;
    }
}
