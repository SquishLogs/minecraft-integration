package wtf.squish.minecraft.loggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
                .addFragment(new Fragment(event.getPlayer()))
                .addFragment(new Fragment("joined the server.", null))
                .addFragment(new Fragment("(he also smells)", new Color(255, 50, 50)))
                .send();
    }
}
