package wtf.squish.minecraft.websocket;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.entities.SocketResponse;
import wtf.squish.minecraft.entities.Log;
import wtf.squish.minecraft.util.Output;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles the talking to the websocket
 * @author Livaco
 */
public class LogWebsocketClient extends WebSocketClient {
    private boolean needsReconnected = false;
    private List<String> reconnectionQueue;
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
        if(!needsReconnected) {
            Output.print("Connected to websocket. Attempting authentication...");
        } else {
            Output.print("Reconnected. Reauthenticating...");
        }

        Gson gson = new Gson();

        // Not sure if this or hard coding with concat it is better practice, easy change if I decide against one tho
        HashMap<String, String> authValues = new HashMap<>();
        authValues.put("type", "auth");
        authValues.put("community", SquishLogs.getConfigInstance().getString("community"));
        authValues.put("token", SquishLogs.getConfigInstance().getString("token"));
        send(gson.toJson(authValues));
    }

    /**
     * Runs when the websocket receives a message.
     * @param message The UTF-8 decoded message that was received.
     */
    @Override
    public void onMessage(String message) {
        Gson gson = new Gson();
        SocketResponse response = gson.fromJson(message, SocketResponse.class);

        if(response.getType().equals("ping")) return;
        if(!response.isSuccess()) {
            Output.print("Websocket authentication failed.");
            Output.print("Check your configuration!");

            stayClosed = true;
            this.close();
            return;
        }

        Output.print("Authenticated.");

        if(needsReconnected) {
            if(reconnectionQueue != null) {
                if(!reconnectionQueue.isEmpty()) {
                    Output.print("Clearing log queue from reconnection.");
                    for(String data : reconnectionQueue) {
                        send(data);
                    }
                }

                reconnectionQueue = null;
            }

            needsReconnected = false;
        } else {
            Output.print("Good to go!");

            HashMap<String, String> archiveValues = new HashMap<>();
            archiveValues.put("type", "archive");
            send(gson.toJson(archiveValues));

            // Start Log
            new Log("System", "Server")
                    .addFragment("Server started.")
                    .send();
        }
    }

    /**
     * Runs when the websocket gets closed.
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     */
    @SuppressWarnings("GrazieInspection")
    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(stayClosed) {
            Output.print("Websocket connection will not be re-established.");
            return;
        }
        Output.print("Lost connection to websocket.");
        Output.print("Waiting until next message before reconnecting...");
        needsReconnected = true;
    }

    /**
     * Runs when the websocket errors out.
     * @param ex The exception causing this error
     */
    @Override
    public void onError(Exception ex) {
        Output.print("Websocket produced this error:");
        ex.printStackTrace();
        Output.print("Closing, so we can get a fresh connection.");
        this.close();
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

            if(this.reconnectionQueue == null) {
                this.reconnectionQueue = new ArrayList<>();
            }
            this.reconnectionQueue.add(text);
            this.reconnect();
        } else {
            super.send(text);
        }
    }
}
