package wtf.squish.minecraft.entities;

import org.bukkit.entity.Player;
import wtf.squish.minecraft.enums.FragmentType;

import java.awt.*;
import java.util.HashMap;

/**
 * Stores information about log fragments.
 * @author Livaco
 */
public class Fragment {
    private FragmentType type;
    private HashMap<String, Object> data = new HashMap<>();

    public Fragment(FragmentType type) {
        this.type = type;
    }
    public Fragment(Player player) {
        this.type = FragmentType.PLAYER;
        this.data.put("name", player.getName());
        this.data.put("platform_name", player.getName());
        this.data.put("platform_id", player.getUniqueId().toString());
        this.data.put("health", player.getHealth());
        this.data.put("hunger", player.getFoodLevel());
    }
    public Fragment(String string, Color color) {
        this.type = FragmentType.TEXT;
        this.data.put("text", " " + string + " "); // Pad it with spaces so it doesn't interfere with other fragments.
        this.data.put("color", formatColor(color));
    }
    public Fragment(int number, Color color) {
        this(String.valueOf(number), color);
    }

    public FragmentType getType() {return type;}
    public void setType(FragmentType type) {this.type = type;}

    public HashMap<String, Object> getData() {return data;}
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    private static String formatColor(Color color) {
        if(color == null) return null;
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
}