package wtf.squish.minecraft.loggers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Log;

/**
 * Logs actions related to combat.
 * @author Livaco
 */
public class CombatLogger implements Listener {
    /**
     * Logs PVE deaths.
     * @param event The event.
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Player) return;
        if(event.getEntity().getKiller() == null) return;

        new Log("Combat | Entity Death")
                .addFragment(event.getEntity().getKiller())
                .addFragment(" killed a ")
                .addFragment(event.getEntity().getType().name().toLowerCase(), SquishLogs.getLogColor())
                .addFragment(" with a ")
                .addFragment(event.getEntity().getKiller().getInventory().getItemInMainHand().getType().name().toLowerCase(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if(killer != null) {
            new Log("Combat | PvP")
                    .addFragment(killer)
                    .addFragment(" killed ")
                    .addFragment(player)
                    .addFragment(" with a ")
                    .addFragment(killer.getInventory().getItemInMainHand().getType().name().toLowerCase(), SquishLogs.getLogColor())
                    .addFragment(".")
                    .send();
        } else {
            new Log("Combat | Death")
                    .addFragment(player)
                    .addFragment(" died.")
                    .send();
        }
    }
}
