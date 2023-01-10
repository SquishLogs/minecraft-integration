package wtf.squish.minecraft.loggers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Log;

import java.util.HashMap;

/**
 * Logs actions related to combat.
 * @author Livaco
 */
public class CombatLogger implements Listener {
    private final HashMap<EntityDamageEvent.DamageCause, String> causeStrings = new HashMap<>() {{
        put(EntityDamageEvent.DamageCause.PROJECTILE, "died from a projectile.");
        put(EntityDamageEvent.DamageCause.VOID, "fell into the void.");
        put(EntityDamageEvent.DamageCause.LIGHTNING, "died from being struck by lightning.");
        put(EntityDamageEvent.DamageCause.FALLING_BLOCK, "died from being squished by a block.");
        put(EntityDamageEvent.DamageCause.DRAGON_BREATH, "died to dragons breath.");
        put(EntityDamageEvent.DamageCause.FLY_INTO_WALL, "died by flying into a wall too fast.");
        put(EntityDamageEvent.DamageCause.SONIC_BOOM, "died from a sonic boom.");
        put(EntityDamageEvent.DamageCause.FALL, "died from fall damage.");
        put(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, "died to an explosion.");
        put(EntityDamageEvent.DamageCause.FIRE, "burned to death.");
        put(EntityDamageEvent.DamageCause.FIRE_TICK, "burned to death.");
        put(EntityDamageEvent.DamageCause.LAVA, "swam in lava.");
        put(EntityDamageEvent.DamageCause.HOT_FLOOR, "died to stepping on a hot floor.");
        put(EntityDamageEvent.DamageCause.POISON, "died to poison.");
        put(EntityDamageEvent.DamageCause.WITHER, "died from withering.");
        put(EntityDamageEvent.DamageCause.CRAMMING, "died while being crammed.");
        put(EntityDamageEvent.DamageCause.FREEZE, "froze to death.");
        put(EntityDamageEvent.DamageCause.DROWNING, "drowned.");
        put(EntityDamageEvent.DamageCause.STARVATION, "starved to death.");
        put(EntityDamageEvent.DamageCause.SUICIDE, "committed suicide.");
    }};

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

    /**
     * Logs player deaths.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if(player.getLastDamageCause() != null) {
            EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();
            if(cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                && cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                && cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
            ) {
                new Log("Combat | Death")
                        .addFragment(player)
                        .addFragment(" ")
                        .addFragment(causeStrings.get(player.getLastDamageCause().getCause()))
                        .send();
                return;
            }
        }
        if(killer == null) return;

        new Log("Combat | PvP")
                .addFragment(killer)
                .addFragment(" killed ")
                .addFragment(player)
                .addFragment(" with a ")
                .addFragment(killer.getInventory().getItemInMainHand().getType().name().toLowerCase(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }

    /**
     * Logs PvP damage.
     * @param event The event.
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Thanks https://www.spigotmc.org/threads/get-the-mob-that-killed-a-player.508310/#post-4175020
        // Why this sorta logic isn't possible in onPlayerDeath or onEntityDeath is beyond me
        if(!(event.getEntity() instanceof Player player)) return;
        if(event.getDamager() instanceof Player) return; // Other loggers deal with this
        if(event.getFinalDamage() < player.getHealth()) return;

        new Log("Combat | PvP")
                .addFragment(player)
                .addFragment(" was killed by a ")
                .addFragment(event.getDamager().getName(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }
}