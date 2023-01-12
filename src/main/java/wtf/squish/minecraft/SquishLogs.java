package wtf.squish.minecraft;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.squish.minecraft.entities.Log;
import wtf.squish.minecraft.entities.ServerInformation;
import wtf.squish.minecraft.loggers.PlayerLogger;
import wtf.squish.minecraft.loggers.SystemLogger;
import wtf.squish.minecraft.loggers.CombatLogger;
import wtf.squish.minecraft.loggers.WorldLogger;
import wtf.squish.minecraft.util.Output;
import wtf.squish.minecraft.websocket.LogWebsocketClient;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Primary class for the plugin.
 * @author Livaco
 */
public class SquishLogs extends JavaPlugin {
    private static SquishLogs instance;
    private static LogWebsocketClient webSocketClient;
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static ServerInformation serverInfo;
    private static final Color logColor = new Color(29, 97, 129);

    @Override
    public void onEnable() {
        Output.print("Loading SquishLogs...");
        instance = this;

        // Config
        this.saveDefaultConfig();

        // Register our listeners
        // Register your custom loggers here!
        getServer().getPluginManager().registerEvents(new PlayerLogger(), this);
        getServer().getPluginManager().registerEvents(new SystemLogger(), this);
        getServer().getPluginManager().registerEvents(new CombatLogger(), this);
        getServer().getPluginManager().registerEvents(new WorldLogger(), this);
        // Don't touch anything beyond here :)

        // Fetch the websocket info
        boolean gotInformation = getWebsocketInfo();
        if(!gotInformation || serverInfo == null) {
            Output.print("Failed to receive information about the server.");
            Output.print("Refusing to go any further.");
            return;
        }

        Output.print("Server information received.");
        if(!serverInfo.getServerType().equals("Minecraft")) {
            Output.print("Uh, this isn't a minecraft server type...");
            Output.print("Unsure what to do here, but we'll just continue on and hope it's fine.");
        }
        Output.print("  Server: " + serverInfo.getName() + " (" + serverInfo.getIpAddress() + ")");
        Output.print("  Server/Socket Region: " + serverInfo.getRegionID() + " / " + serverInfo.getSocket().getContinent());
        Output.print("Attempting to connect to websocket...");
        connectToWebsocket();
    }

    public void onDisable() {
        new Log("System")
                .addFragment("Server stopped.")
                .send();

        instance = null;
    }

    public static SquishLogs getInstance() {
        return instance;
    }

    /**
     * Gives the config instance, typically to read from it.
     * @return The config instance.
     */
    public static FileConfiguration getConfigInstance() {
        return instance.getConfig();
    }

    /**
     * Fetches information about the server and websocket we're using.
     * @return True if successful, false if it failed in any way to fetch it.
     */
    public static boolean getWebsocketInfo() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(getConfigInstance().getString("domain") + "/api/v1/server"))
                .setHeader("User-Agent", "SquishLogsMinecraft/1.0.0 (+https://squish.wtf/)")
                .setHeader("X-Game-Server-Token", getConfigInstance().getString("token"))
                .build();

        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String responseBody;
        int statusCode;
        try {
            responseBody = response.thenApply(HttpResponse::body).get();
            statusCode = response.thenApply(HttpResponse::statusCode).get();
        } catch(InterruptedException | ExecutionException e) {
            Output.print("Failed to get valid response from URI " + request.uri().toString());
            throw new RuntimeException(e);
        }

        if(statusCode != 200) {
            Output.print("Failed to get valid response from URI " + request.uri().toString());
            Output.print("Response code " + statusCode);

            return false;
        }

        // Parse the JSON it gave us
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        serverInfo = gson.fromJson(responseBody, ServerInformation.class);
        return true;
    }

    /**
     * Attempts connection to the websocket.
     */
    public static void connectToWebsocket() {
        webSocketClient = new LogWebsocketClient(URI.create(serverInfo.getSocket().getIpAddress()));
        webSocketClient.connect();
    }

    /**
     * Gets the current websocket client.
     * @return The websocket client.
     */
    public static LogWebsocketClient getWebSocket() {
        return webSocketClient;
    }

    /**
     * Gets the current server's information, fetched from the API.
     * @return The server info.
     */
    public static ServerInformation getServerInfo() {
        return serverInfo;
    }

    /**
     * Returns the log color.
     * @return The log color.
     */
    public static Color getLogColor() {
        return logColor;
    }

    /**
     * Registers a player on the api.
     * @param player The player to register.
     */
    public static void registerPlayer(Player player) {
        Gson gson = new Gson();
        HashMap<String, Object> playerInfo = new HashMap<>();
        playerInfo.put("name", player.getName());
        playerInfo.put("platform_id", player.getUniqueId().toString());
        HashMap<String, Object> values = new HashMap<>();
        values.put("type", "player");
        values.put("player", playerInfo);

        String json = gson.toJson(values);
        SquishLogs.getWebSocket().send(json);
    }
}