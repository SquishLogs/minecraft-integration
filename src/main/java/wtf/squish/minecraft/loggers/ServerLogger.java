package wtf.squish.minecraft.loggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import wtf.squish.minecraft.GameLog;

public class ServerLogger implements Listener {
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if(event.isCancelled())
            return;

        new GameLog()
                .setCategory("System | Command")
                .addTextFragment("Server ran console command ")
                .addTextFragment(event.getCommand(), true)
                .addTextFragment(".")
                .send();
    }
}
