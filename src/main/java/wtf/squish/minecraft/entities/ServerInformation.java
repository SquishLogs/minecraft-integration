package wtf.squish.minecraft.entities;

import java.net.URI;

/**
 * Stores all the information about the server.
 * This is the info gotten from the /api/v1/server request.
 * @author Livaco
 */
public class ServerInformation {
    private int id;
    private String name;
    private String regionId;
    private String ipAddress;
    private String serverType;
    private String color;
    private short logErrors; // Either a 1 or a 0 - fuck you GSON for not being able to parse one between the other ffs
    private String discordWebhook;
    private SocketInformation socket;

    // getters setters
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getRegionID() {return regionId;}
    public void setRegionID(String regionID) {this.regionId = regionID;}

    public String getIpAddress() {return ipAddress;}
    public void setIpAddress(String ipAddress) {this.ipAddress = ipAddress;}

    public String getServerType() {return serverType;}
    public void setServerType(String serverType) {this.serverType = serverType;}

    public String getColor() {return color;}
    public void setColor(String color) {this.color = color;}

    public short getLogErrors() {return logErrors;}
    public void setLogErrors(short logErrors) {this.logErrors = logErrors;}

    public String getDiscordWebhook() {return discordWebhook;}
    public void setDiscordWebhook(String webhook) {this.discordWebhook = webhook;}

    public SocketInformation getSocket() {return socket;}
    public void setSocket(SocketInformation socket) {this.socket = socket;}
}
