package fr.cocoraid.prodigyserver.utils;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GlowEntity {

    private static Map<UUID,String> glowed = new HashMap<>();


    public static void setGlow(List<Player> viewers, Player player, ChatColor color) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("GLOW_COLOR_" + String.valueOf(color.getChar()).toUpperCase());
        team.addEntry(player.getName());
        viewers.forEach(v -> {
            v.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        });
        player.setGlowing(true);
        glowed.put(player.getUniqueId(), team.getName());


    }



    public static void clearColorTeams() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : scoreboard.getTeams()) {
            if (team.getName().contains("GLOW_COLOR_")) {
                team.unregister();
            }
        }
    }

    public static void removeGlow(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setGlowing(false);

        if(glowed.containsKey(player.getUniqueId())){
            Team team = scoreboard.getTeam(glowed.get(player.getUniqueId()));
            team.removeEntry(player.getName());
            glowed.remove(player.getUniqueId());
        }

    }



    public static void setupColorTeams() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (ChatColor c : ChatColor.values()) {
            Team team = scoreboard.registerNewTeam("GLOW_COLOR_" + String.valueOf(c.getChar()).toUpperCase());
            team.setColor(c);
        }
    }
}
