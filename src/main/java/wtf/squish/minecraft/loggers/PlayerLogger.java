package wtf.squish.minecraft.loggers;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Log;
import wtf.squish.minecraft.util.Output;

import java.util.Map;

/**
 * Logger for player actions.
 * @author Livaco
 */
public class PlayerLogger implements Listener {
    /**
     * Logs the player joining the server, as well as calls for them to be registered.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SquishLogs.registerPlayer(event.getPlayer());

        // Log them
        new Log("Player | Connect")
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
        new Log("Player | Disconnect")
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
        new Log("Player | Chat")
                .addFragment(event.getPlayer())
                .addFragment(" said in chat ")
                .addFragment(event.getMessage(), SquishLogs.getLogColor())
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

        new Log("Player | Command")
                .addFragment(event.getPlayer())
                .addFragment(" ran command ")
                .addFragment(event.getMessage(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }

    /**
     * Logs the player teleporting.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        new Log("Player | Teleport")
                .addFragment(event.getPlayer())
                .addFragment(" teleported from ")
                .addFragment(event.getFrom().toString(), SquishLogs.getLogColor())
                .addFragment(" to ")
                .addFragment(event.getTo().toString(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }

    /**
     * Logs the player changing game mode changing.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        new Log("Player | Gamemode")
                .addFragment(event.getPlayer())
                .addFragment(" set their gamemode to ")
                .addFragment(event.getNewGameMode().name().toLowerCase(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }

    /**
     * Logs when the player trades with a villager.
     * @param event The event.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // I'm trusting you https://www.spigotmc.org/threads/is-there-an-aftertradeevent.507855/#post-4172939
        if(event.getInventory().getType() != InventoryType.MERCHANT) return;
        if(event.getSlot() != 2) return;
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());

        if(event.getCurrentItem() != null) {
            new Log("Player | Villager Trade")
                    .addFragment(player)
                    .addFragment(" traded with a villager to get a ")
                    .addFragment(event.getCurrentItem().getType().name().toLowerCase(), SquishLogs.getLogColor())
                    .addFragment(".")
                    .send();
        } else {
            new Log("Player | Villager Trade")
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

        new Log("Player | Craft")
                .addFragment(player)
                .addFragment(" crafted ")
                .addFragment(amount, SquishLogs.getLogColor())
                .addFragment(" of ")
                .addFragment(event.getRecipe().getResult().getType().name().toLowerCase(), SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }

    /**
     * Logs when the player enchants an item.
     * @param event The event.
     */
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
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

        new Log("Player | Enchant")
                .addFragment(event.getEnchanter())
                .addFragment(" enchanted their ")
                .addFragment(event.getItem().getType().name().toLowerCase(), SquishLogs.getLogColor())
                .addFragment(" with ")
                .addFragment(enchantments, SquishLogs.getLogColor())
                .addFragment(".")
                .send();
    }
}