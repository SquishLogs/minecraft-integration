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
    /**
     * Logs the player joining the server, as well as calls for them to be registered.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SquishLogs.registerPlayer(event.getPlayer());

        // Log them
        new Log("Player | Connect")
                .addFragment(event.getPlayer())
                .addFragment(" joined the server.")
                .send();
    }

    /**
     * Logs the player leaving the server.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        new Log("Player | Disconnect")
                .addFragment(event.getPlayer())
                .addFragment(" left the server.")
                .send();
    }

    /**
     * Logs the player chatting.
     * @param event The event.
     */
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        new Log("Player | Chat")
                .addFragment(event.getPlayer())
                .addFragment(" said in chat ")
                .addFragment(event.getMessage(), Color.MAGENTA)
                .addFragment(".")
                .send();
    }
}