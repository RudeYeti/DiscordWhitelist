package io.github.rudeyeti.discordwhitelist;

import org.bukkit.configuration.Configuration;

public class Config {

    public static Configuration config;
    public static String channelId;

    public static void updateConfig() {
        config = DiscordWhitelist.plugin.getConfig();
        channelId = config.getString("channel-id");
    }

    public static boolean validateConfig() {
        String channelIdMessage = "The channel-id value in the configuration must be ";

        if (!(config.get("channel-id") instanceof String)) {
            DiscordWhitelist.logger.warning(channelIdMessage + "enclosed in quotes.");
        } else if (config.get("channel-id").equals("##################")) {
            DiscordWhitelist.logger.warning(channelIdMessage + "modified from ##################.");
        } else {
            return true;
        }

        return false;
    }
}
