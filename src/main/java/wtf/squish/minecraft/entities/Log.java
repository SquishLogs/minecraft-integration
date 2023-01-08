package wtf.squish.minecraft.entities;

import com.google.gson.Gson;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.util.Output;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores and handles log objects.
 * @author Livaco
 */
public class Log {
    private String category;
    private Color currentColor = Color.RED;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public Log(String category) {
        this.category = category;
    }

    public Log setColor(Color color) {
        this.currentColor = color;
        return this;
    }
    public Log addFragment(Fragment fragment) {
        this.fragments.add(fragment);
        return this;
    }

    public void send() {
        Gson gson = new Gson();
        HashMap<String, Object> values = new HashMap<>();
        values.put("type", "log");
        values.put("category", this.category);
        values.put("log", this.fragments);

        String log = gson.toJson(values);
        Output.print(log);
        SquishLogs.getWebSocket().send(log);
    }

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public Color getCurrentColor() {return currentColor;}
    public void setCurrentColor(Color currentColor) {this.currentColor = currentColor;}

    public ArrayList<Fragment> getFragments() {return fragments;}
    public void setFragments(ArrayList<Fragment> fragments) {this.fragments = fragments;}
}
