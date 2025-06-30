package wtf.squish.minecraft.loggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import wtf.squish.minecraft.LogBuilder;
import wtf.squish.minecraft.SquishLogs;

public class ChatLogger implements Listener {
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) {
            return;
        }

        new LogBuilder()
                .setCategory("Chat")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" said ")
                .addTextFragment(event.getMessage(), SquishLogs.highlightColor)
                .addTextFragment(".")
                .send();
    }
}
