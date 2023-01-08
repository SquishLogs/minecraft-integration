package wtf.squish.minecraft.loggers;

import com.google.gson.Gson;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Fragment;
import wtf.squish.minecraft.entities.Log;
import wtf.squish.minecraft.util.Output;

import java.awt.*;
import java.util.HashMap;

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