package wtf.squish.minecraft.loggers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import wtf.squish.minecraft.GameLog;
import wtf.squish.minecraft.SquishLogs;

import java.util.ArrayList;
import java.util.List;

public class PlayerLogger implements Listener {
    private List<Player> playersThatCleared;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SquishLogs.registerPlayer(event.getPlayer());

        new GameLog()
                .setCategory("Player | Connect")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" joined the game.")
                .send();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        new GameLog()
                .setCategory("Player | Disconnect")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" left the game.")
                .send();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.isCancelled())
            return;
        if(event.getFrom() == event.getTo())
            // This can sometimes happen with TPS lagspikes
            return;

        new GameLog()
                .setCategory("Player | Teleport")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" teleported from ")
                .addLocationFragment(event.getFrom(), true)
                .addTextFragment(" to ")
                .addLocationFragment(event.getTo(), true)
                .addTextFragment(".")
                .send();
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if(event.isCancelled())
            return;

        new GameLog()
                .setCategory("Player | Gamemode")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" had their gamemode changed to ")
                .addTextFragment(event.getNewGameMode().name(), true)
                .addTextFragment(".")
                .send();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.isCancelled())
            return;
        // I'm trusting you https://www.spigotmc.org/threads/is-there-an-aftertradeevent.507855/#post-4172939
        if(event.getInventory().getType() != InventoryType.MERCHANT)
            return;
        if(event.getSlot() != 2)
            return;
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
        if(player == null)
            return;

        if(event.getCurrentItem() != null) {
            new GameLog()
                    .setCategory("Player | Villager Trade")
                    .addPlayerFragment(player)
                    .addTextFragment(" traded with a villager receiving ")
                    .addItemFragment(event.getCurrentItem(), true)
                    .addTextFragment(".")
                    .send();
        } else {
            new GameLog()
                    .setCategory("Player | Villager Trade")
                    .addPlayerFragment(player)
                    .addTextFragment(" traded with a villager to get an unknown item.")
                    .send();
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        if(event.getAdvancement().getDisplay() == null)
            return;
        if(event.getAdvancement().getDisplay().isHidden())
            return;
        String display = event.getAdvancement().getKey().getKey();
        String description = "Unknown (Minecraft version does not support this)";

        if(SquishLogs.minecraftMajor > 1 || (SquishLogs.minecraftMinor >= 19 && SquishLogs.minecraftMajor == 1)) {
            display = event.getAdvancement().getDisplay().getTitle();
            description = event.getAdvancement().getDisplay().getDescription();
        }

        new GameLog()
                .setCategory("Player | Advancement")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" earned the advancement ")
                .addTextFragment(display, true)
                .withMetadata("Description", description)
                .addTextFragment(".")
                .send();
    }

    @EventHandler
    public void onEntityPotionEffectEvent(EntityPotionEffectEvent event) {
        if(event.isCancelled()) return;
        if(!(event.getEntity() instanceof Player player)) return;

        switch(event.getAction()) {
            case ADDED, CHANGED -> {
                if(event.getNewEffect() == null) return;
                new GameLog()
                        .setCategory("Player | Potion")
                        .addPlayerFragment(player)
                        .addTextFragment(" was given the effect of ")
                        // I'm aware getName is deprecated, but I don't want *client side* translations since all this code only runs serverside
                        // Since Squish only supports English, thats the only language I want to use
                        // Spigot's really making it hard to make me want to make minecraft plugins with shit like this lol
                        .addTextFragment(event.getNewEffect().getType().getName(), true)
                        .addTextFragment(".")
                        .send();
            }
            case CLEARED -> {
                // Spigot calls this for EVERY SINGLE POTION THAT GOT CLEARED
                // So, this is what I did...
                if(playersThatCleared != null) {
                    if(playersThatCleared.contains(player)) return;
                } else {
                    playersThatCleared = new ArrayList<>();
                }
                playersThatCleared.add(player);
                Bukkit.getScheduler().scheduleSyncDelayedTask(SquishLogs.getInstance(), () -> {
                    playersThatCleared.remove(player);
                    if(playersThatCleared.isEmpty()) {
                        playersThatCleared = null;
                    }
                }, 1);

                new GameLog()
                        .setCategory("Player | Potion")
                        .addPlayerFragment(player)
                        .addTextFragment(" had their effects cleared.")
                        .send();
            }
            case REMOVED -> {
                if(event.getOldEffect() == null) return;
                new GameLog()
                        .setCategory("Player | Potion")
                        .addPlayerFragment(player)
                        .addTextFragment(" had their ")
                        .addTextFragment(event.getOldEffect().getType().getName(), true)
                        .addTextFragment(" effect removed.")
                        .send();
            }
        }
    }
}
