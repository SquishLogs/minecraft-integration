package wtf.squish.minecraft.entities;

import com.google.gson.Gson;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.util.Output;

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
