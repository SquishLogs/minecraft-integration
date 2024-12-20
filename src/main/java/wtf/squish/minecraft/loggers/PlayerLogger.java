package wtf.squish.minecraft.loggers;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Logger for player actions.
 * @author Livaco
 */
public class PlayerLogger implements Listener {
    private List<Player> playersThatCleared; // You could've prevented this Spigot...

    /**
     * Logs the player joining the server, as well as calls for them to be registered.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SquishLogs.registerPlayer(event.getPlayer());

        // Log them
        new Log("Player", "Connect")
                .addFragment(event.getPlayer())
                .addFragment(" joined the server.")
                .send();
    }

    /**
     * Logs the player leaving the server.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        new Log("Player", "Disconnect")
                .addFragment(event.getPlayer())
                .addFragment(" left the server.")
                .send();
    }

    /**
     * Logs the player chatting.
     * @param event The event.
     */
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;

        new Log("Player", "Chat")
                .addFragment(event.getPlayer())
                .addFragment(" said ")
                .addFragment(event.getMessage(), true)
                .addFragment(".")
                .send();
    }

    /**
     * Logs the player running commands.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(event.isCancelled()) return;

        new Log("Player", "Command")
                .addFragment(event.getPlayer())
                .addFragment(" ran command ")
                .addFragment(event.getMessage(), true)
                .addFragment(".")
                .send();
    }

    /**
     * Logs the player teleporting.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.isCancelled()) return;
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND) return;

        new Log("Player", "Teleport")
                .addFragment(event.getPlayer())
                .addFragment(" teleported from ")
                .addFragment(event.getFrom(), true)
                .addFragment(" to ")
                .addFragment(event.getTo(), true)
                .addFragment(".")
                .send();
    }

    /**
     * Logs the player changing game mode changing.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if(event.isCancelled()) return;

        new Log("Player", "Gamemode")
                .addFragment(event.getPlayer())
                .addFragment(" set their gamemode to ")
                .addFragment(event.getNewGameMode().name().toLowerCase(), true)
                .addFragment(".")
                .send();
    }

    /**
     * Logs when the player trades with a villager.
     * @param event The event.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.isCancelled()) return;
        // I'm trusting you https://www.spigotmc.org/threads/is-there-an-aftertradeevent.507855/#post-4172939
        if(event.getInventory().getType() != InventoryType.MERCHANT) return;
        if(event.getSlot() != 2) return;
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());

        if(event.getCurrentItem() != null) {
            new Log("Player", "Villager Trade")
                    .addFragment(player)
                    .addFragment(" traded with a villager to get a ")
                    .addFragment(event.getCurrentItem())
                    .addFragment(".")
                    .send();
        } else {
            new Log("Player", "Villager Trade")
                    .addFragment(player)
                    .addFragment(" traded with a villager to get an unknown item.")
                    .send();
        }
    }

    /**
     * Logs when the player crafts an item.
     * @param event The event.
     */
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if(event.isCancelled()) return;
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());

        // Thank you https://www.spigotmc.org/threads/how-to-get-amount-of-item-crafted.377598/#post-3896072
        ItemStack item = event.getRecipe().getResult();
        int amount = item.getAmount();
        if(event.getClick().isShiftClick()) {
            int lowerAmount = item.getMaxStackSize() + 1000;
            for(ItemStack actualItem : event.getInventory().getContents()) {
                if(actualItem.getType().isAir() || lowerAmount <= actualItem.getAmount() || actualItem.getType().equals(item.getType())) continue;
                lowerAmount = actualItem.getAmount();
            }
            amount = lowerAmount * item.getAmount();
        }

        new Log("Player", "Craft")
                .addFragment(player)
                .addFragment(" crafted ")
                .addFragment(amount, true)
                .addFragment(" of ")
                .addFragment(event.getRecipe().getResult())
                .addFragment(".")
                .send();
    }

    /**
     * Logs when the player takes a smelted item.
     * @param event The event.
     */
    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        new Log("Player", "Smelt")
                .addFragment(event.getPlayer())
                .addFragment(" took out ")
                .addFragment(event.getItemAmount(), true)
                .addFragment(" ")
                .addFragment(event.getItemType().name().toLowerCase(), true) // Why is this not an itemstack? Ffs
                .addFragment(" from a furnace at ")
                .addFragment(event.getPlayer().getLocation(), true)
                .addFragment(".")
                .send();
    }

    /**
     * Logs when the player enchants an item.
     * @param event The event.
     */
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        if(event.isCancelled()) return;
        Map<Enchantment, Integer> enchantsToAdd = event.getEnchantsToAdd();
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for(Map.Entry<Enchantment, Integer> entry : enchantsToAdd.entrySet()) {
            builder.append(entry.getKey().getKey().getKey()); // excellent naming there spigot
            if(i >= 0 && i < (enchantsToAdd.size() - 1)) {
                 builder.append(", ");
            }
            i++;
        }
        String enchantments = builder.toString();

        new Log("Player", "Enchant")
                .addFragment(event.getEnchanter())
                .addFragment(" enchanted their ")
                .addFragment(event.getItem())
                .addFragment(" with ")
                .addFragment(enchantments, true)
                .addFragment(".")
                .send();
    }

    /**
     * Logs when a player earns an advancement.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        String display = event.getAdvancement().getKey().getKey();
        if(SquishLogs.major > 1 || (SquishLogs.minor >= 19 && SquishLogs.major == 1)) {
            if(event.getAdvancement().getDisplay() != null) {
                display = event.getAdvancement().getDisplay().getTitle();
            }
        }

        new Log("Player", "Advancement")
                .addFragment(event.getPlayer())
                .addFragment(" earned the advancement ")
                .addFragment(display, true)
                .addFragment(".")
                .send();
    }

    /**
     * Logs when a player gets effects modified to them. You are not allowed to judge my implementation on this.
     * @param event The event.
     */
    @EventHandler
    public void onEntityPotionEffectEvent(EntityPotionEffectEvent event) {
        if(event.isCancelled()) return;
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        switch(event.getAction()) {
            case ADDED, CHANGED -> {
                if(event.getNewEffect() == null) return;
                new Log("Player", "Potion")
                        .addFragment(player)
                        .addFragment(" was given the effect of ")
                        .addFragment(event.getNewEffect().getType().getName(), true)
                        .addFragment(".")
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
                    if(playersThatCleared.size() == 0) {
                        playersThatCleared = null;
                    }
                }, 1);

                new Log("Player", "Potion")
                        .addFragment(player)
                        .addFragment(" had their effects cleared.")
                        .send();
            }
            case REMOVED -> {
                if(event.getOldEffect() == null) return;
                new Log("Player", "Potion")
                        .addFragment(player)
                        .addFragment(" had their ")
                        .addFragment(event.getOldEffect().getType().getName(), true)
                        .addFragment(" effect removed.")
                        .send();
            }
        }
    }

    /**
     * Logs when the player opens a chest.
     * @param event The event.
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(event.isCancelled()) return;
        if(event.getInventory().getType() != InventoryType.CHEST) return;
        if(!(event.getPlayer() instanceof Player)) return;
        if(event.getInventory().getLocation() == null) return; // Likely a plugin opening it
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());

        new Log("Player", "Chest Open")
                .addFragment(player)
                .addFragment(" opened a chest at ")
                .addFragment(event.getInventory().getLocation())
                .addFragment(".")
                .send();
    }

    /**
     * Logs when the player closes a chest.
     * @param event The event.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getInventory().getType() != InventoryType.CHEST) return;
        if(!(event.getPlayer() instanceof Player)) return;
        if(event.getInventory().getLocation() == null) return; // Likely a plugin opening it
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());

        new Log("Player", "Chest Close")
                .addFragment(player)
                .addFragment(" closed a chest at ")
                .addFragment(event.getInventory().getLocation())
                .addFragment(".")
                .send();
    }
}
