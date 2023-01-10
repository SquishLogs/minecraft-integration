package wtf.squish.minecraft.loggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.Log;

/**
 * Logger for server and system actions.
 * @author Livaco
 */
public class SystemLogger implements Listener {
    /**
     * Logs when the console runs a command.
     * @param event The event.
     */
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if(event.isCancelled()) return;

        new Log("System")
                .addFragment("Server ran command ")
                .addFragment(event.getCommand(), true)
                .addFragment(".")
                .send();
    }
}
