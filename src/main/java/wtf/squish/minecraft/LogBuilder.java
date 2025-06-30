package wtf.squish.minecraft;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class LogBuilder {
    private String category = "Unknown Category";
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public LogBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    public LogBuilder addRawFragment(Fragment fragment) {
        this.fragments.add(fragment);
        return this;
    }
    public LogBuilder addTextFragment(String text, Color highlightColor) {
        return this.addRawFragment(new Fragment(Fragment.FragmentType.Text)
                .setData("text", text)
                .setData("color", highlightColor));
    }
    public LogBuilder addTextFragment(String text) {
        return this.addRawFragment(new Fragment(Fragment.FragmentType.Text)
                .setData("text", text));
    }
    public LogBuilder addPlayerFragment(Player player) {
        return this.addRawFragment(new Fragment(Fragment.FragmentType.Player)
                .setData("name", player.getName())
                .setData("platform_name", player.getName())
                .setData("platform_id", player.getUniqueId())
                .setData("health", player.getHealth())
                .setData("hunger", player.getFoodLevel())
                .setData("location", formatLocation(player.getLocation())));
    }
    public LogBuilder addLocationFragment(Location location, Color highlightColor) {
        return this.addRawFragment(new Fragment(Fragment.FragmentType.Text)
                .setData("text", formatLocation(location))
                .setData("color", highlightColor));
    }
    public LogBuilder addLocationFragment(Location location) {
        return this.addRawFragment(new Fragment(Fragment.FragmentType.Text)
                .setData("text", formatLocation(location)));
    }

    public void send() {
        SquishLogs.websocket.sendLog(this);
    }

    public String getCategory() {
        return category;
    }
    public ArrayList<Fragment> getFragments() {
        return fragments;
    }

    public static String formatLocation(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }

    public static class Fragment {
        private final FragmentType type;
        private final HashMap<String, Object> data = new HashMap<>();

        public Fragment(FragmentType type) {
            this.type = type;
        }

        public Fragment setData(String key, Object val) {
            if(val instanceof Color)
                val = formatFragmentColor((Color) val);
            this.data.put(key, val);
            return this;
        }

        private static String formatFragmentColor(Color color) {
            if(color == null)
                return null;
            return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
        }

        public enum FragmentType {
            @SerializedName("player")
            Player,
            @SerializedName("text")
            Text
        }
    }
}
