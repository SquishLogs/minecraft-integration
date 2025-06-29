package wtf.squish.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SquishLogs extends JavaPlugin {
    private static SquishLogs instance;
    private static int minecraftMajor;
    private static int minecraftMinor;

    @Override
    public void onEnable() {
        log("Loading SquishLogs...");
        instance = this;

        // Start figuring out the version stuff
        String version = Bukkit.getBukkitVersion().split("-")[0];
        String[] splitVersion = version.split("([.])");
        minecraftMajor = Integer.parseInt(splitVersion[0]);
        minecraftMinor = Integer.parseInt(splitVersion[1]);
        log("Detected minecraft version " + minecraftMajor + "." + minecraftMinor);
    }

    /**
     * Logs a message to the console in the SquishLogs styling.
     * @param message The message to log.
     */
    public static void log(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "SquishLogs >> " + ChatColor.WHITE + message);
    }
}