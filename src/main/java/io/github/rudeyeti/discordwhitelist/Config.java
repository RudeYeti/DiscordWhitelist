package io.github.rudeyeti.discordwhitelist;

import org.bukkit.configuration.Configuration;
import java.util.List;

public class Config {

    public static Configuration config;
    public static String channelId;
    public static String command;
    public static boolean linkAccounts;
    public static boolean deleteOnLeave;
    public static List<String> blacklist;

    public static void updateConfig() {
        config = DiscordWhitelist.plugin.getConfig();
        channelId = config.getString("channel-id");
        command = config.getString("command");
        linkAccounts = config.getBoolean("link-accounts");
        deleteOnLeave = config.getBoolean("delete-on-leave");
        blacklist = config.getStringList("blacklist");
    }

    private static String message(String option, String message) {
        return "The " + option + " value in the configuration must be " + message;
    }

    public static boolean validateConfig() {
        if (!(config.get("channel-id") instanceof String)) {
            DiscordWhitelist.logger.warning(message("channel-id", "enclosed in quotes."));
        } else if (config.get("channel-id").equals("##################")) {
            DiscordWhitelist.logger.warning(message("channel-id", "modified from ##################."));
        } else if (!(config.get("command") instanceof String)) {
            DiscordWhitelist.logger.warning(message("command", "enclosed in quotes."));
        } else if (!(config.get("link-accounts") instanceof Boolean)) {
            DiscordWhitelist.logger.warning(message("link-accounts", "either true or false."));
        } else if (!(config.get("delete-on-leave") instanceof Boolean)) {
            DiscordWhitelist.logger.warning(message("delete-on-leave", "either true or false."));
        } else if (!(config.get("blacklist") instanceof List)) {
            DiscordWhitelist.logger.warning(message("blacklist", "a list with entries enclosed in quotes."));
        } else {
            return true;
        }

        return false;
    }
}
