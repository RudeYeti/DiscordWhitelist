package io.github.rudeyeti.discordwhitelist.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import io.github.rudeyeti.discordwhitelist.Config;
import io.github.rudeyeti.discordwhitelist.DiscordWhitelist;
import io.github.rudeyeti.discordwhitelist.Player;
import io.github.rudeyeti.discordwhitelist.listeners.JDAListener;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class CheckCommand {
    public static void execute() {
        if (JDAListener.messageContent.startsWith(Config.command)) {
            String user = JDAListener.messageContent.replace(Config.command + " ", "");

            if (!user.equals(JDAListener.messageContent)) {
                if (Player.exists(user)) {
                    OfflinePlayer offlinePlayer = DiscordWhitelist.server.getOfflinePlayer(user);
                    String discordId = JDAListener.accountLinkManager.getDiscordId(offlinePlayer.getUniqueId());

                    if (discordId != null) {
                        Member member = DiscordWhitelist.guild.getMemberById(discordId);

                        JDAListener.textChannel.sendMessage("Discord Username: `" + member.getUser().getAsTag() + "`").queue();
                        return;
                    }
                } else {
                    UUID minecraftUuid = JDAListener.accountLinkManager.getUuid(user);

                    if (minecraftUuid != null) {
                        OfflinePlayer offlinePlayer = DiscordWhitelist.server.getOfflinePlayer(minecraftUuid);

                        JDAListener.textChannel.sendMessage("Minecraft Username: `" + offlinePlayer.getName() + "`").queue();
                        return;
                    }
                }
            } else {
                JDAListener.textChannel.sendMessage("Usage: `" + Config.command + " <discord-id | minecraft-username>`").queue();
                return;
            }

            JDAListener.textChannel.sendMessage("The specified user could not be found.").queue();
        }
    }
}
