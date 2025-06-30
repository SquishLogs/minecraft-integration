package wtf.squish.minecraft;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;

public class Websocket extends WebSocketClient {
    private boolean requiresReconnect = false;
    private int reconnectionAttempts = 0;
    private boolean terminated = false;
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public Websocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.reconnectionAttempts = 0;

        if(this.requiresReconnect) {
            SquishLogs.log("Reconnected, re-authenticating...");
            this.requiresReconnect = false;
        } else {
            SquishLogs.log("Connected to websocket, authenticating...");
        }

        // Authenticate
        HashMap<String, String> authValues = new HashMap<>();
        authValues.put("type", "auth");
        authValues.put("community", SquishLogs.getSquishConfig().getCommunity());
        authValues.put("token", SquishLogs.getSquishConfig().getToken());
        send(this.gson.toJson(authValues));
    }

    @Override
    public void onMessage(String message) {
        WebsocketMessage response = this.gson.fromJson(message, WebsocketMessage.class);

        switch (response.getType()) {
            case Ping -> {
                // websocket doesn't need a pong
            }
            case AuthResponse -> {
                if(!response.isSuccess()) {
                    SquishLogs.log("Error: Server rejected authentication. Check your configuration and try again.");
                    SquishLogs.log("Terminating connection.");
                    this.terminateConnection();
                    return;
                }

                SquishLogs.log("Authentication successful.");
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(this.terminated)
            return;

        // Reconnection
        if(this.reconnectionAttempts > 3) {
            SquishLogs.log("Failed to reacquire connection to websocket after 3 attempts. To prevent spamming our servers, please restart your server to retry again.");
            this.terminateConnection();
            return;
        }
        SquishLogs.log("Lost connection to websocket... reconnecting...");
        this.reconnect();
        this.reconnectionAttempts++;
    }

    @Override
    public void onError(Exception e) {
        SquishLogs.log("Error: Websocket produced error.");
        e.printStackTrace();
    }

    public void terminateConnection() {
        this.terminated = true;
        this.close();
    }

    public void sendLog(LogBuilder logBuilder) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("type", "log");
        data.put("category", logBuilder.getCategory());
        data.put("log", logBuilder.getFragments());

        String jsonLog = this.gson.toJson(data);
        this.send(jsonLog);
        SquishLogs.log(jsonLog);
    }

    private static class WebsocketMessage {
        private MessageType type;
        private boolean success;

        enum MessageType {
            @SerializedName("ping")
            Ping,
            @SerializedName("auth-response")
            AuthResponse
        }

        public MessageType getType() {return type;}
        public boolean isSuccess() {return success;}
    }
}