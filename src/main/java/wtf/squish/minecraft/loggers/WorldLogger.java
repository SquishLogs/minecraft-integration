package wtf.squish.minecraft.loggers;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
import wtf.squish.minecraft.GameLog;
import wtf.squish.minecraft.SquishLogs;

public class WorldLogger implements Listener {
    @EventHandler
    public void onRaidTriggerEvent(RaidTriggerEvent event) {
        if(event.isCancelled())
            return;

        new GameLog()
                .setCategory("World | Raid")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" triggered a raid at ")
                .addLocationFragment(event.getRaid().getLocation(), SquishLogs.highlightColor)
                .addTextFragment(".")
                .send();
    }

    @EventHandler
    public void onRaidFinish(RaidFinishEvent event) {
        new GameLog()
                .setCategory("World | Raid")
                .addTextFragment("Raid at ")
                .addLocationFragment(event.getRaid().getLocation(), SquishLogs.highlightColor)
                .addTextFragment(" has finished.")
                .send();
    }


    @EventHandler
    public void onRaidStop(RaidStopEvent event) {
        new GameLog()
                .setCategory("World | Raid")
                .addTextFragment("Raid at ")
                .addLocationFragment(event.getRaid().getLocation(), SquishLogs.highlightColor)
                .addTextFragment(" was stopped.")
                .send();
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if(event.isCancelled())
            return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if(item.getType() != Material.NAME_TAG)
            return;
        if(item.getItemMeta() == null)
            return;

        String name = event.getRightClicked().getCustomName();
        if(name == null)
            name = event.getRightClicked().getName();

        new GameLog()
                .setCategory("World | Mob Renamed")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" renamed ")
                .addTextFragment(name, SquishLogs.highlightColor)
                .addTextFragment(" to ")
                .addTextFragment(item.getItemMeta().getDisplayName(), SquishLogs.highlightColor)
                .send();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled())
            return;
        if(event.getBlockPlaced().getType() != Material.FIRE)
            return;

        new GameLog()
                .setCategory("World | Fire")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" lit a fire at ")
                .addLocationFragment(event.getBlockPlaced().getLocation(), SquishLogs.highlightColor)
                .send();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(event.isCancelled())
            return;
        EntityType type = event.getEntityType();
        // https://minecraft-archive.fandom.com/wiki/Category:Bosses
        if(type != EntityType.WITHER
            && type != EntityType.ENDER_DRAGON
            && type != EntityType.WARDEN
            && type != EntityType.GIANT
            && type != EntityType.ILLUSIONER
            && type != EntityType.SKELETON_HORSE)
            return;

        new GameLog()
                .setCategory("World | Boss")
                .addTextFragment("A ")
                .addTextFragment(event.getEntityType().name(), SquishLogs.highlightColor)
                .addTextFragment(" spawned at ")
                .addLocationFragment(event.getLocation(), SquishLogs.highlightColor)
                .send();
    }
}