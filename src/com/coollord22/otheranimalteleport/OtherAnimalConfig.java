package com.coollord22.otheranimalteleport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import com.coollord22.otheranimalteleport.Verbosity;
import com.coollord22.otheranimalteleport.CommonPlugin;

public class OtherAnimalConfig {
	private final OtherAnimalTeleport parent;
	
	protected static Verbosity verbosity = Verbosity.NORMAL;
	public static boolean gColorLogMessages;
	public static int radius;

	public OtherAnimalConfig(OtherAnimalTeleport instance) {
		parent = instance;
	}
	
	public void load(CommandSender sender) {
        List<String> result = new ArrayList<String>();
		try { 
            firstRun();
			loadConfig();
		} catch (FileNotFoundException e) {
            if (verbosity.exceeds(Verbosity.HIGH)) e.printStackTrace();
            result.add("Config file not found!");
            result.add("The error was:\n" + e.toString());
            result.add("You can fix the error and reload with /orr.");
            sendMessage(sender, result);
        } catch (IOException e) {
            if (verbosity.exceeds(Verbosity.HIGH)) e.printStackTrace();
            result.add("There was an IO error which has forced OtherDrops to abort loading!");
            result.add("The error was:\n" + e.toString());
            result.add("You can fix the error and reload with /orr.");
            sendMessage(sender, result);
        } catch (InvalidConfigurationException e) {
            if (verbosity.exceeds(Verbosity.HIGH)) e.printStackTrace();
            result.add("Config is invalid!");
            result.add("The error was:\n" + e.toString());
            result.add("You can fix the error and reload with /orr.");
            sendMessage(sender, result);
        } catch (NullPointerException e) {
            result.add("Config load failed!");
            result.add("The error was:\n" + e.toString());
            if (verbosity.exceeds(Verbosity.NORMAL)) e.printStackTrace();
            result.add("Please try the latest version & report this issue to the developer if the problem remains.");
            sendMessage(sender, result);
        } catch (Exception e) {
            if (verbosity.exceeds(Verbosity.HIGH)) e.printStackTrace();
            result.add("Config load failed!  Something went wrong.");
            result.add("The error was:\n" + e.toString());
            result.add("If you can fix the error, reload with /orr.");
            sendMessage(sender, result);
        }
	}
	 
	private void sendMessage(CommandSender sender, List<String> result) {
	        if (sender != null) {
	            sender.sendMessage(result.toArray(new String[0]));
	        }
	        Log.logInfo(result);
	    }
	 
    private void firstRun() throws Exception {
        List<String> files = new ArrayList<String>();
        files.add("config.yml");
        
        for (String filename : files) {
            File file = new File(parent.getDataFolder(), filename);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                copy(parent.getResource(filename), file);
            }
        }
    }
    
    public void loadConfig() throws FileNotFoundException, IOException, InvalidConfigurationException {
        String filename = "config.yml";
        File global = new File(parent.getDataFolder(), filename);
        YamlConfiguration globalConfig = YamlConfiguration.loadConfiguration(global);
        // Make sure config file exists (even for reloads - it's possible this
        // did not create successfully or was deleted before reload)
        if (!global.exists()) {
            try {
                global.createNewFile();
                Log.logInfo("Created a config file " + parent.getDataFolder() + "\\" + filename + ", please edit it!");
                globalConfig.save(global);
            } catch (IOException ex) {
                Log.logWarning(parent.getDescription().getName() + ": could not generate " + filename + ". Are the file permissions OK?");
            } catch (Exception e) {
                Log.logWarning(parent.getDescription().getName() + ": could not generate " + filename + ". Are the file permissions OK?");
			}
        }
        
        // Load in the values from the configuration file
        globalConfig.load(global);

        verbosity = CommonPlugin.getConfigVerbosity(globalConfig);
        gColorLogMessages = globalConfig.getBoolean("color_log_messages", true);
        radius = globalConfig.getInt("radius", 2);

        Log.logInfo("Loaded global config (" + global + "), keys found: " + " (verbosity=" + verbosity + ")", Verbosity.HIGHEST);
    }

    public static Verbosity getVerbosity() {
        return verbosity;
    }

    public static void setVerbosity(Verbosity verbosity) {
        OtherAnimalConfig.verbosity = verbosity;
    }
    
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
