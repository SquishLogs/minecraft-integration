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

    /**
     * Creates a fragment. This constructor requires you to manually add the data.
     * @param type The type of the fragment.
     */
    public Fragment(FragmentType type) {
        this.type = type;
    }

    /**
     * Creates a player fragment.
     * @param player The player.
     */
    public Fragment(Player player) {
        this.type = FragmentType.PLAYER;
        this.data.put("name", player.getName());
        this.data.put("platform_name", player.getName());
        this.data.put("platform_id", player.getUniqueId().toString());
        this.data.put("health", player.getHealth());
        this.data.put("hunger", player.getFoodLevel());
    }

    /**
     * Creates a text fragment.
     * @param string The text.
     * @param color The color of the text.
     */
    public Fragment(String string, Color color) {
        this.type = FragmentType.TEXT;
        this.data.put("text", string);
        this.data.put("color", formatColor(color));
    }

    /**
     * Creates a number fragment. This gets converted to a text fragment internally.
     * @param number The number.
     * @param color The color of the number.
     */
    public Fragment(int number, Color color) {
        this(String.valueOf(number), color);
    }

    // get/set
    public FragmentType getType() {return type;}
    public void setType(FragmentType type) {this.type = type;}

    public HashMap<String, Object> getData() {return data;}
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    /**
     * Formats the color object for the API.
     * @param color The color to format.
     * @return The formatted color, in "r,g,b" format.
     */
    private static String formatColor(Color color) {
        if(color == null) return null;
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
}
