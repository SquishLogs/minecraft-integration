package wtf.squish.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import wtf.squish.minecraft.listeners.ChatListeners;
import wtf.squish.minecraft.util.Output;
import wtf.squish.minecraft.websocket.LogWebsocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.WebSocket;

/**
 * Primary class for the plugin.
 * @author Livaco
 */
public class SquishLogs extends JavaPlugin {
    private static SquishLogs instance;
    private static LogWebsocketClient webSocketClient;

    @Override
    public void onEnable() {
        Output.print("Loading SquishLogs...");
        instance = this;

        // Config
        this.saveDefaultConfig();

        // Register our listeners
        getServer().getPluginManager().registerEvents(new ChatListeners(), this);

        // Set up the websocket
        URI uri;
        try {
            uri = new URI("ws://localhost:8765");
        } catch(URISyntaxException e) {
            Output.print("Failed to find websocket host.");
            throw new RuntimeException(e);
        }
        webSocketClient = new LogWebsocketClient(uri);
        webSocketClient.connect();

        // Done.
        Output.print("Loaded.");
    }

    /**
     * Gives the config instance, typically to read from it.
     * @return The config instance.
     */
    public static FileConfiguration getConfigInstance() {
        return instance.getConfig();
    }
}