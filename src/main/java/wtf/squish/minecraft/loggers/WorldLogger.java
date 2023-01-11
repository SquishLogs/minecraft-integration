package wtf.squish.minecraft.loggers;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
import wtf.squish.minecraft.entities.Log;

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
        if(event.isCancelled()) return;

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
        if(event.isCancelled()) return;

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

    /**
     * Logs when an entity is renamed.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if(event.isCancelled()) return;

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if(item.getType() != Material.NAME_TAG) return;
        if(item.getItemMeta() == null) return;

        String name = event.getRightClicked().getCustomName();
        if(name == null) {
            name = event.getRightClicked().getName();
        }

        new Log("World | Mob Renamed")
                .addFragment(event.getPlayer())
                .addFragment(" renamed ")
                .addFragment(name, true)
                .addFragment(" to ")
                .addFragment(item.getItemMeta().getDisplayName(), true)
                .send();
    }

    /**
     * Logs when a player starts a fire.
     * @param event The event.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) return;
        if(event.getBlockPlaced().getType() != Material.FIRE) return;

        new Log("World | Fire Started")
                .addFragment(event.getPlayer())
                .addFragment(" lit a fire at ")
                .addFragment(event.getBlockPlaced().getLocation(), true)
                .send();
    }
}