package io.github.rudeyeti.discordwhitelist;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Player {
    public static boolean exists(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            URLConnection connection = url.openConnection();
            connection.getContent().toString().isEmpty();
        } catch (IOException | NullPointerException error) {
            return false;
        }
        return true;
    }
}
