package wtf.squish.minecraft.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.swing.text.Utilities;

/**
 * Handles logging to the console.
 * @author Livaco
 */
public class Output {
    /**
     * Prints a message to the console.
     * @param message The message to send.
     */
    public static void print(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "SquishLogs >> " + ChatColor.WHITE + message);
    }
}
