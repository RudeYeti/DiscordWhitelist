package io.github.rudeyeti.discordwhitelist.commands.minecraft.discordwhitelist;

import io.github.rudeyeti.discordwhitelist.Config;
import io.github.rudeyeti.discordwhitelist.DiscordWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

public class ReloadSubcommand {
    public static void execute(CommandSender sender) {
        if (sender.hasPermission("discordwhitelist.reload") || sender.isOp()) {
            Configuration oldConfig = Config.config;
            DiscordWhitelist.plugin.reloadConfig();
            DiscordWhitelist.server.reloadWhitelist();
            Config.config = DiscordWhitelist.plugin.getConfig();

            if (!Config.validateConfig()) {
                Config.config = oldConfig;
                Config.updateConfig();
                sender.sendMessage("The configuration was invalid, reverting back to the previous state.");
            } else {
                Config.updateConfig();
                sender.sendMessage("The plugin has been successfully reloaded.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: You are missing the correct permission to perform this command.");
        }
    }
}