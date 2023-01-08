package wtf.squish.minecraft.loggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Log;

import java.awt.*;

/**
 * Logger for player actions.
 * - Joining
 * - Leaving
 * @author Livaco
 */
public class PlayerLogger implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SquishLogs.registerPlayer(event.getPlayer());

        // Log them
        new Log("Player | Connect")
                .addFragment(event.getPlayer())
                .addFragment("joined the server.")
                .send();
    }
}