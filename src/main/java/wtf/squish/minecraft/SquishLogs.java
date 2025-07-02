package wtf.squish.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.squish.minecraft.loggers.ChatLogger;
import wtf.squish.minecraft.loggers.PlayerLogger;
import wtf.squish.minecraft.loggers.ServerLogger;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

public class SquishLogs extends JavaPlugin {
    public static SquishConfig config;
    public static SquishServerInformation serverInformation;
    public static Websocket websocket;
    public static Color highlightColor = new Color(29, 97, 129);
    public static int minecraftMajor;
    public static int minecraftMinor;

    private static SquishLogs instance;
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public void onEnable() {
        log("Loading SquishLogs...");
        instance = this;

        // Start figuring out the version stuff
        String version = Bukkit.getBukkitVersion().split("-")[0];
        String[] splitVersion = version.split("([.])");
        minecraftMajor = Integer.parseInt(splitVersion[0]);
        minecraftMinor = Integer.parseInt(splitVersion[1]);

        // Read the config file
        this.saveDefaultConfig();
        config = new SquishConfig(this);

        // Fetch the remote information about the server
        serverInformation = SquishServerInformation.getFromRemote();
        log(serverInformation.getName() + " (" + config.getDomain() + ") [" + serverInformation.getIpAddress() + "] using minecraft " + minecraftMajor + "." + minecraftMinor);

        if(serverInformation == null || config == null) {
            log("Error: Failed to find all the required information about this server.");
            log("Squish Logs will not continue further.");
            return;
        }
        if(!serverInformation.getServerType().equals("Minecraft")) {
            log("Error: This configuration is not bound to a Minecraft server! Did you download the integration correctly?");
            log("Squish Logs will not continue further.");
            return;
        }

        // Register our loggers
        getServer().getPluginManager().registerEvents(new ChatLogger(), this);
        getServer().getPluginManager().registerEvents(new PlayerLogger(), this);
        getServer().getPluginManager().registerEvents(new ServerLogger(), this);

        // Connect to the websocket
        websocket = new Websocket(URI.create(serverInformation.getSocket().getIpAddress()));
        websocket.connect();
    }

    /**
     * Logs a message to the console in the SquishLogs styling.
     * @param message The message to log.
     */
    protected static void log(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "SquishLogs >> " + ChatColor.WHITE + message);
    }

    protected static SquishConfig getSquishConfig() {
        return config;
    }
    protected static HttpClient getHttpClient() {
        return httpClient;
    }
    public static SquishLogs getInstance() {
        return SquishLogs.instance;
    }

    public static void registerPlayer(Player player) {
        websocket.registerPlayer(player);
    }
}