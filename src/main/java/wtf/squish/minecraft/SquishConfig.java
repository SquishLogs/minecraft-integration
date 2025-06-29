package wtf.squish.minecraft;

import org.bukkit.configuration.file.FileConfiguration;

public class SquishConfig {
    private final String community;
    private final String domain;
    private final String token;

    public SquishConfig(SquishLogs instance) {
        FileConfiguration config = instance.getConfig();
        this.community = config.getString("community");
        this.domain = config.getString("domain");
        this.token = config.getString("token");
    }

    public String getCommunity() {
        return this.community;
    }
    public String getDomain() {
        return this.domain;
    }
    public String getToken() {
        return this.token;
    }
}