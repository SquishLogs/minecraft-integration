package wtf.squish.minecraft.loggers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.isCancelled()) return;

        new LogBuilder()
                .setCategory("Player | Teleport")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" teleported from ")
                .addLocationFragment(event.getFrom(), SquishLogs.highlightColor)
                .addTextFragment(" to ")
                .addLocationFragment(event.getTo(), SquishLogs.highlightColor)
                .addTextFragment(".")
                .send();
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if(event.isCancelled()) return;

        new LogBuilder()
                .setCategory("Player | Gamemode")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" had their gamemode changed to ")
                .addTextFragment(event.getNewGameMode().name(), SquishLogs.highlightColor)
                .addTextFragment(".")
                .send();
    }
}
