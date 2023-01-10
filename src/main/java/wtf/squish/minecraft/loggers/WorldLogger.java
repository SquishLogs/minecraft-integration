package wtf.squish.minecraft.loggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import wtf.squish.minecraft.entities.Log;
import wtf.squish.minecraft.util.Output;

/**
 * Logs events that happen in general around the world.
 * @author Livaco
 */
public class WorldLogger implements Listener {
    /**
     * Logs when a player drops an item.
     * @param event The item.
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        new Log("World | Dropped Item")
                .addFragment(event.getPlayer())
                .addFragment(" dropped ")
                .addFragment(event.getItemDrop().getItemStack().getAmount(), true)
                .addFragment(" ")
                .addFragment(event.getItemDrop().getName(), true)
                .addFragment(" at ")
                .addFragment(event.getItemDrop().getLocation(), true)
                .addFragment(".")
                .send();
    }

    /**
     * Logs when a raid starts.
     * @param event The event.
     */
    @EventHandler
    public void onRaidTriggerEvent(RaidTriggerEvent event) {
        new Log("World | Raid Started")
                .addFragment(event.getPlayer())
                .addFragment(" triggered a raid at ")
                .addFragment(event.getRaid().getLocation(), true)
                .addFragment(".")
                .send();
    }

    /**
     * Logs when a raid finishes.
     * @param event The vent.
     */
    @EventHandler
    public void onRaidFinish(RaidFinishEvent event) {
        new Log("World | Raid End")
                .addFragment("Raid at ")
                .addFragment(event.getRaid().getLocation(), true)
                .addFragment(" finished.")
                .send();
    }

    /**
     * Logs when a raid stops.
     * @param event The event.
     */
    @EventHandler
    public void onRaidStop(RaidStopEvent event) {
        new Log("World | Raid End")
                .addFragment("Raid at ")
                .addFragment(event.getRaid().getLocation(), true)
                .addFragment(" was stopped.")
                .send();
    }
}
