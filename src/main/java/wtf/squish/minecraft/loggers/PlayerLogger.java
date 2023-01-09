package wtf.squish.minecraft.loggers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Log;

/**
 * Logger for player actions.
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
                .addFragment(event.getMessage(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }

    /**
     * Logs the player running commands.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(event.isCancelled()) return;

        new Log("Player | Command")
                .addFragment(event.getPlayer())
                .addFragment(" ran command ")
                .addFragment(event.getMessage(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }

    /**
     * Logs the player teleporting.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        new Log("Player | Teleport")
                .addFragment(event.getPlayer())
                .addFragment(" teleported from ")
                .addFragment(event.getFrom().toString(), SquishLogs.getLogColor())
                .addFragment(" to ")
                .addFragment(event.getTo().toString(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }

    /**
     * Logs the player changing game mode changing.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        new Log("Player | Gamemode")
                .addFragment(event.getPlayer())
                .addFragment(" set their gamemode to ")
                .addFragment(event.getNewGameMode().name().toLowerCase(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }
}