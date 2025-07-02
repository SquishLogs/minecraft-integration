package wtf.squish.minecraft;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameLog {
    private String category = "Unknown Category";
    private final ArrayList<Fragment> fragments = new ArrayList<>();

    public GameLog setCategory(String category) {
        this.category = category;
        return this;
    }

    public GameLog addRawFragment(Fragment fragment) {
        this.fragments.add(fragment);
        return this;
    }

    // Text Fragments
    public GameLog addTextFragment(String text, boolean highlight) {
        return this.addRawFragment(new Fragment(Fragment.FragmentType.Text)
                .setData("text", text)
                .setData("color", highlight ? SquishLogs.highlightColor : null));
    }
    public GameLog addTextFragment(String text) {
        return this.addTextFragment(text, false);
    }

    // Player Fragment
    public GameLog addPlayerFragment(Player player) {
        return this.addRawFragment(new Fragment(Fragment.FragmentType.Player)
                .setData("name", player.getName())
                .setData("platform_name", player.getName())
                .setData("platform_id", player.getUniqueId())
                .setData("health", player.getHealth())
                .setData("hunger", player.getFoodLevel())
                .setData("location", formatLocation(player.getLocation())));
    }

    // Location (internally text) Fragments
    public GameLog addLocationFragment(Location location, boolean highlight) {
        return this.addTextFragment(formatLocation(location), highlight);
    }
    public GameLog addLocationFragment(Location location) {
        return this.addLocationFragment(location, false);
    }

    // Item (internally text) Fragments
    public GameLog addItemFragment(ItemStack item, boolean highlight) {
        String itemName = item.getType().name();
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            if (meta.hasDisplayName())
                itemName = meta.getDisplayName();
        }
        itemName = item.getAmount() + "x " + itemName;

        StringBuilder enchantments = new StringBuilder();
        if(item.getEnchantments().isEmpty()) {
            enchantments.append("None");
        } else {
            for(Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                enchantments.append(entry.getKey().getKey()).append(" ").append(entry.getValue()).append(", ");
            }
        }

        return this.addRawFragment(new Fragment(Fragment.FragmentType.Text)
                .setData("text", itemName)
                .setData("color", highlight ? SquishLogs.highlightColor : null))
                .withMetadata("Item Type", item.getType().name())
                .withMetadata("Amount", String.valueOf(item.getAmount()))
                .withMetadata("Enchantments", enchantments.toString());
    }
    public GameLog addItemFragment(ItemStack item) {
        return this.addItemFragment(item, false);
    }

    public GameLog withMetadata(String title, String value) {
        this.fragments.get(this.fragments.toArray().length - 1).addMeta(title, value);
        return this;
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
            this.data.put("meta", new ArrayList<FragmentMeta>());
            this.type = type;
        }

        public Fragment setData(String key, Object val) {
            if(key.equals("meta")) {
                // dumbass trying to break the plugin
                SquishLogs.log("Warning: Fragment data key 'meta' is reserved.");
                return this;
            }
            if(val instanceof Color)
                val = formatFragmentColor((Color) val);
            this.data.put(key, val);
            return this;
        }
        @SuppressWarnings("unchecked") // suppressed cus its guaranteed to be
        public Fragment addMeta(String title, String text) {
            ((ArrayList<FragmentMeta>) this.data.get("meta")).add(new FragmentMeta(title, text));
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

        public static class FragmentMeta {
            private String title;
            private String text;

            public FragmentMeta(String title, String text) {
                this.title = title;
                this.text = text;
            }

            public String getTitle() {
                return title;
            }
            public String getText() {
                return text;
            }
        }
    }
}
