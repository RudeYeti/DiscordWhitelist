package io.github.rudeyeti.discordwhitelist.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.discordwhitelist.DiscordWhitelist;

public class DiscordSRVListener {
    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new JDAListener());
        DiscordWhitelist.guild = DiscordSRV.getPlugin().getMainGuild();
    }
}
