package io.github.rudeyeti.discordwhitelist.commands.minecraft.discordwhitelist;

import io.github.rudeyeti.discordwhitelist.DiscordWhitelist;
import org.bukkit.command.CommandSender;

public class InfoSubcommand {
    public static void execute(CommandSender sender) {
        sender.sendMessage("General information:\n" +
                "Author - " + DiscordWhitelist.plugin.getDescription().getAuthors().get(0) + "\n" +
                "Version - " + DiscordWhitelist.plugin.getDescription().getVersion());
    }
}
