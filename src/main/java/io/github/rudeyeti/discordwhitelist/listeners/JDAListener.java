package io.github.rudeyeti.discordwhitelist.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import io.github.rudeyeti.discordwhitelist.Config;
import io.github.rudeyeti.discordwhitelist.DiscordWhitelist;
import io.github.rudeyeti.discordwhitelist.Player;
import io.github.rudeyeti.discordwhitelist.commands.discord.CheckCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.util.concurrent.TimeUnit;

public class JDAListener extends ListenerAdapter {

    public static Message message;
    public static TextChannel textChannel;
    public static String messageContent;
    public static AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getGuild() == DiscordWhitelist.guild && !event.getAuthor().isBot()) {
            message = event.getMessage();
            textChannel = message.getTextChannel();
            messageContent = message.getContentRaw();

            if (event.getChannel().getId().equals(Config.channelId)) {
                for (String string : Config.blacklist) {
                    if (string.equals(messageContent)) {
                        message.delete().queue();
                        textChannel.sendMessage("The specified user `" + messageContent + "` is blacklisted.").queue((message) -> {
                            message.delete().queueAfter(3, TimeUnit.SECONDS);
                        });
                        return;
                    }
                }

                if (Player.exists(messageContent)) {
                    OfflinePlayer offlinePlayer = DiscordWhitelist.server.getOfflinePlayer(messageContent);

                    if (offlinePlayer.isWhitelisted()) {
                        message.delete().queue();
                        textChannel.sendMessage("The specified user `" + messageContent + "` is already whitelisted.").queue((message) -> {
                            message.delete().queueAfter(3, TimeUnit.SECONDS);
                        });
                    } else {
                        offlinePlayer.setWhitelisted(true);
                        DiscordWhitelist.server.reloadWhitelist();
                        message.addReaction("✅").queue();
                    }

                    if (Config.linkAccounts) {
                        if (DiscordSRV.config().getBoolean("GroupRoleSynchronizationOnLink")) {
                            try {
                                File dataFolder = DiscordSRV.getPlugin().getDataFolder();
                                File file = new File(dataFolder, "synchronization.yml");
                                File backup = new File(dataFolder, "synchronization.yml.old");
                                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                                Files.copy(file.toPath(), backup.toPath());
                                config.set("GroupRoleSynchronizationOnLink", false);
                                config.save(file);
                                DiscordSRV.getPlugin().reloadConfig();

                                accountLinkManager.link(event.getAuthor().getId(), offlinePlayer.getUniqueId());

                                Files.delete(file.toPath());
                                backup.renameTo(file);
                                DiscordSRV.getPlugin().reloadConfig();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            accountLinkManager.link(event.getAuthor().getId(), offlinePlayer.getUniqueId());
                        }
                    }
                } else {
                    message.delete().queue();
                    textChannel.sendMessage("The specified user `" + messageContent + "` is not an actual player.").queue((message) -> {
                        message.delete().queueAfter(3, TimeUnit.SECONDS);
                    });
                }
            } else {
                CheckCommand.execute();
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (event.getMember().getGuild() == DiscordWhitelist.guild) {
            TextChannel channel = DiscordWhitelist.guild.getTextChannelById(Config.channelId);

            channel.getIterableHistory().cache(false).forEachAsync((message) -> {

                if (message.getAuthor().getId().equals(event.getUser().getId())) {
                    OfflinePlayer offlinePlayer = DiscordWhitelist.server.getOfflinePlayer(message.getContentRaw());

                    if (Player.exists(message.getContentRaw())) {
                        offlinePlayer.setWhitelisted(false);
                        DiscordWhitelist.server.reloadWhitelist();

                        if (Config.linkAccounts) {
                            accountLinkManager.unlink(event.getUser().getId());
                        }
                    }

                    if (Config.deleteOnLeave) {
                        message.delete().queue();
                    } else {
                        message.getReactions().forEach((reaction) ->
                                reaction.removeReaction().queue()
                        );
                        message.addReaction("❌").queue();
                    }
                }

                return true;
            });
        }
    }
}
