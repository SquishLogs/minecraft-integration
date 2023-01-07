package wtf.squish.minecraft.entities;

/**
 * Stores information about the websockets.
 * @author Livaco
 */
public class SocketInformation {
    private String uid;
    private String name;
    private String ipAddress;
    private String continent;

    // This is why I did not miss java...
    public String getUid() {return uid;}
    public void setUid(String uid) {this.uid = uid;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getIpAddress() {return ipAddress;}
    public void setIpAddress(String ipAddress) {this.ipAddress = ipAddress;}

    public String getContinent() {return continent;}
    public void setContinent(String continent) {this.continent = continent;}
}
