package wtf.squish.minecraft.entities;

/**
 * Stores the authentication response given to us.
 * This class only exists because JsonParser is deprecated. Fuck you google.
 * @author Livaco
 */
public class AuthResponse {
    private boolean success;

    public boolean isSuccess() {return success;}
    public void setSuccess(boolean success) {this.success = success;}
}
