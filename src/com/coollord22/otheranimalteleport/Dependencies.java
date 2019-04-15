package com.coollord22.otheranimalteleport;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class Dependencies {
	public static boolean hasPermission(Permissible who, String permission) {
        if (who instanceof ConsoleCommandSender)
            return true;
        boolean perm = who.hasPermission(permission);
        if (!perm) {
            Log.logInfo("SuperPerms - permission (" + permission + ") denied for " + who.toString(), Verbosity.HIGHEST);
        } else {
            Log.logInfo("SuperPerms - permission (" + permission + ") allowed for " + who.toString(), Verbosity.HIGHEST);
        }
        return perm;
    }
}
