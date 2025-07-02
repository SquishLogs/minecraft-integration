package wtf.squish.minecraft.loggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import wtf.squish.minecraft.GameLog;
import wtf.squish.minecraft.SquishLogs;

public class ChatLogger implements Listener {
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled())
            return;

        new GameLog()
                .setCategory("Chat | Message")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" said ")
                .addTextFragment(event.getMessage(), true)
                .addTextFragment(".")
                .send();
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(event.isCancelled())
            return;

        new GameLog()
                .setCategory("Chat | Command")
                .addPlayerFragment(event.getPlayer())
                .addTextFragment(" ran command ")
                .addTextFragment(event.getMessage(), true)
                .addTextFragment(".")
                .send();
    }
}
