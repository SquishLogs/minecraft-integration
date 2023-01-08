package wtf.squish.minecraft.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.AuthResponse;
import wtf.squish.minecraft.entities.Fragment;
import wtf.squish.minecraft.entities.Log;
import wtf.squish.minecraft.util.Output;

import java.net.URI;
import java.util.HashMap;

/**
 * Handles the talking to the websocket
 * @author Livaco
 */
public class LogWebsocketClient extends WebSocketClient {
    private boolean needsReconnected = false;
    private boolean stayClosed = false;

    /**
     * Creates the websocket.
     * @param serverUri The URI of the websocket to connect to.
     */
    public LogWebsocketClient(URI serverUri) {
        super(serverUri);
    }

    /**
     * Runs when the websocket connects.
     * @param handshakedata The handshake of the websocket instance
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if(needsReconnected) {
            // We're just back from a reconnection, nothing much to do
            Output.print("Reconnected.");
            return;
        }

        Output.print("Connected to websocket. Attempting authentication...");
        Gson gson = new Gson();

        // Not sure if this or hard coding with concat it is better practice, easy change if i decide against one tho
        HashMap<String, String> authValues = new HashMap<>();
        authValues.put("type", "auth");
        authValues.put("community", SquishLogs.getConfigInstance().getString("community"));
        authValues.put("token", SquishLogs.getServerInfo().getToken());
        send(gson.toJson(authValues));
    }

    /**
     * Runs when the websocket receives a message.
     * @param message The UTF-8 decoded message that was received.
     */
    @Override
    public void onMessage(String message) {
        Gson gson = new Gson();
        AuthResponse response = gson.fromJson(message, AuthResponse.class);
        if(!response.isSuccess()) {
            Output.print("Websocket authentication failed.");
            Output.print("Check your configuration!");

            stayClosed = true;
            this.close();
            return;
        }

        Output.print("Authenticated.");
        Output.print("Good to go!");

        HashMap<String, String> archiveValues = new HashMap<>();
        archiveValues.put("type", "archive");
        send(gson.toJson(archiveValues));

        // Start Log
        new Log("System")
                .addFragment(new Fragment("Server started.", null))
                .send();
    }

    /**
     * Runs when the websocket gets closed.
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(stayClosed) {
            Output.print("Websocket connection will not be re-established.");
            return;
        }
        Output.print("Lost connection to websocket.");
        Output.print("Waiting until next message before reconnecting...");
    }

    /**
     * Runs when the websocket errors out.
     * @param ex The exception causing this error
     */
    @Override
    public void onError(Exception ex) {
        Output.print("Websocket produced this error:");
        ex.printStackTrace();
    }

    /**
     * Runs when the websocket needs to send data, overridden so reconnection logic can be put here.
     * @param text The string which will be transmitted.
     */
    @Override
    public void send(String text) {
        if(this.getConnection().isClosed()) {
            if(stayClosed) return;
            Output.print("Attempting to reconnect...");
            this.reconnect();
        }

        super.send(text);
    }
}
