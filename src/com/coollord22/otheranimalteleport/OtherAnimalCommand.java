package com.coollord22.otheranimalteleport;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class OtherAnimalCommand implements CommandExecutor {
    private final OtherAnimalTeleport otheranimal;

    public OtherAnimalCommand(OtherAnimalTeleport plugin) {
    	otheranimal = plugin;
    }
    
    private enum OATCommand {
        HELP("help", "otheranimalteleport.player.help"),
        RELOAD("reload", "otheranimalteleport.admin.reloadconfig");

        private String cmdName;
        private String perm;

        private OATCommand(String name, String perm) {
            cmdName = name;
            this.perm = perm;
        }

        public static OATCommand match(String label, String firstArg) {
            boolean arg = false;
            if (label.equalsIgnoreCase("oat"))
                arg = true;
            for (OATCommand cmd : values()) {
                if (arg) {
                    for (String item : cmd.cmdName.split(",")) {
                        if (firstArg.equalsIgnoreCase(item))
                            return cmd;
                    }
                }
            }
            return null;
        }

        public String[] trim(String[] args, StringBuffer name) {
            if (args.length == 0)
                return args;
            if (!args[0].equalsIgnoreCase(cmdName))
                return args;
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            if (name != null)
                name.append(" " + args[0]);
            return newArgs;
        }
    }

    private String getName(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender)
            return "CONSOLE";
        else if (sender instanceof Player)
            return ((Player) sender).getName();
        else
            return "UNKNOWN";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	OATCommand cmd = OATCommand.match(label, args.length >= 1 ? args[0] : "");
        if (cmd == null)
            return false;
        StringBuffer cmdName = new StringBuffer(label);
        args = cmd.trim(args, cmdName);

        if (!checkCommandPermissions(sender, args, cmd))
            return true;

        switch (cmd) {
        case RELOAD:
            cmdReload(sender);
            break;
        case HELP:
        	cmdHelp(sender);
        	break;
        default:
            break;
        }
        return true;
    }

    private boolean checkCommandPermissions(CommandSender sender, String[] args, OATCommand cmd) {
        boolean pass = false;
        if (cmd.perm.isEmpty())
            pass = true;
        else if (Dependencies.hasPermission(sender, cmd.perm))
            pass = true;

        if (!pass)
        	CommonPlugin.sendMessage(true, sender, "You don't have permission for this command.");
        return pass;
    }
    
    private void cmdHelp(CommandSender sender) {
        List<String> result = new ArrayList<String>();
    	result.add("&b---------========== Help Page ==========---------");
    	result.add(" &a/oat reload&7: reload config");
    	result.add("");
    	for(String msg : result) {
    		CommonPlugin.sendMessage(false, sender, ChatColor.translateAlternateColorCodes('&', msg));
    	}
    }
    
    private void cmdReload(CommandSender sender) {
    	OtherAnimalTeleport.config.load(sender);
		CommonPlugin.sendMessage(true, sender, "&aOtherAnimalTeleport config reloaded.");
        Log.logInfo("Config reloaded by " + getName(sender) + ".");
    }
    
	public OtherAnimalTeleport getOtheranimal() {
		return otheranimal;
	}
}
