// Log.java - Bukkit Plugin Logger Wrapper
// Copyright (C) 2012 Zarius Tularial
//
// This file released under Evil Software License v1.1
// <http://fredrikvold.info/ESL.htm>

package com.coollord22.otheranimalteleport.assets;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import com.coollord22.otheranimalteleport.OtherAnimalTeleport;

public class Log {
    static ConsoleCommandSender console = null;
    static String pluginName = "";
    static final Logger logger = Logger.getLogger("Minecraft");
    private final OtherAnimalTeleport plugin;

    public Log(OtherAnimalTeleport plugin) {
    	this.plugin = plugin;
        if (plugin != null) {
            pluginName = plugin.getDescription().getName();
        }
        console = Bukkit.getServer().getConsoleSender();
    }

    // LogInfo & Logwarning - display messages with a standard prefix
    public void logWarning(String msg) {
        Log.logger.warning("[" + pluginName + "] " + msg);
    }

    public void logInfo(List<String> msgs) {
        if (msgs == null || msgs.isEmpty())
            return;
        
        for (String msg : msgs) {
            logInfo(msg);
        }
    }

    public void logInfo(String msg) {
        if (OATConfig.verbosity.exceeds(Verbosity.NORMAL))
            Log.logger.info("[" + pluginName + "] " + msg);
    }

    /**
     * dMsg - used for debug messages that are expected to be removed before
     * distribution
     *
     * @param msg - msg to log
     */
    public void dMsg(String msg) {
        if (OATConfig.verbosity.exceeds(Verbosity.HIGHEST))
            if (console != null && plugin.config.gColorLogMessages) {
                console.sendMessage(ChatColor.RED + "[" + pluginName + "] " + ChatColor.RESET + msg);
            } else {
                Log.logger.info("[" + pluginName + "] " + msg);
            }
    }

    // LogInfo & LogWarning - if given a level will report the message
    // only for that level & above
    public void logInfo(String msg, Verbosity level) {
        if (OATConfig.verbosity.exceeds(level)) {
            if (console != null && plugin.config.gColorLogMessages) {
                ChatColor col = ChatColor.GREEN;
                switch (level) {
                case EXTREME:
                    col = ChatColor.GOLD;
                    break;
                case HIGHEST:
                    col = ChatColor.YELLOW;
                    break;
                case HIGH:
                    col = ChatColor.AQUA;
                    break;
                case NORMAL:
                    case LOW:
                        col = ChatColor.RESET;
                    break;
                    default:
                    break;
                }
                console.sendMessage(col + "[" + pluginName + "] " + ChatColor.RESET + msg);
            } else {
                Log.logger.info("[" + pluginName + "] " + msg);
            }
        }
    }

    public void logWarning(String msg, Verbosity level) {
        if (OATConfig.verbosity.exceeds(level))
            logWarning(msg);
    }
}
