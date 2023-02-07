package wtf.squish.minecraft.entities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import wtf.squish.minecraft.enums.FragmentType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores information about log fragments.
 * @author Livaco
 */
public class Fragment {
    private FragmentType type;
    private final HashMap<String, Object> data = new HashMap<>();

    private transient final List<FragmentMeta> meta = new ArrayList<>(); // This is to prevent unsafe operations issues with putting meta into the data map

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
        this.data.put("location", player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ());

        HashMap<String, Object> itemData = new HashMap<>();
        Material itemMaterial = player.getInventory().getItemInMainHand().getType();
        String itemName;
        if(itemMaterial == Material.AIR) {
            itemName = "hand";
        } else {
            itemName = itemMaterial.name().replace('_', ' ').toLowerCase();
        }
        itemData.put("name", itemName);
        itemData.put("class", itemMaterial.name());
        this.data.put("main_item", itemData);
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

    /**
     * Creates a location fragment. This gets converted to a text fragment internally.
     * @param location The location.
     * @param color The color.
     */
    public Fragment(Location location, Color color) {
        this(location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ(), color);
    }

    /**
     * Adds an entry of metadata to the fragment.
     * @param title The title of the metadata entry.
     * @param text The text of the metadata entry.
     */
    public void addFragmentMeta(String title, String text) {
        meta.add(new FragmentMeta(title, text));
    }

    // get/set
    public FragmentType getType() {return type;}
    public void setType(FragmentType type) {this.type = type;}

    public HashMap<String, Object> getData() {return data;}
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    public List<FragmentMeta> getMeta() {return meta;}

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
