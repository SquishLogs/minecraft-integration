package wtf.squish.minecraft;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import wtf.squish.minecraft.entities.ServerInformation;
import wtf.squish.minecraft.listeners.ChatListeners;
import wtf.squish.minecraft.util.Output;
import wtf.squish.minecraft.websocket.LogWebsocketClient;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.time.Duration;
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

    @Override
    public void onEnable() {
        Output.print("Loading SquishLogs...");
        instance = this;

        // Config
        this.saveDefaultConfig();

        // Register our listeners
        getServer().getPluginManager().registerEvents(new ChatListeners(), this);

        // Fetch the websocket info
        boolean gotInformation = getWebsocketInfo();
        if(!gotInformation || serverInfo == null) {
            Output.print("Failed to receive information about the server.");
            Output.print("Refusing to go any further.");
            return;
        } else {
            Output.print("Server information received.");
            if(!serverInfo.getServerType().equals("Minecraft")) {
                Output.print("Uh, this isn't a minecraft server type...");
                Output.print("Unsure what to do here, but we'll just continue on and hope it's fine.");
            }

            Output.print("  Server: " + serverInfo.getName() + " (" + serverInfo.getIpAddress() + ")");
            Output.print("  Server/Socket Region: " + serverInfo.getRegionID() + " / " + serverInfo.getSocket().getContinent());
        }

        // Set up the websocket
//        URI uri;
//        try {
//            uri = new URI("ws://localhost:8765");
//        } catch(URISyntaxException e) {
//            Output.print("Failed to find websocket host.");
//            throw new RuntimeException(e);
//        }
//        webSocketClient = new LogWebsocketClient(uri);
//        webSocketClient.connect();

        // Done.
        Output.print("Loaded successfully.");
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
}