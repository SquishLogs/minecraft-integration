package wtf.squish.minecraft.loggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Fragment;
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
        new Log("Player | Connect")
                .setColor(new Color(25, 25, 25))
                .addFragment(new Fragment(event.getPlayer()))
                .send();
    }
}
