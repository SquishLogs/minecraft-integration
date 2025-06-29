package wtf.squish.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SquishLogs extends JavaPlugin {
    private static SquishLogs instance;

    @Override
    public void onEnable() {
        log("Loading SquishLogs...");
        instance = this;
    }

    /**
     * Logs a message to the console in the SquishLogs styling.
     * @param message The message to log.
     */
    public static void log(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "SquishLogs >> " + ChatColor.WHITE + message);
    }
}