package wtf.squish.minecraft.loggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import wtf.squish.minecraft.LogBuilder;
import wtf.squish.minecraft.SquishLogs;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SquishLogs.registerPlayer(event.getPlayer());

        new LogBuilder()
                .setCategory("Player | Connect")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" joined the game.")
                .send();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        new LogBuilder()
                .setCategory("Player | Disconnect")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" left the game.")
                .send();
    }
}
