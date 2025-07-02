package wtf.squish.minecraft.loggers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import wtf.squish.minecraft.LogBuilder;
import wtf.squish.minecraft.SquishLogs;

import java.util.HashMap;

public class CombatLogger implements Listener {
    private final HashMap<EntityDamageEvent.DamageCause, String> causeStrings = new HashMap<>();
    public CombatLogger() {
        for(EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
            causeStrings.put(cause, switch(cause) {
                case KILL -> "was killed by a command.";
                case WORLD_BORDER -> "was killed by the world border";
                case CONTACT -> "died to contact with a block.";
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "was killed by another entity.";
                case PROJECTILE -> "died from a projectile.";
                case SUFFOCATION -> "suffocated.";
                case FALL -> "died from fall damage.";
                case FIRE, FIRE_TICK -> "burned to death.";
                case MELTING -> "melted.";
                case LAVA -> "tried to swim in lava.";
                case DROWNING -> "drowned.";
                case BLOCK_EXPLOSION -> "exploded";
                case ENTITY_EXPLOSION -> "exploded.";
                case VOID -> "fell into the void.";
                case LIGHTNING -> "was stuck by lightning";
                case SUICIDE -> "commited suicide.";
                case STARVATION -> "starved.";
                case POISON -> "was poisoned to death.";
                case MAGIC -> "was killed via magic.";
                case WITHER -> "withered to death.";
                case FALLING_BLOCK -> "was squashed by a falling block.";
                case THORNS -> "was killed via the thorns effect.";
                case DRAGON_BREATH -> "died to dragons breath.";
                case CUSTOM -> "died to custom damage.";
                case FLY_INTO_WALL -> "died by flying into a wall.";
                case HOT_FLOOR -> "was burned by a magma block.";
                case CAMPFIRE -> "was burned by a campfire";
                case CRAMMING -> "was crammed.";
                case DRYOUT -> "dried out.";
                case FREEZE -> "froze to death.";
                case SONIC_BOOM -> "died from a sonic boom.";
            });
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Player)
            return;
        if(event.getEntity().getKiller() == null)
            return;

        ItemStack item = event.getEntity().getKiller().getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR) {
            new LogBuilder()
                    .setCategory("Combat | Entity Death")
                    .addPlayerFragment(event.getEntity().getKiller())
                    .addTextFragment(" killed a ")
                    .addTextFragment(event.getEntity().getType().name().toLowerCase(), SquishLogs.highlightColor)
                    .addTextFragment(" with their hands.")
                    .send();
            return;
        }

        new LogBuilder()
                .setCategory("Combat | Entity Death")
                .addPlayerFragment(event.getEntity().getKiller())
                .addTextFragment(" killed a ")
                .addTextFragment(event.getEntity().getType().name().toLowerCase(), SquishLogs.highlightColor)
                .addTextFragment(" with ")
                .addItemFragment(item, SquishLogs.highlightColor)
                .addTextFragment(".")
                .send();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if(player.getLastDamageCause() != null) {
            EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();
            if(cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    && cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                    && cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                new LogBuilder()
                        .setCategory("Combat | Death")
                        .addPlayerFragment(player)
                        .addTextFragment(" ")
                        .addTextFragment(causeStrings.get(player.getLastDamageCause().getCause()))
                        .send();
                return;
            }
        }
        if(killer == null)
            return;

        ItemStack item = killer.getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR) {
            new LogBuilder()
                    .setCategory("Combat | PvP")
                    .addPlayerFragment(killer)
                    .addTextFragment(" killed ")
                    .addPlayerFragment(player)
                    .addTextFragment(" with their hands.")
                    .send();
            return;
        }
        new LogBuilder()
                .setCategory("Combat | PvP")
                .addPlayerFragment(killer)
                .addTextFragment(" killed ")
                .addPlayerFragment(player)
                .addTextFragment(" with ")
                .addItemFragment(item, SquishLogs.highlightColor)
                .addTextFragment(".")
                .send();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player player))
            return;

        if(event.getDamager() instanceof Player damager) {
            if(event.getFinalDamage() >= player.getHealth())
                return; // Other logs will deal with this one

            new LogBuilder()
                    .setCategory("Combat | PvP")
                    .addPlayerFragment(player)
                    .addTextFragment(" was damaged by ")
                    .addPlayerFragment(damager)
                    .addTextFragment(" for ")
                    .addTextFragment(String.valueOf(event.getFinalDamage()), SquishLogs.highlightColor)
                    .addTextFragment(" damage.")
                    .send();
            return;
        }

        // Thanks https://www.spigotmc.org/threads/get-the-mob-that-killed-a-player.508310/#post-4175020
        // Why this sorta logic isn't possible in onPlayerDeath or onEntityDeath is beyond me
        if(event.getFinalDamage() < player.getHealth())
            return;

        new LogBuilder()
                .setCategory("Combat | PvP")
                .addPlayerFragment(player)
                .addTextFragment(" was killed by a ")
                .addTextFragment(event.getDamager().getName(), SquishLogs.highlightColor)
                .addTextFragment(".")
                .send();
    }
}
