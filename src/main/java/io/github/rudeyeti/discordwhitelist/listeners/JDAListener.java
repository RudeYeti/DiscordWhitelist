package io.github.rudeyeti.discordwhitelist.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.discordwhitelist.Config;
import io.github.rudeyeti.discordwhitelist.DiscordWhitelist;
import io.github.rudeyeti.discordwhitelist.Player;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class JDAListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getGuild() == DiscordWhitelist.guild) {
            Message message = event.getMessage();
            String messageContent = message.getContentRaw();
            AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();

            if (event.getChannel().getId().equals(Config.channelId)) {
                if (Player.exists(messageContent)) {
                    OfflinePlayer offlinePlayer = DiscordWhitelist.server.getOfflinePlayer(messageContent);

                    if (offlinePlayer.isWhitelisted()) {
                        message.addReaction("🤷").queue();
                    } else {
                        offlinePlayer.setWhitelisted(true);
                        DiscordWhitelist.server.reloadWhitelist();
                        message.addReaction("✅").queue();

                        if (Config.linkAccounts) {
                            accountLinkManager.link(event.getAuthor().getId(), offlinePlayer.getUniqueId());
                        }
                    }
                } else {
                    message.delete().queue();
                }
            } else if (messageContent.startsWith(Config.command)) {
                if (messageContent.equals(Config.command) || !messageContent.startsWith(Config.command + " ")) {
                    message.getTextChannel().sendMessage("**Usage:** `" + Config.command + " <discord-id | minecraft-username>`").queue();
                } else {
                    String user = messageContent.replace(Config.command + " ", "");

                    try {
                        DiscordUtil.getJda().getUserById(user);

                        UUID minecraftUuid = accountLinkManager.getUuid(user);

                        if (minecraftUuid != null) {
                            OfflinePlayer offlinePlayer = DiscordWhitelist.server.getOfflinePlayer(minecraftUuid);

                            if (Player.exists(offlinePlayer.getName()) && offlinePlayer.getName() != null) {
                                message.getTextChannel().sendMessage("**Minecraft Username:** `" + offlinePlayer.getName() + "`").queue();
                                return;
                            }
                        }
                    } catch (NumberFormatException error) {
                        OfflinePlayer offlinePlayer = DiscordWhitelist.server.getOfflinePlayer(user);
                        String discordId = accountLinkManager.getDiscordId(offlinePlayer.getUniqueId());

                        if (discordId != null) {
                            Member member = DiscordWhitelist.guild.getMemberById(discordId);

                            if (member != null) {
                                message.getTextChannel().sendMessage("**Discord Username:** `" + member.getUser().getAsTag() + "`").queue();
                                return;
                            }
                        }
                    }

                    message.getTextChannel().sendMessage("The specified user could not be found.").queue();
                }
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

                    if (Player.exists(message.getContentRaw()) && offlinePlayer.isWhitelisted()) {
                        offlinePlayer.setWhitelisted(false);
                        DiscordWhitelist.server.reloadWhitelist();
                        message.removeReaction("✅").queue();
                        message.addReaction("❌").queue();

                        if (Config.linkAccounts) {
                            DiscordSRV.getPlugin().getAccountLinkManager().unlink(event.getUser().getId());
                        }
                    }
                }

                return true;
            });
        }
    }
}
