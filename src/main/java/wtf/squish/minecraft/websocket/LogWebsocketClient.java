package wtf.squish.minecraft.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import wtf.squish.minecraft.util.Output;

import java.net.URI;

/**
 * Handles the talking to the websocket
 * @author Livaco
 */
public class LogWebsocketClient extends WebSocketClient {
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
        send("test");
        Output.print("Connected to websocket.");
    }

    /**
     * Runs when the websocket receives a message.
     * We never should get them, so this is empty.
     * @param message The UTF-8 decoded message that was received.
     */
    @Override
    public void onMessage(String message) {}

    /**
     * Runs when the websocket gets closed.
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        Output.print("Lost connection to websocket.");
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
}
