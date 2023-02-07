package wtf.squish.minecraft.entities;

import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.squish.minecraft.SquishLogs;
import wtf.squish.minecraft.util.Output;

import java.awt.*;
import java.io.IOException;
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
     * Adds a text fragment with a highlight.
     * @param string The text.
     * @param highlight If to highlight the text or not.
     * @return The log object.
     */
    public Log addFragment(String string, boolean highlight) {
        return this.addFragment(new Fragment(string, (highlight ? SquishLogs.getLogColor() : null)));
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
     * Adds a number fragment with a highlight.
     * @param number The number.
     * @param highlight If to highlight the text or not.
     * @return The log object.
     */
    public Log addFragment(int number, boolean highlight) {
        return this.addFragment(new Fragment(number, (highlight ? SquishLogs.getLogColor() : null)));
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
     * Adds a location fragment with a color.
     * @param location The location.
     * @param color The color.
     * @return The log object.
     */
    public Log addFragment(Location location, Color color) {
        return this.addFragment(new Fragment(location, color));
    }

    /**
     * Adds a number fragment with a highlight.
     * @param location The location.
     * @param highlight If to highlight the text or not.
     * @return The log object.
     */
    public Log addFragment(Location location, boolean highlight) {
        return this.addFragment(new Fragment(location, (highlight ? SquishLogs.getLogColor() : null)));
    }

    /**
     * Adds a location fragment.
     * @param location The location.
     * @return The log object.
     */
    public Log addFragment(Location location) {
        return this.addFragment(new Fragment(location, null));
    }

    /**
     * Adds a piece of metadata to the previously added fragment.
     * @param title The title of the metadata.
     * @param text The text of the metadata.
     * @return The log object.
     */
    public Log addMetadata(String title, String text) {
        this.fragments.get(this.fragments.size() - 1).addFragmentMeta(title, text);
        return this;
    }

    /**
     * Creates x, y and z metadata entries from a location object.
     * @param location The location.
     * @return The log object.
     */
    public Log addLocationMetadata(Location location) {
        addMetadata("x", String.valueOf(location.getBlockX()));
        addMetadata("y", String.valueOf(location.getBlockY()));
        addMetadata("z", String.valueOf(location.getBlockZ()));
        return this;
    }

    /**
     * Sends the log to the server.
     */
    public void send() {
        Gson gson = new Gson();

        // i blame owain
        for(Fragment fragment : this.fragments) {
            if(fragment.getMeta().isEmpty()) continue;
            fragment.addData("meta", fragment.getMeta());
        }

        HashMap<String, Object> values = new HashMap<>();
        values.put("type", "log");
        values.put("category", this.category);
        values.put("log", this.fragments);

        String log = gson.toJson(values);
        SquishLogs.getWebSocket().send(log);

        if(SquishLogs.getServerInfo().getDiscordWebhook() != null) {
            // Discord time
            DiscordWebhook webhook = new DiscordWebhook(SquishLogs.getServerInfo().getDiscordWebhook());
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
            embed.setTitle(this.category);
            embed.setColor(Color.decode(SquishLogs.getServerInfo().getColor()));
            embed.setFooter("Powered by Squish Logs", "");

            StringBuilder stringLog = new StringBuilder();
            for(Fragment fragment : this.fragments) {
                switch(fragment.getType()) {
                    case TEXT -> stringLog.append(fragment.getData().get("text"));
                    case PLAYER -> {
                        stringLog.append(fragment.getData().get("name"));
                        embed.addField(fragment.getData().get("name").toString(),
                                "Health: " + fragment.getData().get("health")
                                        + " | Hunger: " + fragment.getData().get("hunger")
                                        + " | Location: " + fragment.getData().get("location"),
                                false);
                    }
                }
            }
            embed.setDescription(stringLog.toString());
            webhook.addEmbed(embed);
            try {
                webhook.execute();
            } catch(IOException e) {
                Output.print("Failed to send discord webhook:");
                e.printStackTrace();
            }
        }
    }

    // getters/setters
    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public ArrayList<Fragment> getFragments() {return fragments;}
    public void setFragments(ArrayList<Fragment> fragments) {this.fragments = fragments;}
}
