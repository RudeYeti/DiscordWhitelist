package io.github.rudeyeti.discordwhitelist.listeners;

import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import io.github.rudeyeti.discordwhitelist.Config;
import io.github.rudeyeti.discordwhitelist.DiscordWhitelist;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class JDAListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getGuild() == DiscordWhitelist.guild && event.getChannel().getId().equals(Config.channelId)) {
            String message = event.getMessage().getContentRaw();
            OfflinePlayer offlinePlayer = DiscordWhitelist.server.getOfflinePlayer(message);

            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + message);
                URLConnection connection = url.openConnection();
                connection.getContent().toString().isEmpty();
            } catch (IOException | NullPointerException error) {
                event.getMessage().delete().queue();
                return;
            }

            if (!offlinePlayer.isWhitelisted()) {
                offlinePlayer.setWhitelisted(true);
                DiscordWhitelist.server.reloadWhitelist();
            }
        }
    }
}
