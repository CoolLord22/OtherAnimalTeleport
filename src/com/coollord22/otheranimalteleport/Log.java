// Log.java - Bukkit Plugin Logger Wrapper
// Copyright (C) 2012 Zarius Tularial
//
// This file released under Evil Software License v1.1
// <http://fredrikvold.info/ESL.htm>

package com.coollord22.otheranimalteleport;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Log {
    static ConsoleCommandSender console = null;
    static String pluginName = "";
    static String pluginVersion = "";
    static Logger logger = Logger.getLogger("Minecraft");

    public Log(JavaPlugin plugin) {
        if (plugin != null) {
            pluginName = plugin.getDescription().getName();
            pluginVersion = plugin.getDescription().getVersion();
        }
        if (Bukkit.getServer() == null)
            console = null;
        else
            console = Bukkit.getServer().getConsoleSender();
    }

    // LogInfo & Logwarning - display messages with a standard prefix
    public static void logWarning(String msg) {
        Log.logger.warning("[" + pluginName + ":" + pluginVersion + "] " + msg);
    }

    public static void logInfo(List<String> msgs) {
        if (msgs == null || msgs.isEmpty())
            return;
        
        for (String msg : msgs) {
            logInfo(msg);
        }
    }

    public static void logInfo(String msg) {
        if (OtherAnimalConfig.verbosity.exceeds(Verbosity.NORMAL))
            Log.logger.info("[" + pluginName + ":" + pluginVersion + "] " + msg);
    }

    public static void logInfoNoVerbosity(String msg) {
        Log.logger.info("[" + pluginName + ":" + pluginVersion + "] " + msg);
    }

    /**
     * dMsg - used for debug messages that are expected to be removed before
     * distribution
     * 
     * @param msg
     */
    public static void dMsg(String msg) {
        // Deliberately doesn't check gColorLogMessage as I want these messages
        // to stand out in case they
        // are left in by accident
        if (OtherAnimalConfig.verbosity.exceeds(Verbosity.HIGHEST))
            if (console != null && OtherAnimalConfig.gColorLogMessages) {
                console.sendMessage(ChatColor.RED + "[" + pluginName + ":" + pluginVersion + "] " + ChatColor.RESET + msg);
            } else {
                Log.logger.info("[" + pluginName + ":" + pluginVersion + "] " + msg);
            }
    }

    // LogInfo & LogWarning - if given a level will report the message
    // only for that level & above
    public static void logInfo(String msg, Verbosity level) {
        if (OtherAnimalConfig.verbosity.exceeds(level)) {
            if (console != null && OtherAnimalConfig.gColorLogMessages) {
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
                    col = ChatColor.RESET;
                    break;
                case LOW:
                    col = ChatColor.RESET;
                    break;
                default:
                    break;
                }
                console.sendMessage(col + "[" + pluginName + ":" + pluginVersion + "] " + ChatColor.RESET + msg);
            } else {
                Log.logger.info("[" + pluginName + ":" + pluginVersion + "] " + msg);
            }
        }
    }

    public static void logWarning(String msg, Verbosity level) {
        if (OtherAnimalConfig.verbosity.exceeds(level))
            logWarning(msg);
    }

    // TODO: This is only for temporary debug purposes.
    public static void stackTrace() {
        if (OtherAnimalConfig.verbosity.exceeds(Verbosity.EXTREME))
            Thread.dumpStack();
    }
}
