package wtf.squish.minecraft.entities;

import com.google.gson.Gson;
import org.bukkit.entity.Player;
import wtf.squish.minecraft.SquishLogs;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores and handles log objects.
 * @author Livaco
 */
public class Log {
    private String category;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    /**
     * Creates a new log.
     * @param category The category for this log.
     */
    public Log(String category) {
        this.category = category;
    }

    /**
     * Adds a new fragment to the log.
     * @param fragment The fragment to add.
     * @return The log object.
     */
    public Log addFragment(Fragment fragment) {
        this.fragments.add(fragment);
        return this;
    }

    /**
     * Adds a new player fragment.
     * @param player The player.
     * @return The log object.
     */
    public Log addFragment(Player player) {
        return this.addFragment(new Fragment(player));
    }

    /**
     * Adds a text fragment with a color.
     * @param string The text.
     * @param color The color of the text.
     * @return The log object.
     */
    public Log addFragment(String string, Color color) {
        return this.addFragment(new Fragment(string, color));
    }

    /**
     * Adds a text fragment.
     * @param string The text.
     * @return The log object.
     */
    public Log addFragment(String string) {
        return this.addFragment(new Fragment(string, null));
    }

    /**
     * Adds a number fragment with a color.
     * @param number The number.
     * @param color The color of the number.
     * @return The log object.
     */
    public Log addFragment(int number, Color color) {
        return this.addFragment(new Fragment(number, color));
    }

    /**
     * Adds a number fragment.
     * @param number The number.
     * @return The log object.
     */
    public Log addFragment(int number) {
        return this.addFragment(new Fragment(number, null));
    }

    /**
     * Sends the log to the server.
     */
    public void send() {
        Gson gson = new Gson();
        HashMap<String, Object> values = new HashMap<>();
        values.put("type", "log");
        values.put("category", this.category);
        values.put("log", this.fragments);

        String log = gson.toJson(values);
        SquishLogs.getWebSocket().send(log);
    }

    // getters/setters
    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public ArrayList<Fragment> getFragments() {return fragments;}
    public void setFragments(ArrayList<Fragment> fragments) {this.fragments = fragments;}
}
