package wtf.squish.minecraft.entities;

/**
 * Stores the authentication response given to us.
 * This class only exists because JsonParser is deprecated. Fuck you google.
 * @author Livaco
 */
public class SocketResponse {
    private String type;
    private boolean success;

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}

    public boolean isSuccess() {return success;}
    public void setSuccess(boolean success) {this.success = success;}
}
