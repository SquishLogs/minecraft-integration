package wtf.squish.minecraft.loggers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Log;

import java.util.HashMap;

/**
 * Logs actions related to combat.
 * @author Livaco
 */
public class CombatLogger implements Listener {
    private final HashMap<EntityDamageEvent.DamageCause, String> causeStrings = new HashMap<>();
    public CombatLogger() {
        causeStrings.put(EntityDamageEvent.DamageCause.PROJECTILE, "died from a projectile.");
        causeStrings.put(EntityDamageEvent.DamageCause.VOID, "fell into the void.");
        causeStrings.put(EntityDamageEvent.DamageCause.LIGHTNING, "died from being struck by lightning.");
        causeStrings.put(EntityDamageEvent.DamageCause.FALLING_BLOCK, "died from being squished by a block.");
        causeStrings.put(EntityDamageEvent.DamageCause.DRAGON_BREATH, "died to dragons breath.");
        causeStrings.put(EntityDamageEvent.DamageCause.FLY_INTO_WALL, "died by flying into a wall too fast.");
        causeStrings.put(EntityDamageEvent.DamageCause.FALL, "died from fall damage.");
        causeStrings.put(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, "died to an explosion.");
        causeStrings.put(EntityDamageEvent.DamageCause.FIRE, "burned to death.");
        causeStrings.put(EntityDamageEvent.DamageCause.FIRE_TICK, "burned to death.");
        causeStrings.put(EntityDamageEvent.DamageCause.LAVA, "swam in lava.");
        causeStrings.put(EntityDamageEvent.DamageCause.HOT_FLOOR, "died to stepping on a hot floor.");
        causeStrings.put(EntityDamageEvent.DamageCause.POISON, "died to poison.");
        causeStrings.put(EntityDamageEvent.DamageCause.WITHER, "died from withering.");
        causeStrings.put(EntityDamageEvent.DamageCause.CRAMMING, "died while being crammed.");
        causeStrings.put(EntityDamageEvent.DamageCause.DROWNING, "drowned.");
        causeStrings.put(EntityDamageEvent.DamageCause.STARVATION, "starved to death.");
        causeStrings.put(EntityDamageEvent.DamageCause.SUICIDE, "committed suicide.");

        if(SquishLogs.major > 1 || (SquishLogs.minor >= 19 && SquishLogs.major == 1)) {
            causeStrings.put(EntityDamageEvent.DamageCause.SONIC_BOOM, "died from a sonic boom.");
        }
        if(SquishLogs.major > 1 || (SquishLogs.minor >= 17 && SquishLogs.major == 1)) {
            causeStrings.put(EntityDamageEvent.DamageCause.FREEZE, "froze to death.");
        }
    }

    /**
     * Logs PVE deaths.
     * @param event The event.
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Player) return;
        if(event.getEntity().getKiller() == null) return;

        ItemStack item = event.getEntity().getKiller().getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR) {
            new Log("Combat", "Entity Death")
                    .addFragment(event.getEntity().getKiller())
                    .addFragment(" killed a ")
                    .addFragment(event.getEntity().getType().name().toLowerCase(), true)
                    .addFragment(" with their hands.")
                    .send();
            return;
        }

        new Log("Combat", "Entity Death")
                .addFragment(event.getEntity().getKiller())
                .addFragment(" killed a ")
                .addFragment(event.getEntity().getType().name().toLowerCase(), true)
                .addFragment(" with a ")
                .addFragment(item)
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
                new Log("Combat", "Death")
                        .addFragment(player)
                        .addFragment(" ")
                        .addFragment(causeStrings.get(player.getLastDamageCause().getCause()))
                        .send();
                return;
            }
        }
        if(killer == null) return;

        ItemStack item = killer.getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR) {
            new Log("Combat", "PvP")
                    .addFragment(killer)
                    .addFragment(" killed ")
                    .addFragment(player)
                    .addFragment(" with their hands.")
                    .send();
            return;
        }

        new Log("Combat", "PvP")
                .addFragment(killer)
                .addFragment(" killed ")
                .addFragment(player)
                .addFragment(" with a ")
                .addFragment(item)
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
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if(event.getFinalDamage() >= player.getHealth()) return; // Other logs will deal with this one
            new Log("Combat", "PvP")
                    .addFragment(player)
                    .addFragment(" was damaged by ")
                    .addFragment(damager)
                    .addFragment(" for ")
                    .addFragment((int) event.getFinalDamage(), true)
                    .addFragment(" damage.")
                    .send();
            return;
        }
        if(event.getFinalDamage() < player.getHealth()) return;

        new Log("Combat", "PvP")
                .addFragment(player)
                .addFragment(" was killed by a ")
                .addFragment(event.getDamager().getName(), true)
                .addFragment(".")
                .send();
    }

    // Below here is the version specific stuff

}