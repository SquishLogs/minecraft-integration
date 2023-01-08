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
        this.data.clear();
        this.data.put("name", player.getName());
        this.data.put("platform_name", player.getName());
        this.data.put("platform_id", player.getUniqueId().toString());
        this.data.put("health", player.getHealth());
        this.data.put("hunger", player.getFoodLevel());
    }
    public Fragment(String string, Color color) {
        this.type = FragmentType.TEXT;
        this.data.clear();
        this.data.put("text", string);
        this.data.put("color", color);
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
}
